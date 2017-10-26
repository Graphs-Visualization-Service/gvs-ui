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
import javafx.collections.ListChangeListener;
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
    this.currentSessionName.set(PROMT_MESSAGE);
    sessionNames.addListener((ListChangeListener.Change<? extends String> c) -> {
      if (sessionNames.size() == 1) {
        displaySession();
      } else if (sessionNames.isEmpty()) {
        hideSession();
      }
    });
  }

  private void hideSession() {
    System.out.println("hiding session");
  }

  private void displaySession() {
    System.out.println("showing session");
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

}
