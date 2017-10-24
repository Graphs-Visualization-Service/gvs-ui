package gvs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.server.socket.SocketServer;
import gvs.ui.application.controller.GVSApplication;

/**
 * Starts the Graphs-Visualization Service.
 * 
 * @author mwieland
 */
public class GVSLauncher {

  private static final Logger logger = LoggerFactory
      .getLogger(GVSLauncher.class);

  /**
   * Main method.
   * 
   * @param args
   *          console arguments
   */
  public static void main(String[] args) {
    try {
      logger.info("Start GVS UI 2.0...");
      GVSApplication.launch(GVSApplication.class);
      logger.info("GVS UI started.");

      logger.info("Start Server Socket...");
      SocketServer server = new SocketServer();
      server.start();
      logger.info("Sever Socket started.");
    } catch (Exception e) {
      logger.error("Unable to start GVS UI", e);
    }
  }
}
