package gvs.ui.logic.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.GraphSessionType;
import gvs.business.logic.Session;
import gvs.business.logic.TreeSessionType;
import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.SessionHolder;
import gvs.ui.view.ScalablePane;
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
public class SessionViewModel {

  private final SessionHolder sessionHolder;

  private final StringProperty layoutTooltip;

  private final BooleanProperty lastBtnDisableProperty;
  private final BooleanProperty firstBtnDisableProperty;
  private final BooleanProperty nextBtnDisableProperty;
  private final BooleanProperty prevBtnDisableProperty;
  private final BooleanProperty autoLayoutBtnDisableProperty;
  private final BooleanProperty replayBtnDisableProperty;
  private final BooleanProperty cancelReplayBtnDisableProperty;
  private final BooleanProperty speedSliderDisableProperty;
  private final BooleanProperty isReplayingProperty;

  private final IntegerProperty currentGraphIdProperty;
  private final IntegerProperty totalGraphCountProperty;

  private static final String LAYOUT_INFO_TOOLTIP = "All nodes are user "
      + "positioned.\nAuto layout not possible.";

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
    this.replayBtnDisableProperty = new SimpleBooleanProperty();
    this.cancelReplayBtnDisableProperty = new SimpleBooleanProperty();
    this.speedSliderDisableProperty = new SimpleBooleanProperty();
    this.autoLayoutBtnDisableProperty = new SimpleBooleanProperty();

    this.currentGraphIdProperty = new SimpleIntegerProperty();
    this.totalGraphCountProperty = new SimpleIntegerProperty();

    this.isReplayingProperty = new SimpleBooleanProperty();
    this.layoutTooltip = new SimpleStringProperty();
  }

  public void changeCurrentGraphToNext() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
    currentSession.changeCurrentGraphToNext();
  }

  public void changeCurrentGraphToPrevious() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
    currentSession.changeCurrentGraphToPrev();
  }

  public void changeCurrentGraphToFirst() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
    currentSession.changeCurrentGraphToFirst();
  }

  public void changeCurrentGraphToLast() {
    logger.info("Changing the displayed graph model...");
    Session currentSession = sessionHolder.getCurrentSession();
    currentSession.changeCurrentGraphToLast();
  }

  public void updateButtonStates() {
    Session currentSession = sessionHolder.getCurrentSession();
    if (currentSession != null) {
      if (!isReplayingProperty().get()) {
        disableAllButtons(false);
        disableSpeedSlider(false);
        boolean isTreeSession = currentSession
            .getSessionType() instanceof TreeSessionType;
        if (isTreeSession) {
          disableLayoutButton(true);
        }
      }
      updateStepProperties(currentSession);
    }
  }

  /**
   * Update step progress bar with correct max index and correct current index.
   * Disable step buttons correctly.
   * 
   * @param currentSession
   */
  public void updateStepProperties(Session currentSession) {
    Graph currentGraph = currentSession.getGraphHolder().getCurrentGraph();

    int maxIndex = currentSession.getTotalGraphCount();
    totalGraphCountProperty.set(maxIndex);

    int currentIndex = currentGraph.getId();
    currentGraphIdProperty.set(currentIndex);

    if (!isReplayingProperty.get()) {
      if (currentIndex == 1 && maxIndex == 1) {
        disableStepButtons(true, true, true, true);
        disableReplayButtons(true, true);
      } else if (currentIndex <= 1) {
        disableStepButtons(true, true, false, false);
      } else if (currentIndex == maxIndex) {
        disableStepButtons(false, false, true, true);
      } else {
        disableStepButtons(false, false, false, false);
      }
    }
  }

  public void replayGraph(long timeout) {
    logger.info("Starting replay with speed {}", timeout);
    isReplayingProperty.set(true);

    boolean disable = true;
    disableStepButtons(disable, disable, disable, disable);
    disableLayoutButton(disable);
    disableSpeedSlider(disable);
    sessionHolder.getCurrentSession().replay(timeout, this::finishReplay);
  }

  public void pauseReplay() {
    sessionHolder.getCurrentSession().pauseReplay();
    isReplayingProperty.set(false);
    updateButtonStates();
  }

  public void cancelReplay() {
    // no need to call updateButtonState here
    // that call is made when switching to the first graph after canceling
    // replay
    isReplayingProperty.set(false);
    sessionHolder.getCurrentSession().cancelReplay();
  }

  public void finishReplay() {
    Platform.runLater(() -> {
      isReplayingProperty.set(false);
      updateButtonStates();
    });
  }

  /**
   * Request auto layout of the layout engine.
   * 
   * @param useRandomLayout
   *          use random positions
   */
  public void autoLayout(boolean useRandomLayout) {
    disableAllButtons(true);
    Session currentSession = sessionHolder.getCurrentSession();
    Graph currentGraph = currentSession.getGraphHolder().getCurrentGraph();

    // reset
    currentGraph.setLayouted(false);

    if (currentSession.getSessionType() instanceof GraphSessionType
        && currentGraph.getVertices().stream()
            .anyMatch(v -> !v.isUserPositioned())) {

      ILayouter layouter = currentSession.getSessionType().getLayouter();
      layouter.layout(currentGraph, useRandomLayout, this::updateButtonStates);
    } else {
      layoutTooltip.set(LAYOUT_INFO_TOOLTIP);
      autoLayoutBtnDisableProperty.set(true);
      updateButtonStates();
    }
  }

  private void disableAllButtons(boolean disable) {
    disableStepButtons(disable, disable, disable, disable);
    disableReplayButtons(disable, disable);
    disableLayoutButton(disable);
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

  private void disableLayoutButton(boolean disable) {
    autoLayoutBtnDisableProperty.set(disable);
  }

  private void disableSpeedSlider(boolean disable) {
    speedSliderDisableProperty.set(disable);
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

  public BooleanProperty speedSliderDisableProperty() {
    return speedSliderDisableProperty;
  }

  public BooleanProperty isReplayingProperty() {
    return isReplayingProperty;
  }

  public StringProperty layoutTooltip() {
    return layoutTooltip;
  }
}
