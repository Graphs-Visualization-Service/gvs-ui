package gvs.ui.application.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.Inject;

import gvs.GuiceBaseModule;
import gvs.common.Persistor;
import gvs.interfaces.ISessionController;
import gvs.ui.application.model.ApplicationModel;
import gvs.ui.logic.app.AppViewModel;
import gvs.ui.view.app.AppView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This class is the entrypoint to the GVS application. It loads the application
 * frame.
 * 
 * @author muriele
 *
 */
public class GVSApplication extends Application implements Observer {

  private static final Logger logger = LoggerFactory
      .getLogger(GVSApplication.class);

  private GuiceContext context = new GuiceContext(this,
      () -> Arrays.asList(new GuiceBaseModule()));

  @Inject
  private FXMLLoader fxmlLoader;

  private Stage primaryStage;
  private BorderPane rootLayout;
  private BorderPane sessionLayout;
  private AppView appView;

  /**
   * Set up the main frame
   * 
   * @param mainStage
   *          main stage
   */
  @Override
  public void start(Stage mainStage) {
    context.init();

    this.primaryStage = mainStage;
    this.primaryStage.setTitle("GVS");
    initRootLayout();
    // displaySession();
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
      primaryStage.show();

      appView = fxmlLoader.getController();
      ApplicationModel appModel = new ApplicationModel();
      appModel.addObserver(this);
      appView.setAppViewModel(new AppViewModel(appModel,
          ApplicationController.getInstance(appModel), new Persistor()));
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

  /**
   * The GVSApplication gets notified by the ApplicationModel whenever a new
   * current session is set.
   * 
   * @param o
   * @param arg
   */
  @Override
  public void update(Observable o, Object arg) {
    // TODO Auto-generated method stub

  }
}
