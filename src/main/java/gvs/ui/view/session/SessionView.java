package gvs.ui.view.session;

import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.ui.logic.session.SessionViewModel;
import gvs.ui.model.graph.EdgeViewModel;
import gvs.ui.model.graph.GraphViewModel;
import gvs.ui.model.graph.VertexViewModel;
import gvs.ui.view.controls.StepProgressBar;
import gvs.util.FontAwesome;
import gvs.util.FontAwesome.Glyph;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
@Singleton
public class SessionView implements Observer {

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
  private AnchorPane graphPane;

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

  @Inject
  private GraphViewModel graphViewModel;

  @Inject
  private SessionViewModel sessionViewModel;

  private static final int DEFAULT_REPLAY_SPEED = 500;
  private static final Logger logger = LoggerFactory
      .getLogger(SessionView.class);

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    graphViewModel.addObserver(this);

    initStepButtons();
    playBtn.setGraphic(FontAwesome.createLabel(Glyph.PLAY));
    initSlider();
    initStepIndicator();
    bindStepIndicator();
  }

  private void initStepIndicator() {
    stepProgressBar = new StepProgressBar();
    leftPanel.getChildren().add(stepProgressBar);
    int margin = 5;
    AnchorPane.setBottomAnchor(stepProgressBar, (double) margin);
    AnchorPane.setLeftAnchor(stepProgressBar, (double) margin);
    AnchorPane.setRightAnchor(stepProgressBar, (double) margin);
  }

  private void initSlider() {
    speedSlider.setValue(DEFAULT_REPLAY_SPEED);
  }

  private void initStepButtons() {
    firstBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_BACKWARD));
    prevBtn.setGraphic(FontAwesome.createLabel(Glyph.BACKWARD));
    nextBtn.setGraphic(FontAwesome.createLabel(Glyph.FORWARD));
    lastBtn.setGraphic(FontAwesome.createLabel(Glyph.STEP_FORWARD));
  }

  private void redraw(GraphViewModel graphViewModel) {
    drawVertices(graphViewModel.getVertexViewModels());
    drawEdges(graphViewModel.getEdgeViewModels());
  }

  private void drawVertices(Collection<VertexViewModel> vertexViewModels) {
    vertexViewModels.forEach(v -> {
      Circle circle = new Circle();
      circle.setRadius(20);
      circle.centerXProperty().bindBidirectional(v.getXProperty());
      circle.centerYProperty().bindBidirectional(v.getYProperty());

      circle.setOnMouseDragged(e -> {
        circle.setCenterX(e.getSceneX());
        circle.setCenterY(e.getSceneY());
        e.consume();
      });

      graphPane.getChildren().add(circle);
    });
  }

  private void drawEdges(Collection<EdgeViewModel> edgeViewModels) {
    edgeViewModels.forEach(e -> {
      double startX = e.getStartVertex().getXProperty().get();
      double startY = e.getStartVertex().getYProperty().get();
      double endX = e.getEndVertex().getXProperty().get();
      double endY = e.getEndVertex().getYProperty().get();

      Line line = new Line(startX, startY, endX, endY);
      line.setStrokeWidth(5);

      line.startXProperty()
          .bindBidirectional(e.getStartVertex().getXProperty());
      line.startYProperty()
          .bindBidirectional(e.getStartVertex().getYProperty());
      line.endXProperty().bindBidirectional(e.getEndVertex().getXProperty());
      line.endYProperty().bindBidirectional(e.getEndVertex().getYProperty());

      graphPane.getChildren().add(line);
    });
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
    sessionViewModel.replayGraph(speedSlider.getValue());
  }

  @FXML
  private void autoLayout() {
    sessionViewModel.autoLayout();
  }

  private void bindStepIndicator() {
    stepProgressBar.totalStepProperty()
        .bind(sessionViewModel.totalGraphCountProperty());
    stepProgressBar.currentStepProperty()
        .bind(sessionViewModel.currentGraphModelIdProperty());
  }

  @Override
  public void update(Observable o, Object arg) {
    GraphViewModel viewModel = (GraphViewModel) graphViewModel;
    redraw(viewModel);
  }
}
