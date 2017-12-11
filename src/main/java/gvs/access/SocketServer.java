/*
 * Created on 12.11.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gvs.access;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gvs.Configuration;

/**
 * Socket Endpoint of GVS UI. The server is running in its own thread.
 * 
 * For each incoming request, a new {@link ClientConnection} is created.
 * 
 * @author mwieland
 */
public class SocketServer extends Thread {

  private final String hostname;
  private final Integer port;

  private final Configuration configuration;
  private final ClientConnectionFactory connectionFactory;

  private static final String THREAD_NAME = "Socket Server Thread";

  private static final Logger logger = LoggerFactory
      .getLogger(SocketServer.class);;

  /**
   * Searches for free port and writes the communication information to a file
   * 
   * @param factory
   *          factory for incomming client connection
   * @param configuration
   *          configuration wrapper
   */
  @Inject
  public SocketServer(ClientConnectionFactory factory,
      Configuration configuration) {

    super(THREAD_NAME);
    this.connectionFactory = factory;
    this.configuration = configuration;

    this.hostname = getLocalHostName();
    this.port = findFreePort();
  }

  /**
   * Starts the server. Once started, it runs endlessly.
   */
  @Override
  public void run() {
    try (ServerSocket javaSocket = new ServerSocket(port)) {
      logger.info("Server is running on {}:{}", hostname, port);

      while (true) {
        Socket client = javaSocket.accept();

        ClientConnection connection = connectionFactory.create(client);
        connection.start();
      }
    } catch (IOException e) {
      logger.error("Cannot open Server Socket. Service may already be running.",
          e);
    }
  }

  private String getLocalHostName() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      logger.error("Cannot retrieve local host address", e);
      return null;
    }
  }

  private Integer findFreePort() {
    String startPortString = configuration.getStartPort();
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
}
