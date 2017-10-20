package gvs.ui.application.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GVSApplication extends Application {

  private static final Logger logger = LoggerFactory
      .getLogger(GVSApplication.class);

  private Stage primaryStage;
  private BorderPane rootLayout;
  private BorderPane sessionLayout;

  /**
   * Set up the main frame
   * 
   * @param mainStage
   *          main stage
   */
  @Override
  public void start(Stage mainStage) {
    this.primaryStage = mainStage;
    this.primaryStage.setTitle("GVS");
    initRootLayout();
    displaySession();
  }

  /**
   * Loads the main fxml and shows the main frame.
   */
  private void initRootLayout() {
    try {
      logger.debug("Initialize root layout");
      FXMLLoader loader = new FXMLLoader();
      rootLayout = (BorderPane) loader.load(GVSApplication.class
          .getResourceAsStream("/gvs/ui/view/app/AppView.fxml"));

      // Show the scene containing the root layout.
      Scene scene = new Scene(rootLayout);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      logger.error("Could not initialize root layout");
      // TODO error handling
      e.printStackTrace();
    }
  }

  /**
   * Loads the session fxml and shows the session toolbars.
   */
  private void displaySession() {
    try {
      logger.debug("Load session layout");
      FXMLLoader loader = new FXMLLoader();
      sessionLayout = (BorderPane) loader.load(getClass()
          .getResourceAsStream("/gvs/ui/view/session/SessionView.fxml"));
      AnchorPane sessionContentPane = new AnchorPane();
      sessionContentPane.getChildren().add(sessionLayout);
      final int top = 0;
      final int bottom = 0;
      final int left = 0;
      final int right = 0;
      setAnchors(top, bottom, left, right);
      rootLayout.setCenter(sessionContentPane);
    } catch (IOException e) {
      logger.error("Could not load session layout", e);
    }
  }

  private void setAnchors(int top, int bottom, int left, int right) {
    AnchorPane.setTopAnchor(sessionLayout, (double) top);
    AnchorPane.setBottomAnchor(sessionLayout, (double) bottom);
    AnchorPane.setLeftAnchor(sessionLayout, (double) left);
    AnchorPane.setRightAnchor(sessionLayout, (double) right);
  }
}
