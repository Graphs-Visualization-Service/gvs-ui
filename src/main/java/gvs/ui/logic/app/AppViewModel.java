package gvs.ui.logic.app;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.ApplicationController;
import gvs.business.logic.Session;
import gvs.business.model.SessionHolder;
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

  private final BooleanProperty sessionControlVisibilityProperty;
  private final StringProperty currentSessionName;
  private final ObservableList<String> sessionNames;
  private final Map<String, Session> sessionMap;
  private final SessionHolder sessionHolder;
  private final ApplicationController appController;

  private static final String NO_SESSIONS_MSG = "No active session";
  private static final Logger logger = LoggerFactory
      .getLogger(AppViewModel.class);

  @Inject
  public AppViewModel(SessionHolder sessionHolder,
      ApplicationController appController) {

    this.sessionHolder = sessionHolder;
    this.appController = appController;

    this.sessionControlVisibilityProperty = new SimpleBooleanProperty();
    this.currentSessionName = new SimpleStringProperty();
    this.sessionNames = FXCollections.observableArrayList();
    this.sessionMap = new HashMap<>();

    this.sessionHolder.addObserver(this);
    this.currentSessionName.set(NO_SESSIONS_MSG);

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

  /**
   * This method is invoked whenever a new current session is set in the
   * ApplicationModel.
   */
  @Override
  public void update(Observable o, Object arg) {
    // Hand updates over to JavaFX Thread
    Platform.runLater(() -> {
      Session currentSession = ((SessionHolder) o).getCurrentSession();
      if (currentSession == null) {
        currentSessionName.set(NO_SESSIONS_MSG);
      } else {
        String name = currentSession.getSessionName();
        currentSessionName.set(name);
        sessionMap.put(name, currentSession);
        if (!sessionNames.contains(name)) {
          sessionNames.add(name);
        }
      }
    });
  }

  public void removeCurrentSession() {
    logger.info("Removing current session...");
    Session currentSession = sessionHolder.getCurrentSession();
    String sessionName = currentSession.getSessionName();
    sessionNames.remove(sessionName);
    sessionMap.remove(sessionName);
    
    if (sessionNames.isEmpty()) {
      currentSessionName.set(NO_SESSIONS_MSG);
    }
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
      appController.saveSession(sessionHolder.getCurrentSession(),file);
    }
  }

  public void changeSession(String name) {
    logger.info("Detecting change in combobox.");
    if (isInvalidSessionName(name)) {
      logger.warn("No valid session name");
      return;
    }

    Session c = sessionMap.get(name);
    if (sessionHolder.getCurrentSession().getSessionName() != name) {
      appController.changeCurrentSession(c);
      logger.info("Changing current session to {}...", name);
    }
  }

  private boolean isInvalidSessionName(String name) {
    return name == null || name.isEmpty() || NO_SESSIONS_MSG.equals(name);
  }

  public BooleanProperty sessionVisibilityProperty() {
    return sessionControlVisibilityProperty;
  }

  public ObservableList<String> getSessionNames() {
    return sessionNames;
  }

  public StringProperty getCurrentSessionName() {
    return currentSessionName;
  }
}
