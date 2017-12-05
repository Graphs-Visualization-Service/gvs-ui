package gvs.ui.view.session;

import org.controlsfx.control.ToggleSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.ui.logic.session.SessionViewModel;
import gvs.ui.model.GraphViewModel;
import gvs.ui.view.ScalablePane;
import gvs.ui.view.controls.StepProgressBar;
import gvs.util.FontAwesome;
import gvs.util.FontAwesome.Glyph;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
@Singleton
public class SessionView {
  @FXML
  private BorderPane sessionRoot;

  @FXML
  private ScalablePane graphPane;

  @FXML
  private Button autoLayoutBtn;

  @FXML
  private ToggleSwitch randomLayoutSwitch;

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

  @FXML
  private TextArea snapshotDescription;

  private final Tooltip layoutTooltip;
  private final GraphViewModel graphViewModel;
  private final SessionViewModel sessionViewModel;
  private final StepProgressBar progressBarComponent;

  private static final int ONE_SECOND_MS = 1000;
  private static final double SLIDER_MIN = 0.0;
  private static final double SLIDER_DEFAULT = 1.0;
  private static final double SLIDER_MAX = 2.0;

  private static final String DEFAULT_RANDOM_SWITCH_TOOLTIP = "Use Random coordinates";
  private static final String DEFAULT_CANCEL_BTN_TOOLTIP = "Cancel replay";
  private static final String DEFAULT_REPLAY_BTN_TOOLTIP = "Replay session";
  private static final String DEFAULT_LAST_BTN_TOOLTIP = "Show last session";
  private static final String DEFAULT_NEXT_BTN_TOOLTIP = "Show next session";
  private static final String DEFAULT_PREV_BTN_TOOLTIP = "Show previous session";
  private static final String DEFAULT_FIRST_BTN_TOOLTIP = "Show first session";
  private static final String DEFAULT_LAYOUT_TOOLTIP = "Layout current graph";

  private static final String DEFAULT_SNAPSHOP_PROMPT_TXT = "Snapshot description";

  private static final Logger logger = LoggerFactory
      .getLogger(SessionView.class);

  @Inject
  public SessionView(GraphViewModel graphViewModel,
      SessionViewModel sessionViewModel, StepProgressBar progressBarComponent) {

    logger.info("Initiating SessionView...");
    this.graphViewModel = graphViewModel;
    this.sessionViewModel = sessionViewModel;
    this.progressBarComponent = progressBarComponent;
    this.layoutTooltip = new Tooltip();
  }

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    initializeTooltips();
    clipRootPane();
    initializeReplaySlider();
    initializeStepIndicator();
    initializeButtons();
    bindReplayIcons();

    graphPane.setAutoRescale(false);

    graphViewModel.setPane(graphPane);

    // Snapshot description for graphs
    snapshotDescription.setPromptText(DEFAULT_SNAPSHOP_PROMPT_TXT);
    snapshotDescription.textProperty()
        .bindBidirectional(graphViewModel.snapshotDescriptionProperty());
  }

  /**
   * Triggers if the text changes and show the new tooltip
   * 
   * @param observable
   *          observable
   * @param oldValue
   *          old tooltip string
   * @param newValue
   *          new tooltip string
   */
  private void layoutTooltipListener(
      ObservableValue<? extends String> observable, String oldValue,
      String newValue) {

    if (oldValue != null && newValue != null) {
      if (!DEFAULT_LAYOUT_TOOLTIP.equals(newValue)) {
        layoutTooltip.setAutoHide(true);

        double xPos = autoLayoutBtn.getLayoutX() - 20;
        double yPos = autoLayoutBtn.getLayoutY() + 5;
        Point2D point = autoLayoutBtn.localToScreen(xPos, yPos);
        layoutTooltip.show(autoLayoutBtn, point.getX(), point.getY());
        layoutTooltip.setOnHiding(e -> {
          layoutTooltip.getStyleClass().remove("info-tooltip");
          layoutTooltip.setText(DEFAULT_LAYOUT_TOOLTIP);
        });
      }
    }
  }

  /*
   * Prohibits child elements to overlap the bounds of the root pane.
   */
  private void clipRootPane() {
    final Rectangle clip = new Rectangle();
    clip.setWidth(graphPane.getWidth());
    clip.setHeight(graphPane.getHeight());
    sessionRoot.setClip(clip);

    sessionRoot.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
      clip.setWidth(newValue.getWidth());
      clip.setHeight(newValue.getHeight());
    });
  }

  private void initializeStepIndicator() {
    leftPanel.getChildren().add(progressBarComponent);
    double margin = 5.0;
    AnchorPane.setBottomAnchor(progressBarComponent, margin);
    AnchorPane.setLeftAnchor(progressBarComponent, margin);
    AnchorPane.setRightAnchor(progressBarComponent, margin);

    progressBarComponent.totalStepProperty()
        .bind(Bindings.convert(sessionViewModel.totalGraphCountProperty()));
    progressBarComponent.currentStepProperty()
        .bind(Bindings.convert(sessionViewModel.currentGraphIdProperty()));
  }

  private void initializeReplaySlider() {
    speedSlider.setMin(SLIDER_MIN);
    speedSlider.setMax(SLIDER_MAX);
    speedSlider.setValue(SLIDER_DEFAULT);
    speedSlider.setMinorTickCount(4);
    speedSlider.setMajorTickUnit(1);
    speedSlider.setSnapToTicks(false);
    speedSlider.setShowTickMarks(true);
    speedSlider.setShowTickLabels(true);
    speedSlider.setLabelFormatter(new ReplaySliderStringConverter());
  }

  private void initializeTooltips() {
    layoutTooltip.textProperty()
        .bindBidirectional(sessionViewModel.layoutTooltip());
    layoutTooltip.textProperty().addListener(this::layoutTooltipListener);
    layoutTooltip.setText(DEFAULT_LAYOUT_TOOLTIP);
  }

  private void initializeButtons() {
    setButtonIcons();
    bindDisableProperties();
  }

  private void bindDisableProperties() {
    firstBtn.disableProperty()
        .bind(sessionViewModel.firstBtnDisableProperty());
    lastBtn.disableProperty()
        .bind(sessionViewModel.lastBtnDisableProperty());
    nextBtn.disableProperty()
        .bind(sessionViewModel.nextBtnDisableProperty());
    prevBtn.disableProperty()
        .bind(sessionViewModel.prevBtnDisableProperty());

    replayBtn.disableProperty()
        .bind(sessionViewModel.replayBtnDisableProperty());
    cancelReplayBtn.disableProperty()
        .bind(sessionViewModel.cancelReplayBtnDisableProperty());
    speedSlider.disableProperty().bind(sessionViewModel.speedSliderDisableProperty());
    autoLayoutBtn.disableProperty()
        .bind(sessionViewModel.autoLayoutBtnDisableProperty());
  }

  private void bindReplayIcons() {
    sessionViewModel.isReplayingProperty()
        .addListener((ObservableValue<? extends Boolean> observable,
            Boolean oldValue, Boolean newValue) -> {
          if (newValue) {
            replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PAUSE));
          } else {
            replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
          }
        });
  }

  private void setButtonIcons() {
    firstBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_BACKWARD));
    firstBtn.setTooltip(new Tooltip(DEFAULT_FIRST_BTN_TOOLTIP));

    prevBtn.setGraphic(FontAwesome.createLabel(Glyph.BACKWARD));
    prevBtn.setTooltip(new Tooltip(DEFAULT_PREV_BTN_TOOLTIP));

    nextBtn.setGraphic(FontAwesome.createLabel(Glyph.FORWARD));
    nextBtn.setTooltip(new Tooltip(DEFAULT_NEXT_BTN_TOOLTIP));

    lastBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_FORWARD));
    lastBtn.setTooltip(new Tooltip(DEFAULT_LAST_BTN_TOOLTIP));

    autoLayoutBtn.setGraphic(FontAwesome.createLabel(Glyph.MAGIC));
    autoLayoutBtn.setTooltip(layoutTooltip);

    replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
    replayBtn.setTooltip(new Tooltip(DEFAULT_REPLAY_BTN_TOOLTIP));

    cancelReplayBtn.setGraphic(FontAwesome.createLabel(Glyph.STOP));
    cancelReplayBtn.setTooltip(new Tooltip(DEFAULT_CANCEL_BTN_TOOLTIP));

    randomLayoutSwitch.setTooltip(new Tooltip(DEFAULT_RANDOM_SWITCH_TOOLTIP));
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
    if (sessionViewModel.isReplayingProperty().get()) {
      sessionViewModel.pauseReplay();
    } else {
      long sliderDelay = (long) ((SLIDER_MAX + 0.01 - speedSlider.getValue())
          * ONE_SECOND_MS);
      sessionViewModel.replayGraph(sliderDelay);
    }
  }

  @FXML
  private void cancelReplayGraph() {
    sessionViewModel.cancelReplay();
    replayBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
  }

  @FXML
  private void autoLayout() {
    boolean useRandomLayout = randomLayoutSwitch.isSelected();
    sessionViewModel.autoLayout(useRandomLayout);
  }
  
  public BooleanProperty isReplayingProperty() {
    return sessionViewModel.isReplayingProperty();
  }
}
