package gvs.server.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.access.InputXmlReader;
import gvs.access.InputXmlWriter;
import gvs.access.ProtocolCommand;
import gvs.server.ConnectionMonitor;

/**
 * This class is the endpoint for each incoming connection.
 * 
 * The class handles protocol commands and stores the incoming data in a xml
 * file.
 * 
 * @author mwieland
 */
public class ServerConnectionXML extends Thread {

  private ConnectionMonitor monitor;
  private InputXmlReader inputXmlReader;
  private InputXmlWriter inputXmlWriter;

  private Socket socketClient;

  // protocol messages
  private static final String OK = "OK";
  private static final String FAILED = "FAILED";

  private static final String THREAD_NAME = "Server Connection Thread";

  private static final Logger logger = LoggerFactory
      .getLogger(ServerConnectionXML.class);

  /**
   * Default constructor.
   * 
   * @param client
   *          incoming client connection.
   * @param monitor
   *          monitor to reserve the GVS service
   * @param inputXmlWriter
   *          xml writer used to store the incoming data locally
   * @param inputXmlReader
   *          xml reader used to read the created xml
   */
  @Inject
  public ServerConnectionXML(ConnectionMonitor monitor,
      InputXmlReader inputXmlReader, InputXmlWriter inputXmlWriter,
      @Assisted Socket client) {

    super(THREAD_NAME);
    this.socketClient = client;
    this.inputXmlReader = inputXmlReader;
    this.monitor = monitor;
    this.inputXmlWriter = inputXmlWriter;
  }

  /**
   * Read the incoming protocol messages and interpret them. Try to get the
   * monitor lock of {@link ConnectionMonitor} If successful, read the
   * transfered data and store it locally in a XML file.
   */
  public void run() {
    try {
      processInputStream(socketClient.getInputStream());

      inputXmlReader.read();

      socketClient.close();
    } catch (IOException e) {
      logger.error("Unable to read incoming message of client {}",
          socketClient.getInetAddress(), e);
    } finally {
      releaseService();
    }
  }

  /**
   * Read the input line by line.
   * 
   * @param iStream
   *          input stream of the client connection
   * @throws IOException
   *           error while reading a line
   */
  private void processInputStream(InputStream iStream) throws IOException {
    BufferedReader inputReader = new BufferedReader(
        new InputStreamReader(iStream));

    String line = null;
    while ((line = inputReader.readLine()) != null) {
      processLine(line);
    }
  }

  /**
   * Case distinctions for the input
   * 
   * @param line
   *          incoming line
   * 
   * @throws IOException
   *           I/O error
   */
  private void processLine(String line) throws IOException {
    if (line.equals(ProtocolCommand.RESERVE_GVS.toString())) {
      logger.info("Reserve command detected.");
      reserveService();
    } else if (line.equals(ProtocolCommand.RELEASE_GVS.toString())) {
      logger.info("Release command detected.");
      releaseService();
    } else {
      logger.info("Data detected");
      writeXmlFile(line);
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
    int status = monitor.reserveService(remoteHost);
    if (status >= 0) {
      logger.info("Service reserved.");
      sendMessage(OK);
    } else {
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
    logger.info("Service released: " + remoteHost);
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
   * Store the incoming data locally.
   * 
   * @param line
   *          data line
   */
  private void writeXmlFile(String line) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < line.length(); i++) {
      if ((line.charAt(i)) == ProtocolCommand.DATA_END.toString().charAt(0)) {
        String output = stringBuffer.toString().trim();
        inputXmlWriter.write(output);
      } else {
        stringBuffer.append(line.charAt(i));
      }
    }
  }
}
