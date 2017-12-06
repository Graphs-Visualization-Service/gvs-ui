package gvs.access;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.Configuration;

/**
 * This class is the endpoint for each incoming connection.
 * 
 * The class handles protocol commands and stores the incoming data in a xml
 * file.
 * 
 * @author mwieland
 */
public class ClientConnection extends Thread {

  private final Socket socketClient;
  private final ConnectionMonitor monitor;
  private final Watchdog watchdog;
  private final ModelBuilder modelBuilder;

  private final GvsXmlReader xmlReader;
  private Thread watchdogThread;

  // protocol messages
  private static final String OK = "OK";
  private static final String FAILED = "FAILED";

  private static final String THREAD_NAME = "Client Connection Thread";

  private static final Logger logger = LoggerFactory
      .getLogger(ClientConnection.class);

  /**
   * Default constructor.
   * 
   * @param client
   *          incoming client connection.
   * @param modelBuilder
   *          modelbuilder which processes the parsed xml.
   * @param monitor
   *          monitor to reserve the GVS service
   * @param watchdog
   *          connection watchdog
   * @param xmlReaderFactory
   *          xml reader used to read the created xml
   */
  @Inject
  public ClientConnection(ConnectionMonitor monitor, ModelBuilder modelBuilder,
      Watchdog watchdog, GvsXmlReader gvsXmlReader,
      @Assisted Socket client) {

    super(THREAD_NAME);

    this.modelBuilder = modelBuilder;
    this.socketClient = client;
    this.watchdog = watchdog;
    this.monitor = monitor;
    this.xmlReader = gvsXmlReader;
  }

  /**
   * Read the incoming protocol messages and interpret them. Try to get the
   * monitor lock of {@link ConnectionMonitor} If successful, read the
   * transfered data and store it locally in a XML file.
   */
  @Override
  public void run() {
    processInputStream();
  }

  /**
   * Read the input line by line.
   * 
   */
  private void processInputStream() {

    try (BufferedReader inputReader = new BufferedReader(
        new InputStreamReader(socketClient.getInputStream()))) {

      StringBuffer data = new StringBuffer();
      String line;
      while ((line = inputReader.readLine()) != null) {
        if (watchdog.isWatching()) {
          watchdog.feed();
        }

        int endCharIndex = line.indexOf(ProtocolCommand.DATA_END.toString());

        if (line.equals(ProtocolCommand.RESERVE_GVS.toString())) {
          logger.info("Reserve command detected.");
          watchdogThread = new Thread(watchdog);
          watchdogThread.setName(Configuration.getWatchdog());
          watchdogThread.start();
          reserveService();
        } else if (line.equals(ProtocolCommand.RELEASE_GVS.toString())) {
          logger.info("Release command detected.");
          watchdog.stopWatching();
          watchdogThread.join();
          releaseService();
          break;
        } else if (endCharIndex != -1) {
          logger.info("End of data detected.");
          data.append(line.substring(0, endCharIndex));
          readAndTransformModel(
              new ByteArrayInputStream(data.toString().getBytes()));
          data.setLength(0);
        } else {
          logger.info("Data detected");
          data.append(line);
        }
      }
    } catch (IOException | InterruptedException e) {
      logger.error("Unable to read incoming message of client {}",
          socketClient.getInetAddress(), e);
    }
  }

  /**
   * Try to get the monitor lock and send status message back to the client.
   * 
   * @throws IOException
   *           I/O error occurred when creating the output stream
   */
  private void reserveService() throws IOException {
    String remoteHost = socketClient.getRemoteSocketAddress().toString();
    try {
      monitor.reserveService(remoteHost);
      logger.info("Service reserved.");
      sendMessage(OK);
    } catch (InterruptedException e) {
      logger.info("Service busy.");
      sendMessage(FAILED);
    }
  }

  /**
   * Release monitor lock.
   */
  private void releaseService() {
    String remoteHost = socketClient.getRemoteSocketAddress().toString();
    monitor.releaseService(remoteHost);
  }

  /**
   * Send a message to the connected client
   * 
   * @param message
   *          hard coded message
   * 
   * @throws IOException
   *           I/O error occurred when creating the output stream
   */
  private void sendMessage(String message) throws IOException {
    PrintStream outStream = new PrintStream(socketClient.getOutputStream(),
        true);
    outStream.println(message);
  }

  /**
   * Read the just written xml file and transform to graph model.
   */
  private void readAndTransformModel(InputStream input) {
    logger.info("Build model from parsed xml");
    Document document = xmlReader.read(input);
    if (document != null) {
      modelBuilder.buildModelFromXML(document);
    } else {
      logger.warn("XmlReader could not read xml file.");
    }

  }
}
