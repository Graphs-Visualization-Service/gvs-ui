package gvs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import gvs.access.SocketServer;
import gvs.ui.logic.app.GVSApplication;

/**
 * Starts the Graphs-Visualization Service.
 * 
 * @author mwieland
 */
@Singleton
public class GVSLauncher {

  private final SocketServer socketServer;

  private static final Logger logger = LoggerFactory
      .getLogger(GVSLauncher.class);

  @Inject
  public GVSLauncher(SocketServer socketServer) {
    this.socketServer = socketServer;
  }

  /**
   * Launch the socket server in a separate thread. <br>
   * Launch the JavaFX application within UI thread.
   */
  private void launch() {
    logger.info("Start Server Socket...");
    socketServer.start();

    logger.info("Start GVS UI 2.0...");
    GVSApplication.launch(GVSApplication.class);
  }

  /**
   * Main method.
   * 
   * @param args
   *          console arguments
   */
  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new GuiceBaseModule());
    GVSLauncher launcher = injector.getInstance(GVSLauncher.class);
    launcher.launch();
  }
}
