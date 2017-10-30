package gvs.ui.application.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.GuiceBaseModule;
import gvs.common.Persistor;
import gvs.ui.application.model.ApplicationModel;
import gvs.ui.logic.app.AppViewModel;
import gvs.ui.view.app.AppView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This class is the entrypoint to the GVS application. It loads the application
 * frame.
 * 
 * @author muriele
 *
 */
@Singleton
public class GVSApplication extends Application implements Observer {

  private static final Logger logger = LoggerFactory
      .getLogger(GVSApplication.class);

  private final GuiceContext context = new GuiceContext(this,
      () -> Arrays.asList(new GuiceBaseModule()));

  @Inject
  private FXMLLoader fxmlLoader;

  private Stage primaryStage;
  private BorderPane rootLayout;
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

    primaryStage = mainStage;
    primaryStage.setTitle("GVS");
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
      primaryStage.show();

      appView = fxmlLoader.getController();
      ApplicationModel appModel = new ApplicationModel();
      appModel.addObserver(this);
      appView.setAppViewModel(new AppViewModel(appModel,
          ApplicationController.getInstance(appModel), new Persistor(),
          rootLayout));
    } catch (IOException e) {
      logger.error("Could not initialize root layout", e);
    }
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
