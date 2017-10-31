package gvs.ui.logic.app;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import gvs.GuiceBaseModule;
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
public class GVSApplication extends Application {

  private static final Logger logger = LoggerFactory
      .getLogger(GVSApplication.class);

  private final GuiceContext context = new GuiceContext(this,
      () -> Arrays.asList(new GuiceBaseModule()));

  @Inject
  private Provider<FXMLLoader> loaderProvider;

  private Stage primaryStage;
  private BorderPane rootLayout;

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

      FXMLLoader fxmlLoader = loaderProvider.get();
      fxmlLoader
          .setLocation(getClass().getResource("/gvs/ui/view/app/AppView.fxml"));

      rootLayout = (BorderPane) fxmlLoader.load();

      // Show the scene containing the root layout.
      Scene scene = new Scene(rootLayout);
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (IOException e) {
      logger.error("Could not initialize root layout", e);
    }
  }

}
