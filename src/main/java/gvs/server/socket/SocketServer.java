/*
 * Created on 12.11.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gvs.server.socket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gvs.access.ServerConnectionXMLFactory;
import gvs.common.Configuration;

/**
 * Socket Endpoint of GVS UI. The server is running in its own thread.
 * 
 * For each incoming request, a new {@link ServerConnectionXML} is created.
 * 
 * @author mwieland
 */
public class SocketServer extends Thread {

  private String hostname;
  private Integer port;
  private ServerConnectionXMLFactory connectionFactory;

  private static final String DEFAULT_PORT_FILE_NAME = "GVSComm.xml";

  private static final Logger logger = LoggerFactory
      .getLogger(SocketServer.class);;

  /**
   * Searches for free port and writes the communication information to a file
   */
  @Inject
  public SocketServer(ServerConnectionXMLFactory factory) {
    this.connectionFactory = factory;
    this.hostname = getLocalHostName();
    this.port = findFreePort();

    writePortFile();
  }

  /**
   * Starts the server. Once started, it runs endlessly.
   */
  public void run() {
    try (ServerSocket javaSocket = new ServerSocket(port)) {
      logger.info("Server is running on {}:{}", hostname, port);

      while (true) {
        Socket client = javaSocket.accept();
        
        ServerConnectionXML con = connectionFactory.create(client);
        con.start();
      }
    } catch (IOException e) {
      logger.error("Cannot create ServerSocket", e);
    }
  }

  private String getLocalHostName() {
    try {
      InetAddress.getLocalHost().getHostName();
      return "localhost";
    } catch (UnknownHostException e) {
      logger.error("Cannot retrieve local host address", e);
      return null;
    }
  }

  private Integer findFreePort() {
    String startPortString = Configuration.getInstance().getStartPort();
    int configuredStartPort = Integer.parseInt(startPortString);

    for (int searchPort = configuredStartPort; searchPort <= 60000; searchPort++) {
      try (Socket portScanSocket = new Socket()) {
        InetSocketAddress endpointAddress = new InetSocketAddress(
            getLocalHostName(), searchPort);
        int timeout = 1; // in ms
        portScanSocket.connect(endpointAddress, timeout);
      } catch (Exception e) {
        logger.info("Free port found at {}", searchPort);
        return searchPort;
      }
    }
    return null;
  }

  private void writePortFile() {
    String filePath = Configuration.getInstance().getCommFilePath();

    if (filePath != null && !filePath.isEmpty()
        && !DEFAULT_PORT_FILE_NAME.equals(filePath)) {

      logger.info("Write file to configured location");
      writeFile(new File(filePath));
    } else {

      logger.info("Write file to current directory");
      writeFile(new File(DEFAULT_PORT_FILE_NAME));
    }
  }

  private void writeFile(File portFile) {
    Document document = DocumentHelper.createDocument();
    Element docRoot = document.addElement("GVSServer");
    Element ePort = docRoot.addElement("Port");
    Element eHost = docRoot.addElement("Host");
    ePort.addText(String.valueOf(port));
    eHost.addText(hostname);

    OutputFormat format = OutputFormat.createPrettyPrint();
    XMLWriter writerCurrent;
    try {
      writerCurrent = new XMLWriter(new FileOutputStream(portFile), format);
      writerCurrent.write(document);
      logger.info("File {} successfully written. ", portFile);
    } catch (IOException e) {
      logger.info("Unable to write file {}", portFile, e);
    }
  }
}
