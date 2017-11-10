package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.model.SessionHolder;
import gvs.interfaces.ISession;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 * The ViewModel class for the current session. Corresponds to the classical
 * ViewModel of the MVVM Pattern. It observes the ApplicationModel and handles
 * the GUI session logic.
 * 
 * @author mtrentini
 *
 */
@Singleton
public class SessionViewModel implements Observer {

  private boolean isReplaying;

  private final SessionHolder currentSessionHolder;

  private final BooleanProperty replayBtnDisableProperty = new SimpleBooleanProperty();
  private final BooleanProperty lastBtnDisableProperty = new SimpleBooleanProperty();
  private final BooleanProperty firstBtnDisableProperty = new SimpleBooleanProperty();
  private final BooleanProperty nextBtnDisableProperty = new SimpleBooleanProperty();
  private final BooleanProperty prevBtnDisableProperty = new SimpleBooleanProperty();
  private final BooleanProperty autoLayoutBtnDisableProperty = new SimpleBooleanProperty();

  private final IntegerProperty currentGraphIdProperty = new SimpleIntegerProperty();
  private final IntegerProperty totalGraphCountProperty = new SimpleIntegerProperty();

  private static final Logger logger = LoggerFactory
      .getLogger(SessionViewModel.class);

  @Inject
  public SessionViewModel(SessionHolder currentSessionHolder) {
    logger.info("Initializing SessionViewModel.");
    this.currentSessionHolder = currentSessionHolder;

    updateStepProperties();

    currentSessionHolder.addObserver(this);
  }

  public void changeCurrentGraphToNext() {
    logger.info("Changing the displayed graph model...");
    ISession currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToNext();
    updateStepProperties();
  }

  public void changeCurrentGraphToPrevious() {
    logger.info("Changing the displayed graph model...");
    ISession currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToPrev();
    updateStepProperties();
  }

  public void changeCurrentGraphToFirst() {
    logger.info("Changing the displayed graph model...");
    ISession currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToFirst();
    updateStepProperties();
  }

  public void changeCurrentGraphToLast() {
    logger.info("Changing the displayed graph model...");
    ISession currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToLast();
    updateStepProperties();
  }

  /**
   * This method is invoked whenever a new current session is set in the
   * {@link SessionHolder}.
   */
  @Override
  public void update(Observable o, Object arg) {
    // Hand updates over to JavaFX Thread
    Platform.runLater(() -> {
      logger.info("Current session changed...");
      updateStepProperties();
    });
  }

  public void updateStepProperties() {
    ISession currentSession = currentSessionHolder
        .getCurrentSession();

    Platform.runLater(() -> {
      if (currentSession != null) {
        int currentPosition = currentSession.getCurrentGraph().getId();
        int maxPosition = currentSession.getTotalGraphCount();
        totalGraphCountProperty.set(maxPosition);
        currentGraphIdProperty.set(currentPosition);
        if (!isReplaying) {
          if (currentPosition <= 1) {
            disableStepButtons(true, true, false, false);
          } else if (currentPosition == maxPosition) {
            disableStepButtons(false, false, true, true);
          } else {
            disableStepButtons(false, false, false, false);
          }
        }
      } else {
        totalGraphCountProperty.set(0);
        currentGraphIdProperty.set(0);
        disableStepButtons(true, true, true, true);
      }
    });
  }

  public void replayGraph(long timeout) {
    logger.info("Starting replay with speed {}", timeout);
    disableAllButtons(true);
    isReplaying = true;
    currentSessionHolder.getCurrentSession().replay(timeout,
        this::finishReplay);
  }

  public void finishReplay() {
    // TODO: refactor disabling/enabling buttons
    disableAllButtons(false);
    disableStepButtons(false, false, true, true);
    isReplaying = false;
  }

  public void autoLayout() {
    logger.info("Auto-layouting the current graph model...");
    disableAllButtons(true);
    currentSessionHolder.getCurrentSession().autoLayout();

    disableAllButtons(false);
  }

  private void disableAllButtons(boolean disabled) {
    disableStepButtons(disabled, disabled, disabled, disabled);
    autoLayoutBtnDisableProperty.set(disabled);
    replayBtnDisableProperty.set(disabled);
  }

  private void disableStepButtons(boolean first, boolean prev, boolean next,
      boolean last) {
    firstBtnDisableProperty.set(first);
    lastBtnDisableProperty.set(last);
    prevBtnDisableProperty.set(prev);
    nextBtnDisableProperty.set(next);
  }

  public IntegerProperty totalGraphCountProperty() {
    return totalGraphCountProperty;
  }

  public IntegerProperty currentGraphIdProperty() {
    return currentGraphIdProperty;
  }

  public BooleanProperty getReplayBtnDisableProperty() {
    return replayBtnDisableProperty;
  }

  public BooleanProperty getLastBtnDisableProperty() {
    return lastBtnDisableProperty;
  }

  public BooleanProperty getFirstBtnDisableProperty() {
    return firstBtnDisableProperty;
  }

  public BooleanProperty getNextBtnDisableProperty() {
    return nextBtnDisableProperty;
  }

  public BooleanProperty getPrevBtnDisableProperty() {
    return prevBtnDisableProperty;
  }

  public BooleanProperty getAutoLayoutBtnDisableProperty() {
    return autoLayoutBtnDisableProperty;
  }

}
