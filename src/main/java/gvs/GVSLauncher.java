package gvs;

import gvs.server.socket.SocketServer;
import gvs.ui.application.controller.GVSApplication;
import gvs.ui.application.view.ApplicationView;

/**
 * Starts the Graphs-Visualization Service.
 * 
 * @author aegli
 */
public class GVSLauncher {

  /**
   * Main method. 
   * @param args console arguments
   */
  public static void main(String[] args) {

    ApplicationView av = new ApplicationView();
    new Thread(() ->  GVSApplication.launch(GVSApplication.class)).start();

    av.setVisible(true);
    try {
      SocketServer server = new SocketServer();
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
