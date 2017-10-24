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
import gvs.ui.application.controller.GVSApplication;
import gvs.ui.application.model.ApplicationModel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
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

  // TODO: do we still need the persistor here? @mtrentini
  public AppViewModel(ApplicationModel appModel,
      ApplicationController appController, IPersistor persistor) {
    this.appModel = appModel;
    this.appModel.addObserver(this);
    this.appController = appController;
    this.persistor = persistor;
    currentSessionName.setValue("no active session");
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
      logger.debug("Set new current session in GUI.");
      String name = c.getSessionName();
      controllerMap.put(name, c);
      if (!sessionControllers.contains(name)) {
        sessionControllers.add(name);
      }
      // TODO: change selected item in combobox
      Platform.runLater(() -> currentSessionName.set(name));
    } else {
      logger.warn("ApplicationModel holds no current session.");
    }
  }

  // TODO: still shows session in dropdown
  public void removeCurrentSession() {
    ISessionController currentSession = appModel.getSession();
    appController.deleteSession(currentSession);
    sessionControllers.remove(currentSession.getSessionName());
    controllerMap.remove(currentSession.getSessionName());
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
    appController.changeCurrentSession(c);
  }

  public void terminateApplication() {
    Platform.exit();
    System.exit(0);
  }

}
