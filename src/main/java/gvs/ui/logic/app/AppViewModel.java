package gvs.ui.logic.app;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import gvs.business.logic.ApplicationController;
import gvs.business.logic.GraphSessionType;
import gvs.business.logic.Session;
import gvs.business.logic.SessionFactory;
import gvs.business.model.SessionHolder;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * The ViewModel class for the GVS Application. Corresponds to the classical
 * ViewModel of the MVVM Pattern. It observes the ApplicationModel and handles
 * the GUI logic.
 * 
 * @author mtrentini
 *
 */
@Singleton
public class AppViewModel implements Observer {

  private final BooleanProperty sessionControlVisibilityProperty;
  private final ObjectProperty<Session> selectedSession;
  private final ObservableList<Session> sessions;
  private final SessionHolder sessionHolder;
  private final ApplicationController appController;

  private final Session sessionPlaceHolder;
  private static final int PLACEHOLDER_SESSION_ID = -1;
  private static final String PLACEHOLDER_SESSION_NAME = "-- no active session --";

  private static final Logger logger = LoggerFactory
      .getLogger(AppViewModel.class);

  @Inject
  public AppViewModel(SessionHolder sessionHolder,
      ApplicationController appController, SessionFactory sessionFactory,
      Provider<GraphSessionType> graphSessionTypeProvider) {

    this.sessionHolder = sessionHolder;
    this.appController = appController;

    this.sessionControlVisibilityProperty = new SimpleBooleanProperty();
    this.selectedSession = new SimpleObjectProperty<>();
    this.sessions = FXCollections.observableArrayList();

    this.sessionHolder.addObserver(this);
    sessionPlaceHolder = sessionFactory.createSession(
        graphSessionTypeProvider.get(), PLACEHOLDER_SESSION_ID,
        PLACEHOLDER_SESSION_NAME);
    this.selectedSession.set(sessionPlaceHolder);

    sessions.addListener(this::changeSessionVisibility);
  }

  private void changeSessionVisibility(
      ListChangeListener.Change<? extends Session> c) {
    if (sessions.size() == 1) {
      displaySession();
    } else if (sessions.isEmpty()) {
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
        this.selectedSession.set(sessionPlaceHolder);
      } else {
        this.selectedSession.set(currentSession);
        if (!sessions.contains(currentSession)) {
          sessions.add(currentSession);
        }
      }
    });
  }

  public void removeCurrentSession() {
    logger.info("Removing current session...");
    Session currentSession = sessionHolder.getCurrentSession();
    sessions.remove(currentSession);

    if (sessions.isEmpty()) {
      selectedSession.set(sessionPlaceHolder);
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
      appController.saveSession(sessionHolder.getCurrentSession(), file);
    }
  }

  public void changeSession(Session session) {
    logger.info("Detecting change in combobox.");
    if (!sessionHolder.getCurrentSession().equals(session)) {

      logger.info("Changing current session to {}...",
          session.getSessionName());
      appController.changeCurrentSession(session);
    }
  }

  public BooleanProperty sessionVisibilityProperty() {
    return sessionControlVisibilityProperty;
  }

  public ObservableList<Session> getSessions() {
    return sessions;
  }

  public ObjectProperty<Session> getCurrentSession() {
    return selectedSession;
  }
}
