package gvs.ui.application.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 
 * @author Michi
 *
 */
public class GVSApplication extends Application {

  private final Logger logger = LoggerFactory.getLogger(GVSApplication.class);

  private static final String APPLICATION_NAME = "GVS";

  /**
   * Creates the main frame.
   */
  @Override
  public void start(Stage primaryStage) {
    initRootLayout(primaryStage);
  }

  /**
   * Initializes the root layout.
   * 
   * @param primaryStage
   *          The primary stage
   */
  private void initRootLayout(Stage primaryStage) {
    logger.debug("Initialize root layout");

    try {
      // Load FXML file
      String fxmlSource = "/gvs/ui/application/view/Main.fxml";
      FXMLLoader loader = new FXMLLoader();
      BorderPane rootLayout = (BorderPane) loader
          .load(GVSApplication.class.getResourceAsStream(fxmlSource));

      // Show the scene containing the root layout.
      Scene scene = new Scene(rootLayout);
      primaryStage.setTitle(APPLICATION_NAME);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      logger.error("Could not initialize root layout", e);
    }
  }
}
