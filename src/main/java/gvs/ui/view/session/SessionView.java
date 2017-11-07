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
import gvs.ui.model.shapes.LabeledNode;
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
import javafx.scene.Scene;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
  private static final String EDGE_LABEL = "edge-label";
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
      LabeledNode node = new LabeledNode(v.labelProperty());
      node.setCursor(Cursor.HAND);
      node.centerXProperty().bindBidirectional(v.xProperty());
      node.centerYProperty().bindBidirectional(v.yProperty());

      node.setOnMousePressed(e -> {
        node.setCursor(Cursor.MOVE);
        dragOriginalSceneX = e.getSceneX();
        dragOriginalSceneY = e.getSceneY();
      });

      node.setOnMouseDragged(e -> {
        double offsetX = e.getSceneX() - dragOriginalSceneX;
        double offsetY = e.getSceneY() - dragOriginalSceneY;

        LabeledNode n = (LabeledNode) (e.getSource());
        double newX = n.getCenterX() + offsetX;
        double newY = n.getCenterY() + offsetY;

        newX = checkXBoundaries(n, newX);
        newY = checkYBoundaries(n, newY);

        n.setCenterX(newX);
        n.setCenterY(newY);

        dragOriginalSceneX = e.getSceneX();
        dragOriginalSceneY = e.getSceneY();
      });

      graphPane.getContentPane().getChildren().add(node);

      // Circle circle = new Circle();
      // circle.setCursor(Cursor.HAND);
      // circle.setRadius(6);
      // circle.centerXProperty().bindBidirectional(v.xProperty());
      // circle.centerYProperty().bindBidirectional(v.yProperty());

//      Text text = new Text();
//      text.textProperty().bind(v.labelProperty());
//      text.getStyleClass().add("circle");
      // label.translateXProperty().bindBidirectional(v.xProperty());
      // label.translateYProperty().bindBidirectional(v.yProperty());
      //

      // Font font = Font.font("Arial", 20);
      // label.setFont(font);

      //
      //
      // label.translateXProperty().bind(
      // v.xProperty().subtract(label.getLayoutBounds().getWidth() / 2));
      // label.translateYProperty()
      // .bind(v.yProperty().add(label.getLayoutBounds().getHeight() / 2));

      // bindToMiddle(circle, label);
      // labels.add(label);

      // circle.setOnMousePressed(e -> {
      // circle.setCursor(Cursor.MOVE);
      // dragOriginalSceneX = e.getSceneX();
      // dragOriginalSceneY = e.getSceneY();
      // });
      //
      // circle.setOnMouseDragged(e -> {
      // double offsetX = e.getSceneX() - dragOriginalSceneX;
      // double offsetY = e.getSceneY() - dragOriginalSceneY;
      //
      // LabeledNode n = (LabeledNode) (e.getSource());
      // double newX = n.getCenterX() + offsetX;
      // double newY = n.getCenterY() + offsetY;
      //
      // newX = checkXBoundaries(n, newX);
      // newY = checkYBoundaries(n, newY);
      //
      // n.setCenterX(newX);
      // n.setCenterY(newY);
      //
      // dragOriginalSceneX = e.getSceneX();
      // dragOriginalSceneY = e.getSceneY();
      // });
      //
      // graphPane.getContentPane().getChildren().add(circle);
    });
  }

  private double checkXBoundaries(LabeledNode node, double newX) {
    double minimum = node.getRadiusX();
    double maximumX = graphPane.getBoundsInLocal().getWidth();

    if (newX < minimum) {
      newX = minimum;
    }
    if (newX > maximumX) {
      newX = maximumX;
    }
    return newX;
  }

  private double checkYBoundaries(LabeledNode node, double newY) {
    double minimum = node.getRadiusY();
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
      double startX = e.getStartVertex().xProperty().get();
      double startY = e.getStartVertex().yProperty().get();
      double endX = e.getEndVertex().xProperty().get();
      double endY = e.getEndVertex().yProperty().get();

      Line line = new Line(startX, startY, endX, endY);
      line.setStrokeWidth(e.getStyle().getLineThickness());
      line.setStroke(e.getStyle().getLineColor());
      line.getStrokeDashArray().addAll(e.getStyle().getLineStyle());

      Label label = new Label();
      label.textProperty().bind(e.labelProperty());
      label.getStyleClass().add(EDGE_LABEL);
      labels.add(label);
      bindToMiddle(line, label);

      line.startXProperty().bindBidirectional(e.getStartVertex().xProperty());
      line.startYProperty().bindBidirectional(e.getStartVertex().yProperty());
      line.endXProperty().bindBidirectional(e.getEndVertex().xProperty());
      line.endYProperty().bindBidirectional(e.getEndVertex().yProperty());

      graphPane.getContentPane().getChildren().addAll(line, label);
    });
  }

  private void bringLabelsToFront() {
    labels.forEach(l -> l.toFront());
  }

  private void bindToMiddle(Line line, Label label) {
    label.translateXProperty().bind(
        ((line.startXProperty().add(line.endXProperty())).divide(2)).subtract(
            (label.layoutXProperty().add(label.layoutYProperty()).divide(2))));
    label.translateYProperty().bind(
        ((line.startYProperty().add(line.endYProperty())).divide(2)).subtract(
            (label.layoutXProperty().add(label.layoutYProperty()).divide(2))));
  }

  private void bindToMiddle(Circle circle, Label label) {
    label.translateXProperty().bind(circle.centerXProperty());
    label.translateYProperty().bind(circle.centerYProperty());
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
