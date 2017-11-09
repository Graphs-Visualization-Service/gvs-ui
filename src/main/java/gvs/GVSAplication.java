package gvs;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.access.SocketServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Starts the Graphs-Visualization Service. It loads the application frame and
 * starts the socket server.
 * 
 * @author mtrentin
 */
@Singleton
public class GVSAplication extends Application {

  @Inject
  private SocketServer socketServer;

  @Inject
  private FXMLLoader fxmlLoader;

  private Stage primaryStage;
  private BorderPane rootLayout;

  private final GuiceContext context = new GuiceContext(this,
      () -> Arrays.asList(new GuiceBaseModule()));

  private static final Logger logger = LoggerFactory
      .getLogger(GVSAplication.class);

  private static final String APP_NAME = "GVS";
  private static final int MIN_WIDTH = 800;
  private static final int MIN_HEIGHT = 600;

  /**
   * Main method.
   * 
   * @param args
   *          console arguments
   */
  public static void main(String[] args) {
    Application.launch(GVSAplication.class);
  }

  /**
   * Launch the socket server in a separate thread. <br>
   * Launch the JavaFX application within UI thread.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    logger.info("Initialize Guice injector");
    context.init();

    logger.info("Start Server Socket...");
    socketServer.start();

    logger.info("Start GVS UI 2.0...");
    setUpFrame(primaryStage);
  }

  /**
   * Set up the main frame
   * 
   * @param mainStage
   *          main stage
   */

  public void setUpFrame(Stage mainStage) {
    primaryStage = mainStage;
    primaryStage.setMinHeight(MIN_HEIGHT);
    primaryStage.setMinWidth(MIN_WIDTH);
    primaryStage.setTitle(APP_NAME);

    primaryStage.setOnCloseRequest(e -> {
      Platform.exit();
      System.exit(0);
    });

    initRootLayout();
  }

  /**
   * Loads the application fxml and shows the application frame.
   */
  private void initRootLayout() {
    try {
      logger.debug("Initialize root layout");
      fxmlLoader
          .setLocation(getClass().getResource("/gvs/ui/view/app/AppView.fxml"));

      rootLayout = (BorderPane) fxmlLoader.load();

      // Show the scene containing the root layout.
      Scene scene = new Scene(rootLayout);
      primaryStage.setScene(scene);
      primaryStage.sizeToScene();
      primaryStage.show();
    } catch (IOException e) {
      logger.error("Could not initialize root layout", e);
    }
  }
}
