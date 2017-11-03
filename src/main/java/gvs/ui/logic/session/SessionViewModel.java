package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.model.CurrentSessionHolder;
import gvs.interfaces.ISessionController;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

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

  private final CurrentSessionHolder currentSessionHolder;

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
  public SessionViewModel(CurrentSessionHolder currentSessionHolder) {

    logger.info("Initializing SessionViewModel.");
    this.currentSessionHolder = currentSessionHolder;

    updateStepProperties();

    currentSessionHolder.addObserver(this);
  }

  public void changeCurrentGraphToNext() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToNext();
    updateStepProperties();
  }

  public void changeCurrentGraphToPrevious() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToPrev();
    updateStepProperties();
  }

  public void changeCurrentGraphToFirst() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToFirst();
    updateStepProperties();
  }

  public void changeCurrentGraphToLast() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToLast();
    updateStepProperties();
  }

  /**
   * This method is invoked whenever a new current session is set in the
   * ApplicationModel.
   */
  @Override
  public void update(Observable o, Object arg) {
    logger.info("Current session changed...");

    Platform.runLater(() -> {
      updateStepProperties();
    });
  }

  private void updateStepProperties() {
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();

    if (currentSession != null) {
      totalGraphCountProperty.set(currentSession.getTotalGraphCount());
      currentGraphIdProperty.set(currentSession.getCurrentGraph().getId());
    } else {
      totalGraphCountProperty.set(0);
      currentGraphIdProperty.set(0);
    }
  }

  public void replayGraph(long timeout) {
    logger.info("Starting replay with speed {}", timeout);
    disableAllButtons(true);
    currentSessionHolder.getCurrentSession().replay(timeout);

    // TODO view should somehow be notified by business if replay has finished
    disableAllButtons(false);
  }

  public void autoLayout() {
    logger.info("Auto-layouting the current graph model...");
    disableAllButtons(true);
    currentSessionHolder.getCurrentSession().autoLayout();

    // TODO view should somehow be notified by business if layout has finished
    disableAllButtons(false);
  }

  private void disableAllButtons(boolean disabled) {
    firstBtnDisableProperty.set(disabled);
    lastBtnDisableProperty.set(disabled);
    prevBtnDisableProperty.set(disabled);
    nextBtnDisableProperty.set(disabled);
    autoLayoutBtnDisableProperty.set(disabled);
    replayBtnDisableProperty.set(disabled);
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
