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
 * ViewModel of the MVVM Pattern. It observs the ApplicationModel.
 * 
 * @author muriele
 *
 */
public class AppViewModel implements Observer {

  private ApplicationModel appModel;
  private ApplicationController appController;
  private IPersistor persistor;
  private static final Logger logger = LoggerFactory
      .getLogger(AppViewModel.class);

  private final ObservableList<String> sessionControllers = FXCollections
      .observableArrayList();
  private StringProperty currentSessionName = new SimpleStringProperty();
  private final Map<String, ISessionController> controllerMap = new HashMap<>();
  private static final String PROMT_MESSAGE = "no active session";

  // TODO: do we still need the persistor here? @mtrentini
  public AppViewModel(ApplicationModel appModel,
      ApplicationController appController, IPersistor persistor) {
    this.appModel = appModel;
    this.appModel.addObserver(this);
    this.appController = appController;
    this.persistor = persistor;
    currentSessionName.setValue(PROMT_MESSAGE);
  }

  public ObservableList<String> getSessionControllers() {
    return sessionControllers;
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
      if (name != null && name != "") {
        currentSessionName.set(name);
        controllerMap.put(name, c);
        if (!sessionControllers.contains(name)) {
          sessionControllers.add(name);
        }
      } else {
        currentSessionName.set(PROMT_MESSAGE);
      }
    } else {
      logger.warn("ApplicationModel holds no current session.");
    }
  }

  public void removeCurrentSession() {
    ISessionController currentSession = appModel.getSession();
    sessionControllers.remove(currentSession.getSessionName());
    controllerMap.remove(currentSession.getSessionName());
    appController.deleteSession(currentSession);
  }

  public void loadSession(File file) {
    if (file != null) {
      appController.setRequestedFile(file.getPath(), persistor);
    }
  }

  public void saveSession() {
    appModel.getSession().saveSession();
  }

  public void changeSession(String name) {
    ISessionController c = controllerMap.get(name);
    if (c == null) {
      return;
    }
    if (appModel.getSession().getSessionName() != name) {
      appController.changeCurrentSession(c);
      logger.debug(String.format("Changing current session to '%s'...", name));
    }
  }

  public void terminateApplication() {
    Platform.exit();
    System.exit(0);
  }

}
