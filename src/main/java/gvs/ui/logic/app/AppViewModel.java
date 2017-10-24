package gvs.ui.logic.app;

import java.io.File;
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
import javafx.collections.ObservableList;

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

  private final StringProperty currentSessionName = new SimpleStringProperty();
  private final ObservableList<String> sessionNames = FXCollections
      .observableArrayList();
  private final Map<String, ISessionController> controllerMap = new HashMap<>();

  private static final String PROMT_MESSAGE = "no active session";
  private static final Logger logger = LoggerFactory
      .getLogger(AppViewModel.class);

  // TODO: do we still need the persistor here? @mtrentini
  public AppViewModel(ApplicationModel appModel,
      ApplicationController appController, IPersistor persistor) {
    this.appModel = appModel;
    this.appModel.addObserver(this);
    this.appController = appController;
    this.persistor = persistor;
    currentSessionName.setValue(PROMT_MESSAGE);
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
    if (c != null) {
      String name = c.getSessionName();
      if (name != null && !name.isEmpty()) {
        currentSessionName.set(name);
        controllerMap.put(name, c);
        if (!sessionNames.contains(name)) {
          sessionNames.add(name);
        }
      } else {
        currentSessionName.set(PROMT_MESSAGE);
      }
    } else {
      logger.warn("ApplicationModel is not holding a current session.");
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
    if (name.equals(PROMT_MESSAGE)) {
      return;
    }

    ISessionController c = controllerMap.get(name);
    if (appModel.getSession().getSessionName() != name) {
      appController.changeCurrentSession(c);
      logger.debug(String.format("Changing current session to '%s'...", name));
    }
  }

  public void terminateApplication() {
    logger.debug("Quitting GVS...");
    Platform.exit();
    System.exit(0);
  }

}
