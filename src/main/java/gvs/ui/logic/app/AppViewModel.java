package gvs.ui.logic.app;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.GuiceBaseModule;
import gvs.business.logic.ApplicationController;
import gvs.business.model.CurrentSessionHolder;
import gvs.interfaces.ISessionController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
@Singleton
public class AppViewModel implements Observer {

  private final BooleanProperty sessionControlVisibilityProperty = new SimpleBooleanProperty();

  private final StringProperty currentSessionName = new SimpleStringProperty();
  private final ObservableList<String> sessionNames = FXCollections
      .observableArrayList();

  private final Map<String, ISessionController> controllerMap = new HashMap<>();
  private static final String PROMT_MESSAGE = "no active session";

  private final GuiceContext context = new GuiceContext(this,
      () -> Arrays.asList(new GuiceBaseModule()));
  private final CurrentSessionHolder appModel;
  private final ApplicationController appController;

  private static final Logger logger = LoggerFactory
      .getLogger(AppViewModel.class);

  @Inject
  public AppViewModel(CurrentSessionHolder appModel,
      ApplicationController appController) {
    context.init();
    this.appModel = appModel;
    this.appController = appController;

    this.appModel.addObserver(this);
    this.currentSessionName.set(PROMT_MESSAGE);

    sessionNames.addListener(this::changeSessionVisibility);
  }

  private void changeSessionVisibility(
      ListChangeListener.Change<? extends String> c) {
    if (sessionNames.size() == 1) {
      displaySession();
    } else if (sessionNames.isEmpty()) {
      hideSession();
    }
  }

  private void hideSession() {
    logger.info("Hiding session layout.");
    sessionControlVisibilityProperty.set(false);
  }

  private void displaySession() {
    logger.info("Displaying session layout.");
    sessionControlVisibilityProperty.set(true);
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
    Platform.runLater(() -> {
      ISessionController c = ((CurrentSessionHolder) o).getCurrentSession();
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
    });
  }

  public void removeCurrentSession() {
    logger.info("Removing current session...");
    ISessionController currentSession = appModel.getCurrentSession();
    String sessionName = currentSession.getSessionName();
    sessionNames.remove(sessionName);
    controllerMap.remove(sessionName);
    appController.deleteSession(currentSession);
  }

  public void loadSession(File file) {
    // clicking cancel sets file to null
    if (file != null) {
      logger.info("Loading session from file...");
      appController.loadStoredSession(file.getPath());
    }
  }

  public void saveSession(File file) {
    // clicking cancel sets file to null
    if (file != null) {
      logger.info("Saving session to file...");
      appModel.getCurrentSession().saveSession(file);
    }
  }

  public void changeSession(String name) {
    logger.info("Detecting change in combobox.");
    if (isInvalidSessionName(name)) {
      return;
    }

    ISessionController c = controllerMap.get(name);
    if (appModel.getCurrentSession().getSessionName() != name) {
      appController.changeCurrentSession(c);
      logger.info(String.format("Changing current session to '%s'...", name));
    }
  }

  private boolean isInvalidSessionName(String name) {
    return name == null || name.isEmpty() || PROMT_MESSAGE.equals(name);
  }

  public void terminateApplication() {
    logger.info("Quitting GVS...");
    Platform.exit();
    System.exit(0);
  }

  public BooleanProperty sessionVisibilityProperty() {
    return sessionControlVisibilityProperty;
  }
}
