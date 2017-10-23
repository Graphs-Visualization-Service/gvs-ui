package gvs.ui.logic.app;

import java.util.Observable;
import java.util.Observer;

import gvs.common.Persistor;
import gvs.interfaces.ISessionController;
import gvs.ui.application.controller.ApplicationController;
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
  private Persistor persistor;

  private final ObservableList<String> sessionControllers = FXCollections
      .observableArrayList();
  private StringProperty currentSessionName = new SimpleStringProperty();

  public AppViewModel(ApplicationModel appModel,
      ApplicationController appController, Persistor persistor) {
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
    String name = ((ApplicationModel) o).getSession().getSessionName();
    sessionControllers.add(name);
    Platform.runLater(() -> currentSessionName.set(name));
  }

  public void removeCurrentSession() {
    ISessionController currentSession = appModel.getSession();
    appController.deleteSession(currentSession);
    sessionControllers.remove(currentSession);
  }

}
