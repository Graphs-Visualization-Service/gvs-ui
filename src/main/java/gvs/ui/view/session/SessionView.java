package gvs.ui.view.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.ui.logic.session.SessionViewModel;
import gvs.ui.model.graph.GraphViewModel;
import gvs.ui.view.controls.StepProgressBar;
import gvs.util.FontAwesome;
import gvs.util.FontAwesome.Glyph;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
@Singleton
public class SessionView {

  @FXML
  private ScalableContentPane graphPane;

  @FXML
  private Button autoLayoutBtn;

  @FXML
  private Button firstBtn;

  @FXML
  private Button prevBtn;

  @FXML
  private Button nextBtn;

  @FXML
  private Button lastBtn;

  @FXML
  private Button replayBtn;

  @FXML
  private Button cancelReplayBtn;

  @FXML
  private Slider speedSlider;

  @FXML
  private AnchorPane leftPanel;

  private StepProgressBar stepProgressBar;

  private final GraphViewModel graphViewModel;
  private final SessionViewModel sessionViewModel;

  private static final int DEFAULT_REPLAY_TIMEOUT = 1000;
  private static final Logger logger = LoggerFactory
      .getLogger(SessionView.class);

  @Inject
  public SessionView(GraphViewModel graphViewModel,
      SessionViewModel sessionViewModel) {

    logger.info("Initiating SessionView...");
    this.graphViewModel = graphViewModel;
    this.sessionViewModel = sessionViewModel;
  }

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    initializeReplaySlider();
    initializeStepIndicator();
    initializeButtons();
    graphViewModel.setPane(graphPane);
  }

  private void initializeStepIndicator() {
    stepProgressBar = new StepProgressBar();
    leftPanel.getChildren().add(stepProgressBar);
    double margin = 5.0;
    AnchorPane.setBottomAnchor(stepProgressBar, margin);
    AnchorPane.setLeftAnchor(stepProgressBar, margin);
    AnchorPane.setRightAnchor(stepProgressBar, margin);

    stepProgressBar.totalStepProperty()
        .bind(Bindings.convert(sessionViewModel.totalGraphCountProperty()));
    stepProgressBar.currentStepProperty()
        .bind(Bindings.convert(sessionViewModel.currentGraphIdProperty()));
  }

  private void initializeReplaySlider() {
    speedSlider.setValue(DEFAULT_REPLAY_TIMEOUT);
  }

  private void initializeButtons() {
    setButtonIcons();
    bindButtonDisable();
  }

  private void bindButtonDisable() {
    firstBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getFirstBtnDisableProperty());
    lastBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getLastBtnDisableProperty());
    nextBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getNextBtnDisableProperty());
    prevBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getPrevBtnDisableProperty());

    replayBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getReplayBtnDisableProperty());
    cancelReplayBtn.disableProperty().bindBidirectional(
        sessionViewModel.getCancelReplayBtnDisableProperty());

    autoLayoutBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getAutoLayoutBtnDisableProperty());
  }

  private void setButtonIcons() {
    firstBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_BACKWARD));
    firstBtn.setTooltip(new Tooltip("Show first session"));

    prevBtn.setGraphic(FontAwesome.createLabel(Glyph.BACKWARD));
    prevBtn.setTooltip(new Tooltip("Show previous session"));

    nextBtn.setGraphic(FontAwesome.createLabel(Glyph.FORWARD));
    nextBtn.setTooltip(new Tooltip("Show next session"));

    lastBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_FORWARD));
    lastBtn.setTooltip(new Tooltip("Show last session"));

    autoLayoutBtn.setGraphic(FontAwesome.createLabel(Glyph.MAGIC));
    autoLayoutBtn.setTooltip(new Tooltip("Autolayout current graph"));

    replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
    replayBtn.setTooltip(new Tooltip("Replay session"));

    cancelReplayBtn.setGraphic(FontAwesome.createLabel(Glyph.STOP));
    cancelReplayBtn.setTooltip(new Tooltip("Cancel replay"));
  }

  @FXML
  private void stepForward() {
    sessionViewModel.changeCurrentGraphToNext();
  }

  @FXML
  private void stepBackward() {
    sessionViewModel.changeCurrentGraphToPrevious();
  }

  @FXML
  private void stepToFirst() {
    sessionViewModel.changeCurrentGraphToFirst();
  }

  @FXML
  private void stepToLast() {
    sessionViewModel.changeCurrentGraphToLast();
  }

  @FXML
  private void replayGraph() {
    if (sessionViewModel.isReplaying()) {
      sessionViewModel.pauseReplay();
      sessionViewModel.setReplaying(false);
      replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
    } else {
      long sliderDelay = (long) speedSlider.getValue();
      sessionViewModel.replayGraph(sliderDelay);
      sessionViewModel.setReplaying(true);
      replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PAUSE));
    }
  }

  @FXML
  private void cancelReplayGraph() {
    sessionViewModel.cancelReplay();
    replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
  }

  @FXML
  private void autoLayout() {
    sessionViewModel.autoLayout();
  }
}
