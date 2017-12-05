package gvs.ui.model;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.Configuration;
import gvs.business.model.IVertex;
import gvs.business.model.graph.GraphVertex;
import gvs.business.model.styles.GVSStyle;
import gvs.ui.view.ScalablePane;
import gvs.util.ContrastColor;
import gvs.util.FontAwesome;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Contains JavaFX Properties which are used for bidirectional bindings.
 * 
 * @author mwieland
 */
public class VertexViewModel implements Observer {

  private double dragOriginalSceneX;
  private double dragOriginalSceneY;

  private final IVertex vertex;
  private final Label label;
  private final Ellipse ellipse;

  private static final Logger logger = LoggerFactory
      .getLogger(VertexViewModel.class);

  /**
   * Create a new DefaultVertexViewModel with the corresponding Vertex.
   * 
   * @param vertex
   *          JavaFX independent vertex representation
   */
  public VertexViewModel(IVertex vertex) {
    this.vertex = vertex;
    this.ellipse = new Ellipse();
    this.label = new Label();

    setLabelConstraints();

    // bidirectional connection
    this.vertex.addObserver(this);
    ellipse.centerXProperty().addListener(this::xPropertyListener);
    ellipse.centerYProperty().addListener(this::yPropertyListener);

    // setup label text and optional icon
    if (vertex.getIcon() != null) {
      logger.info("Creating VertexViewModel with an icon");
      label.setGraphic(FontAwesome.createLabel(vertex.getIcon(),
          Configuration.getIconFontSize()));
    }
    label.setText(vertex.getLabel());
    if (!vertex.isTreeVertex()) {
      label.setCursor(Cursor.HAND);
    }

    updateCoordinates(vertex.getXPosition(), vertex.getYPosition());

    setStyles();
  }

  private void setLabelConstraints() {
    label.setMaxWidth(vertex.getMaxLabelLength());
    label.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
  }

  /**
   * Set correct CSS classes
   */
  private void setStyles() {
    GVSStyle style = vertex.getStyle();

    String fillColor = style.getFillColor().getColor();
    String lineColor = style.getLineColor().getColor();
    String lineStyle = style.getLineStyle().getStyle();
    String lineThickness = style.getLineThickness().getThickness();

    label.getStyleClass().add("vertex");

    ellipse.getStyleClass().add("fill-" + fillColor);
    ellipse.getStyleClass().add("line-" + lineColor);
    ellipse.getStyleClass().add(lineStyle + "-" + lineThickness);
  }

  /**
   * Inform the business logic {@link GraphVertex} about changes, made in the
   * UI. (e.g. drag vertex)
   * 
   * @param observable
   *          observable value
   * @param oldValue
   *          value before the change
   * @param newValue
   *          value after the change
   */
  private void xPropertyListener(ObservableValue<? extends Number> observable,
      Number oldValue, Number newValue) {
    if (!equalDouble(oldValue.doubleValue(), newValue.doubleValue())) {
      double newX = (double) newValue;
      vertex.setXPosition(newX);
    }
  }

  /**
   * Inform the business logic {@link GraphVertex} about changes, made in the
   * UI. (e.g. drag vertex)
   * 
   * @param observable
   *          observable value
   * @param oldValue
   *          value before the change
   * @param newValue
   *          value after the change
   */
  private void yPropertyListener(ObservableValue<? extends Number> observable,
      Number oldValue, Number newValue) {
    if (!equalDouble(oldValue.doubleValue(), newValue.doubleValue())) {
      double newY = (double) newValue;
      vertex.setYPosition(newY);
    }
  }

  /**
   * Create the binding from the business logic {@link GraphVertex} to the
   * presentation {@link VertexViewModel} properties.
   * 
   * As soon as any {@link GraphVertex} field is updated, the corresponding
   * property will be notified.
   * 
   * @param o
   *          updated {@link GraphVertex} object
   * @param arg
   *          additional arguments
   */
  @Override
  public void update(Observable o, Object arg) {
    // Hand updates over to JavaFX Thread
    Platform.runLater(() -> {
      updateCoordinates(vertex.getXPosition(), vertex.getYPosition());
    });
  }

  /**
   * Update coordinates of the ellipse.
   * 
   * @param x
   *          x position
   * @param y
   *          y position
   */
  private void updateCoordinates(double x, double y) {
    ellipse.setCenterX(x);
    ellipse.setCenterY(y);
  }

  /**
   * Draw vertex on given ScaleableContentPane
   * 
   * @param graphPane
   *          given scaleable pane
   */
  public void draw(ScalablePane graphPane) {
    logger.info("Drawing VertexViewModel.");
    graphPane.getContentPane().getChildren().addAll(ellipse, label);

    // this hack forces the label to compute its height and width
    label.applyCss();
    double xRadius = Math.min(label.prefWidth(-1), label.getMaxWidth()) / 2;
    double yRadius = label.prefHeight(-1) / 2;

    ellipse.setRadiusX(xRadius);
    ellipse.setRadiusY(yRadius);

    label.layoutXProperty().bind(ellipse.centerXProperty().subtract(xRadius));
    label.layoutYProperty().bind(ellipse.centerYProperty().subtract(yRadius));

    dragSupport(graphPane);

    correctLabelColor();
  }

  /**
   * Choose a label color with enough contrast to the background color.
   */
  private void correctLabelColor() {
    ellipse.applyCss();
    Color labelColor = ContrastColor
        .getContrastColor((Color) ellipse.getFill());
    label.setTextFill(labelColor);
    if (label.getGraphic() != null) {
      Label icon = (Label) label.getGraphic();
      icon.setTextFill(labelColor);
    }
  }

  /**
   * Add drag support for graph vertices
   * 
   * @param graphPane
   *          parent pane
   */
  private void dragSupport(ScalablePane graphPane) {

    if (!vertex.isTreeVertex()) {

      logger.info("Adding drag support on VertexViewModel.");

      label.setOnMousePressed(e -> {
        dragOriginalSceneX = e.getSceneX()
            / graphPane.getContentScaleTransform().getX();
        dragOriginalSceneY = e.getSceneY()
            / graphPane.getContentScaleTransform().getY();
      });

      label.setOnMouseDragged(e -> {
        // logger level debug, because this will happen very often
        logger.debug("Mouse drag on VertexViewModel detected.");

        double offsetX = (e.getSceneX()
            / graphPane.getContentScaleTransform().getX()) - dragOriginalSceneX;
        double offsetY = (e.getSceneY()
            / graphPane.getContentScaleTransform().getY()) - dragOriginalSceneY;

        double newX = ellipse.getCenterX() + offsetX;
        double newY = ellipse.getCenterY() + offsetY;

        newX = checkXBoundaries(newX, graphPane);
        newY = checkYBoundaries(newY, graphPane);

        updateCoordinates(newX, newY);

        vertex.setUserPositioned(true);

        // remember last coordinates
        dragOriginalSceneX = e.getSceneX()
            / graphPane.getContentScaleTransform().getX();
        dragOriginalSceneY = e.getSceneY()
            / graphPane.getContentScaleTransform().getY();
      });
    }
  }

  /**
   * Check whether new x coordinate is within the boundaries of the parent.
   * 
   * @param newX
   *          x coordinate to check
   * @param graphPane
   *          parent
   * @return x coordinate within range
   */
  private double checkXBoundaries(double newX, ScalablePane graphPane) {
    newX = Math.max(newX, ellipse.getRadiusX());
    newX = Math.min(newX, graphPane.getBoundsInLocal().getWidth());
    return newX;
  }

  /**
   * Check whether new y coordinate is within the boundaries of the parent.
   * 
   * @param newY
   *          y coordinate to check
   * @param graphPane
   *          parent
   * @return y coordinate within range
   */
  private double checkYBoundaries(double newY, ScalablePane graphPane) {
    newY = Math.max(newY, ellipse.getRadiusY());
    newY = Math.min(newY, graphPane.getBoundsInLocal().getHeight());
    return newY;
  }

  /**
   * Find boundaries of the node shape.
   * 
   * Works like binary search (recursive)
   * 
   * @param outside
   *          point outside the current vertex
   * @param inside
   *          point inside the current vertex
   * @return intersection point
   */
  public Point2D findIntersectionPoint(Point2D outside, Point2D inside) {
    Point2D middle = outside.midpoint(inside);

    double deltaX = outside.getX() - inside.getX();
    double deltaY = outside.getY() - inside.getY();

    if (Math.hypot(deltaX, deltaY) < 1.) {
      return middle;
    } else {
      if (ellipse.contains(middle)) {
        return findIntersectionPoint(outside, middle);
      } else {
        return findIntersectionPoint(middle, inside);
      }
    }
  }

  /**
   * Correct z order
   */
  public void toFront() {
    ellipse.toFront();
    label.toFront();
  }

  public Ellipse getEllipse() {
    return ellipse;
  }

  public String getLabel() {
    return label.getText();
  }

  /**
   * Check whether two double are equal
   * 
   * @param d1
   *          first double
   * @param d2
   *          second double
   * @return true if params are equal
   */
  private boolean equalDouble(double d1, double d2) {
    return Math.abs(d1 - d2) < 1e-6;
  }
}
