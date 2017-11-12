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
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
@Singleton
public class SessionView{

  @FXML
  private GridPane playGrid;

  @FXML
  private Label stepLabel;

  @FXML
  private GridPane stepButtons;

  @FXML
  private TextArea descriptionField;

  @FXML
  private Button autoLayoutBtn;

  @FXML
  private ScalableContentPane graphPane;

  @FXML
  private Button firstBtn;

  @FXML
  private Button prevBtn;

  @FXML
  private Button nextBtn;

  @FXML
  private Button lastBtn;

  @FXML
  private Button playBtn;

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
    int margin = 5;
    AnchorPane.setBottomAnchor(stepProgressBar, (double) margin);
    AnchorPane.setLeftAnchor(stepProgressBar, (double) margin);
    AnchorPane.setRightAnchor(stepProgressBar, (double) margin);

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

    autoLayoutBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getAutoLayoutBtnDisableProperty());
    playBtn.disableProperty()
        .bindBidirectional(sessionViewModel.getReplayBtnDisableProperty());
  }

  private void setButtonIcons() {
    firstBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_BACKWARD));
    prevBtn.setGraphic(FontAwesome.createLabel(Glyph.BACKWARD));
    nextBtn.setGraphic(FontAwesome.createLabel(Glyph.FORWARD));
    lastBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_FORWARD));

    playBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
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
    long sliderDelay = (long) speedSlider.getValue();
    sessionViewModel.replayGraph(sliderDelay);
  }

  @FXML
  private void autoLayout() {
    sessionViewModel.autoLayout();
  }
}
