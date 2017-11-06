package gvs.ui.view.session;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

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
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import jfxtras.labs.scene.layout.ScalableContentPane;

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

  private double dragOriginalSceneX;
  private double dragOriginalSceneY;

  private Set<Label> labels;
  private final GraphViewModel graphViewModel;
  private final SessionViewModel sessionViewModel;

  private static final int DEFAULT_REPLAY_TIMEOUT = 1000;
  private static final String EDGE_LABEL = "edge-label" ;
  private static final Logger logger = LoggerFactory
      .getLogger(SessionView.class);

  @Inject
  public SessionView(GraphViewModel graphViewModel,
      SessionViewModel sessionViewModel) {
    this.graphViewModel = graphViewModel;
    this.sessionViewModel = sessionViewModel;

    graphViewModel.addObserver(this);
  }

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    initializeReplaySlider();
    initializeStepIndicator();
    initializeButtons();
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

  @Override
  public void update(Observable o, Object arg) {
    Platform.runLater(() -> redraw(graphViewModel));
  }

  private void redraw(GraphViewModel graphViewModel) {
    logger.info("redraw graph pane");
    graphPane.getContentPane().getChildren().clear();
    
    labels = new HashSet<>();
    drawEdges(graphViewModel.getEdgeViewModels());
    drawVertices(graphViewModel.getVertexViewModels());
    bringLabelsToFront();

    graphPane.requestScale();
  }

  private void drawVertices(Collection<VertexViewModel> vertexViewModels) {
    vertexViewModels.forEach(v -> {
      Circle circle = new Circle();
      circle.setCursor(Cursor.HAND);
      circle.setRadius(6);
      circle.centerXProperty().bindBidirectional(v.getXProperty());
      circle.centerYProperty().bindBidirectional(v.getYProperty());

      circle.setOnMousePressed(e -> {
        circle.setCursor(Cursor.MOVE);
        dragOriginalSceneX = e.getSceneX();
        dragOriginalSceneY = e.getSceneY();

        ((Circle) (e.getSource())).toFront();
      });

      circle.setOnMouseDragged(e -> {
        double offsetX = e.getSceneX() - dragOriginalSceneX;
        double offsetY = e.getSceneY() - dragOriginalSceneY;

        Circle c = (Circle) (e.getSource());
        double newX = c.getCenterX() + offsetX;
        double newY = c.getCenterY() + offsetY;

        newX = checkXBoundaries(c, newX);
        newY = checkYBoundaries(c, newY);

        c.setCenterX(newX);
        c.setCenterY(newY);

        dragOriginalSceneX = e.getSceneX();
        dragOriginalSceneY = e.getSceneY();
      });

      graphPane.getContentPane().getChildren().add(circle);
    });
  }

  private double checkXBoundaries(Circle circle, double newX) {
    double minimum = circle.getRadius();
    double maximumX = graphPane.getBoundsInLocal().getWidth();

    if (newX < minimum) {
      newX = minimum;
    }
    if (newX > maximumX) {
      newX = maximumX;
    }
    return newX;
  }

  private double checkYBoundaries(Circle circle, double newY) {
    double minimum = circle.getRadius();
    double maximumY = graphPane.getBoundsInLocal().getHeight();

    if (newY < minimum) {
      newY = minimum;
    }
    if (newY > maximumY) {
      newY = maximumY;
    }
    return newY;
  }

  private void drawEdges(Collection<EdgeViewModel> edgeViewModels) {
    edgeViewModels.forEach(e -> {
      double startX = e.getStartVertex().getXProperty().get();
      double startY = e.getStartVertex().getYProperty().get();
      double endX = e.getEndVertex().getXProperty().get();
      double endY = e.getEndVertex().getYProperty().get();

      Line line = new Line(startX, startY, endX, endY);
      line.setStrokeWidth(e.getStyle().getLineThickness());
      line.setStroke(e.getStyle().getLineColor());
      line.getStrokeDashArray().addAll(e.getStyle().getLineStyle());

      Label l = new Label();
      l.textProperty().bind(e.labelProperty());
      l.getStyleClass().add(EDGE_LABEL);
      labels.add(l);
      bindToMiddle(line, l);

      line.startXProperty()
          .bindBidirectional(e.getStartVertex().getXProperty());
      line.startYProperty()
          .bindBidirectional(e.getStartVertex().getYProperty());
      line.endXProperty().bindBidirectional(e.getEndVertex().getXProperty());
      line.endYProperty().bindBidirectional(e.getEndVertex().getYProperty());

      graphPane.getContentPane().getChildren().addAll(line, l);
    });
  }
  
  private void bringLabelsToFront() {
    labels.forEach(l -> l.toFront());
  }

  private void bindToMiddle(Line line, Label l) {
    l.translateXProperty().bind(
        ((line.startXProperty().add(line.endXProperty())).divide(2)).subtract(
            (l.layoutXProperty().add(l.layoutYProperty()).divide(2))));
    l.translateYProperty().bind(
        ((line.startYProperty().add(line.endYProperty())).divide(2)).subtract(
            (l.layoutXProperty().add(l.layoutYProperty()).divide(2))));
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
