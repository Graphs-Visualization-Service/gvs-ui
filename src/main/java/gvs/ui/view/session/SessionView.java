package gvs.ui.view.session;

import gvs.ui.logic.session.SessionViewModel;
import gvs.util.FontAwesome;
import gvs.util.StepProgressBar;
import gvs.util.FontAwesome.Glyph;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
public class SessionView {
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
  private AnchorPane modelPane;

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
  private SessionViewModel sessionViewModel;
  private int replaySpeed;

  private static final int DEFAULT_REPLAY_SPEED = 500;

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    initStepButtons();
    playBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
    initSlider();
    initStepIndicator();
  }

  private void initStepIndicator() {
    stepProgressBar = new StepProgressBar();
    leftPanel.getChildren().add(stepProgressBar);
    setAnchors(stepProgressBar, 0, 5, 5, 5);
  }

  private void initSlider() {
    replaySpeed = DEFAULT_REPLAY_SPEED;
    speedSlider.setValue(replaySpeed);
    speedSlider.valueProperty()
        .addListener((observable, oldValue, newValue) -> {
          replaySpeed = newValue.intValue();
        });
  }

  private void initStepButtons() {
    firstBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_BACKWARD));
    prevBtn.setGraphic(FontAwesome.createLabel(Glyph.BACKWARD));
    nextBtn.setGraphic(FontAwesome.createLabel(Glyph.FORWARD));
    lastBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_FORWARD));
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
    sessionViewModel.replayGraph(replaySpeed);
  }

  @FXML
  private void autoLayout() {
    sessionViewModel.autoLayout();
  }

  public void setViewModel(SessionViewModel viewModel) {
    this.sessionViewModel = viewModel;
    bindStepIndicator();
  }

  private void bindStepIndicator() {
    stepProgressBar.totalStepProperty()
        .bind(sessionViewModel.totalGraphCountProperty());
    stepProgressBar.currentStepProperty()
        .bind(sessionViewModel.currentGraphModelIdProperty());
  }

  /**
   * Helper function. Set anchors for a child of an AnchorPane.
   * 
   * @param top
   * @param bottom
   * @param left
   * @param right
   */
  // TODO: extract method from here and appviewmodel into own util class?
  private void setAnchors(Node anchorChild, int top, int bottom, int left,
      int right) {
    AnchorPane.setTopAnchor(anchorChild, (double) top);
    AnchorPane.setBottomAnchor(anchorChild, (double) bottom);
    AnchorPane.setLeftAnchor(anchorChild, (double) left);
    AnchorPane.setRightAnchor(anchorChild, (double) right);
  }
}
