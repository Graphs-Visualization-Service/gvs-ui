package gvs.ui.application.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private Stage primaryStage;
  private AnchorPane rootLayout;

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    this.primaryStage.setTitle("GVS");

    initRootLayout();
  }

  private void initRootLayout() {
    try {
      // Load root layout from fxml file.
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(Main.class.getResource("../view/Main.fxml"));
      rootLayout = (AnchorPane) loader.load();

      // Show the scene containing the root layout.
      Scene scene = new Scene(rootLayout);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      // TODO add logging framework
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
