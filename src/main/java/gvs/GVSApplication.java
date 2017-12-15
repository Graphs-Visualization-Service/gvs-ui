package gvs;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.access.SocketServer;
import gvs.util.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Starts the Graphs-Visualization Service. It loads the application frame and
 * starts the socket server.
 * 
 * @author mtrentin
 */
@Singleton
public class GVSApplication extends Application {

  @Inject
  private SocketServer socketServer;

  @Inject
  private FXMLLoader fxmlLoader;

  private Stage primaryStage;
  private BorderPane rootLayout;

  private final GuiceContext context = new GuiceContext(this,
      () -> Arrays.asList(new GuiceBaseModule()));

  private static final Logger logger = LoggerFactory
      .getLogger(GVSApplication.class);

  private static final String APP_NAME = "Graph Visualization Service";

  /**
   * Main method.
   * 
   * @param args
   *          console arguments
   */
  public static void main(String[] args) {
    Application.launch(GVSApplication.class);
  }

  /**
   * Launch the socket server in a separate thread. <br>
   * Launch the JavaFX application within UI thread.
   */
  @Override
  public void start(Stage stage) throws Exception {
    this.primaryStage = stage;

    logger.info("Initialize Guice injector");
    context.init();

    logger.info("Start Server Socket...");
    socketServer.start();

    logger.info("Start GVS UI 2.0...");
    setUpFrame();
  }

  /**
   * Set up the main frame
   * 
   * @param mainStage
   *          main stage
   */

  public void setUpFrame() {
    primaryStage.setMinHeight(Configuration.getWindowHeight());
    primaryStage.setMinWidth(Configuration.getWindowWidth());
    primaryStage.setTitle(APP_NAME);
    primaryStage.getIcons().add(new Image(
        getClass().getClassLoader().getResourceAsStream("images/logo.png")));

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
