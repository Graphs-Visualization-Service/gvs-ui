package gvs.ui.application.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GVSApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(GVSApplication.class);

    private Stage primaryStage;
    private BorderPane rootLayout;
    private BorderPane sessionLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("GVS");
        initRootLayout();
        displaySession();
    }

    private void initRootLayout() {
        try {
            logger.debug("Initialize root layout");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GVSApplication.class.getResource("../view/Main.fxml"));
            rootLayout = (BorderPane) loader.load();

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

    public void displaySession() {
        try {
            logger.debug("Load session layout");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(GVSApplication.class.getResource("../view/Session.fxml"));
            sessionLayout = (BorderPane) loader.load();
            AnchorPane sessionContentPane = new AnchorPane();
            sessionContentPane.getChildren().add(sessionLayout);
            setAnchors(sessionLayout, 0, 0, 0, 0);
            rootLayout.setCenter(sessionContentPane);
        } catch (IOException e) {
            logger.error("Could not load session layout");
            // TODO error handling
            e.printStackTrace();
        }
    }

    private void setAnchors(Node sessionLayout, int top, int bottom, int left,
            int right) {
        AnchorPane.setTopAnchor(sessionLayout, (double) top);
        AnchorPane.setBottomAnchor(sessionLayout, (double) bottom);
        AnchorPane.setLeftAnchor(sessionLayout, (double) left);
        AnchorPane.setRightAnchor(sessionLayout, (double) right);
    }
}
