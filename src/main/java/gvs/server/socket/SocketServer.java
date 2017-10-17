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
import java.net.URL;
import java.net.UnknownHostException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.common.Configuration;

/**
 * Socket communciation for GVS. For each client a new server-connection will be
 * created. This class is the entry point for socket-connections.
 * 
 * @author mkoller
 */
public class SocketServer extends Thread {

  private int port = 0;
  private int startPort = 0;
  private String hostName;
  private ServerSocket socket = null;
  private Socket client = null;

  private String communicationfilepath = null;
  private InetAddress inetAdress = null;

  private File portFile = null;
  private File portFileCurent = null;

  private Logger serverLogger = null;

  /**
   * Searches for free port and writes the communication information to a file
   */
  public SocketServer() {
    // TODO check logger replacement
    // serverLogger=gvs.common.Logger.getInstance().getServerLogger();
    serverLogger = LoggerFactory.getLogger(SocketServer.class);
    try {
      inetAdress = InetAddress.getLocalHost();
    } catch (UnknownHostException e1) {
      e1.printStackTrace();
    }

    hostName = inetAdress.getHostName();
    communicationfilepath = Configuration.getInstance().getCommFilePath();
    startPort = Integer.parseInt(Configuration.getInstance().getStartPort());

    // Search for free port
    for (int searchPort = startPort; searchPort <= 60000; searchPort++) {
      try (Socket portScanSocket = new Socket()) {
        InetSocketAddress isa = new InetSocketAddress("localhost", searchPort);
        int timeout = 1; // in ms
        portScanSocket.connect(isa, timeout);
      } catch (Exception e) {
        port = searchPort;
        break;
      }
    }

    // Portfile on Share
    if (communicationfilepath != null || communicationfilepath != ""
        || !communicationfilepath.equals("GVSComm.xml")) {
      serverLogger.info("Create shared communication file");
      portFile = new File(communicationfilepath);
      Document document = DocumentHelper.createDocument();
      Element docRoot = document.addElement("GVSServer");
      Element ePort = docRoot.addElement("Port");
      Element eHost = docRoot.addElement("Host");
      ePort.addText(String.valueOf(port));
      eHost.addText(hostName);

      OutputFormat format = OutputFormat.createPrettyPrint();
      try {
        XMLWriter writer = new XMLWriter(new FileOutputStream(portFile),
            format);
        writer.write(document);
        serverLogger
            .info("Shared communication file: " + communicationfilepath);
      } catch (Exception e) {

        serverLogger.error("Failed to share communication File");
      }
    } else {
      // Portfile current directory
      serverLogger.info("Create local communication file");
      try {

        URL portFileUrl = SocketServer.class.getClassLoader()
            .getResource("GVSComm.xml");

        portFileCurent = new File(portFileUrl.toURI());
        Document document = DocumentHelper.createDocument();
        Element docRoot = document.addElement("GVSServer");
        Element ePort = docRoot.addElement("Port");
        Element eHost = docRoot.addElement("Host");
        ePort.addText(String.valueOf(port));
        eHost.addText(hostName);

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writerCurrent = new XMLWriter(
            new FileOutputStream(portFileCurent), format);
        writerCurrent.write(document);

        serverLogger.info("Local communication file: " + portFileCurent);

      } catch (Exception e) {
        serverLogger.error("Failed to create local communication File", e);
      }
    }
  }

  /**
   * Starts the server. Once started, it runs endlessly.
   */
  public void run() {
    try {
      socket = new ServerSocket(port);
      while (true) {
        serverLogger.info("Server ready: " + hostName + " Port: " + port);
        client = socket.accept();
        ServerConnectionXML con = new ServerConnectionXML(client);
        con.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
