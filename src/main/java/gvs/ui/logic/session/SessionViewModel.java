package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.GraphSessionType;
import gvs.business.logic.Session;
import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.SessionHolder;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

  private final SessionHolder sessionHolder;

  private final StringProperty layoutTooltip;

  private final BooleanProperty lastBtnDisableProperty;
  private final BooleanProperty firstBtnDisableProperty;
  private final BooleanProperty nextBtnDisableProperty;
  private final BooleanProperty prevBtnDisableProperty;
  private final BooleanProperty autoLayoutBtnDisableProperty;
  private final BooleanProperty replayBtnDisableProperty;
  private final BooleanProperty cancelReplayBtnDisableProperty;
  private final BooleanProperty isReplayingProperty;

  private final IntegerProperty currentGraphIdProperty;
  private final IntegerProperty totalGraphCountProperty;

  private static final String LAYOUT_INFO_TOOLTIP = "All nodes are user positioned.\nAuto layout not possible.";

  private static final Logger logger = LoggerFactory
      .getLogger(SessionViewModel.class);

  @Inject
  public SessionViewModel(SessionHolder currentSessionHolder) {

    logger.info("Initializing SessionViewModel.");
    this.sessionHolder = currentSessionHolder;

    this.lastBtnDisableProperty = new SimpleBooleanProperty();
    this.firstBtnDisableProperty = new SimpleBooleanProperty();
    this.nextBtnDisableProperty = new SimpleBooleanProperty();
    this.prevBtnDisableProperty = new SimpleBooleanProperty();

    this.currentGraphIdProperty = new SimpleIntegerProperty();
    this.totalGraphCountProperty = new SimpleIntegerProperty();

    this.isReplayingProperty = new SimpleBooleanProperty();
    this.replayBtnDisableProperty = new SimpleBooleanProperty();
    this.cancelReplayBtnDisableProperty = new SimpleBooleanProperty();

    this.autoLayoutBtnDisableProperty = new SimpleBooleanProperty();
    this.layoutTooltip = new SimpleStringProperty();

    updateStepProperties();

    currentSessionHolder.addObserver(this);
  }

  public void changeCurrentGraphToNext() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
    currentSession.changeCurrentGraphToNext();
    updateStepProperties();
  }

  public void changeCurrentGraphToPrevious() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
    currentSession.changeCurrentGraphToPrev();
    updateStepProperties();
  }

  public void changeCurrentGraphToFirst() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
    currentSession.changeCurrentGraphToFirst();
    updateStepProperties();
  }

  public void changeCurrentGraphToLast() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
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

      // reset buttons
      disableAllButtons(false);
    });
  }

  public void updateStepProperties() {
    Session currentSession = sessionHolder.getCurrentSession();

    Platform.runLater(() -> {
      if (currentSession != null) {
        int currentPosition = currentSession.getGraphHolder().getCurrentGraph()
            .getId();
        int maxPosition = currentSession.getTotalGraphCount();
        totalGraphCountProperty.set(maxPosition);
        currentGraphIdProperty.set(currentPosition);
        if (!isReplayingProperty.get()) {
          if (currentPosition == 1 && maxPosition == 1) {
            disableStepButtons(true, true, true, true);
            disableReplayButtons(true, true);
          } else if (currentPosition <= 1) {
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
    isReplayingProperty.set(true);

    boolean disable = true;
    disableStepButtons(disable, disable, disable, disable);
    disableLayoutButton(disable);
    sessionHolder.getCurrentSession().replay(timeout, this::finishReplay);
  }

  public void pauseReplay() {
    isReplayingProperty.set(false);
    sessionHolder.getCurrentSession().pauseReplay();
  }

  public void cancelReplay() {
    isReplayingProperty.set(false);
    sessionHolder.getCurrentSession().cancelReplay();
  }

  public void finishReplay() {
    Platform.runLater(() -> {
      isReplayingProperty.set(false);
      disableAllButtons(false);
    });
  }

  public void autoLayout(boolean useRandomLayout) {
    disableAllButtons(true);
    Session currentSession = sessionHolder.getCurrentSession();
    Graph currentGraph = currentSession.getGraphHolder().getCurrentGraph();

    if (currentSession.getSessionType() instanceof GraphSessionType && currentGraph.isLayoutable()) {
      ILayouter layouter = currentSession.getSessionType().getLayouter();
      layouter.layout(currentGraph, useRandomLayout, this::finishAutolayout);
    } else {
      layoutTooltip.set(LAYOUT_INFO_TOOLTIP);
      autoLayoutBtnDisableProperty.set(true);
      finishAutolayout();
    }
  }

  public void finishAutolayout() {
    disableAllButtons(false);
  }

  private void disableAllButtons(boolean disabled) {
    disableStepButtons(disabled, disabled, disabled, disabled);
    disableReplayButtons(disabled, disabled);
    disableLayoutButton(disabled);
  }

  private void disableReplayButtons(boolean startStop, boolean cancel) {
    replayBtnDisableProperty.set(startStop);
    cancelReplayBtnDisableProperty.set(cancel);
  }

  private void disableStepButtons(boolean first, boolean prev, boolean next,
      boolean last) {
    firstBtnDisableProperty.set(first);
    lastBtnDisableProperty.set(last);
    prevBtnDisableProperty.set(prev);
    nextBtnDisableProperty.set(next);
  }

  private void disableLayoutButton(boolean layout) {
    autoLayoutBtnDisableProperty.set(layout);
  }

  public IntegerProperty totalGraphCountProperty() {
    return totalGraphCountProperty;
  }

  public IntegerProperty currentGraphIdProperty() {
    return currentGraphIdProperty;
  }

  public BooleanProperty lastBtnDisableProperty() {
    return lastBtnDisableProperty;
  }

  public BooleanProperty firstBtnDisableProperty() {
    return firstBtnDisableProperty;
  }

  public BooleanProperty nextBtnDisableProperty() {
    return nextBtnDisableProperty;
  }

  public BooleanProperty prevBtnDisableProperty() {
    return prevBtnDisableProperty;
  }

  public BooleanProperty autoLayoutBtnDisableProperty() {
    return autoLayoutBtnDisableProperty;
  }

  public BooleanProperty replayBtnDisableProperty() {
    return replayBtnDisableProperty;
  }

  public BooleanProperty cancelReplayBtnDisableProperty() {
    return cancelReplayBtnDisableProperty;
  }

  public BooleanProperty isReplayingProperty() {
    return isReplayingProperty;
  }

  public StringProperty layoutTooltip() {
    return layoutTooltip;
  }
}
