package gvs.ui.logic.app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.interfaces.IPersistor;
import gvs.interfaces.ISessionController;
import gvs.ui.application.controller.ApplicationController;
import gvs.ui.application.model.ApplicationModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * The ViewModel class for the GVS Application. Corresponds to the classical
 * ViewModel of the MVVM Pattern. It observes the ApplicationModel and handles
 * the GUI logic.
 * 
 * @author muriele
 *
 */
public class AppViewModel implements Observer {

  private ApplicationModel appModel;
  private ApplicationController appController;
  private IPersistor persistor;
  private AnchorPane sessionContentPane;
  private BorderPane rootLayout;

  private final StringProperty currentSessionName = new SimpleStringProperty();
  private final ObservableList<String> sessionNames = FXCollections
      .observableArrayList();
  private final Map<String, ISessionController> controllerMap = new HashMap<>();

  private static final String PROMT_MESSAGE = "no active session";
  private static final Logger logger = LoggerFactory
      .getLogger(AppViewModel.class);

  // TODO: do we still need the persistor here?
  public AppViewModel(ApplicationModel appModel,
      ApplicationController appController, IPersistor persistor, BorderPane rootLayout) {
    this.appModel = appModel;
    this.appModel.addObserver(this);
    this.appController = appController;
    this.persistor = persistor;
    this.currentSessionName.set(PROMT_MESSAGE);
    this.rootLayout = rootLayout;
    initSessionLayout();
    sessionNames
        .addListener((ListChangeListener.Change<? extends String> c) -> {
          if (sessionNames.size() == 1) {
            displaySession();
          } else if (sessionNames.isEmpty()) {
            hideSession();
          }
        });
  }

  private void initSessionLayout() {
    logger.debug("Initializing session layout.");
    FXMLLoader loader = new FXMLLoader();
    try {
      BorderPane sessionLayout = (BorderPane) loader.load(getClass()
          .getResourceAsStream("/gvs/ui/view/session/SessionView.fxml"));
      sessionContentPane = new AnchorPane();
      sessionContentPane.getChildren().add(sessionLayout);
      final int anchorMargin = 0;
      setAnchors(sessionLayout, anchorMargin, anchorMargin, anchorMargin, anchorMargin);
    } catch (IOException e) {
      logger.error("Could not load session layout", e);
    }
  }

  private void hideSession() {
    logger.debug("Hiding session layout.");
    rootLayout.setCenter(null);
  }

  private void displaySession() {
    logger.debug("Displaying session layout.");
    rootLayout.setCenter(sessionContentPane);
  }

  public ObservableList<String> getSessionNames() {
    return sessionNames;
  }

  public StringProperty getCurrentSessionName() {
    return currentSessionName;
  }

  /**
   * This method is invoked whenever a new current session is set in the
   * ApplicationModel.
   */
  @Override
  public void update(Observable o, Object arg) {
    ISessionController c = ((ApplicationModel) o).getSession();
    String name = c.getSessionName();
    if (name == null) {
      currentSessionName.set(PROMT_MESSAGE);
    } else {
      currentSessionName.set(name);
      controllerMap.put(name, c);
      if (!sessionNames.contains(name)) {
        sessionNames.add(name);
      }
    }
  }

  public void removeCurrentSession() {
    logger.debug("Removing current session...");
    ISessionController currentSession = appModel.getSession();
    String sessionName = currentSession.getSessionName();
    sessionNames.remove(sessionName);
    controllerMap.remove(sessionName);
    appController.deleteSession(currentSession);
  }

  public void loadSession(File file) {
    logger.debug("Loading session from file...");
    appController.setRequestedFile(file.getPath(), persistor);
  }

  public void saveSession() {
    logger.debug("Saving session to file...");
    appModel.getSession().saveSession();
  }

  public void changeSession(String name) {
    logger.debug("Detecting change in combobox.");
    if (isInvalidSessionName(name)) {
      return;
    }

    ISessionController c = controllerMap.get(name);
    if (appModel.getSession().getSessionName() != name) {
      appController.changeCurrentSession(c);
      logger.debug(String.format("Changing current session to '%s'...", name));
    }
  }

  private boolean isInvalidSessionName(String name) {
    return name == null || name.isEmpty() || PROMT_MESSAGE.equals(name);
  }

  public void terminateApplication() {
    logger.debug("Quitting GVS...");
    Platform.exit();
    System.exit(0);
  }

  /**
   * Helper function. Set anchors for a child of an AnchorPane.
   * @param top
   * @param bottom
   * @param left
   * @param right
   */
  private void setAnchors(Node sessionLayout, int top, int bottom, int left, int right) {
    AnchorPane.setTopAnchor(sessionLayout, (double) top);
    AnchorPane.setBottomAnchor(sessionLayout, (double) bottom);
    AnchorPane.setLeftAnchor(sessionLayout, (double) left);
    AnchorPane.setRightAnchor(sessionLayout, (double) right);
  }
}
