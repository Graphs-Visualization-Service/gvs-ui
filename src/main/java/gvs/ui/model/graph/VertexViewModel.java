package gvs.ui.model.graph;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.DefaultVertex;
import gvs.business.model.graph.NodeStyle;
import gvs.interfaces.IVertex;
import gvs.util.FontAwesome;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import jfxtras.labs.scene.layout.ScalableContentPane;

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
    this.label = new Label();
    if (vertex.getIcon() != null) {
      logger.info("Creating VertexViewModel with an icon");
      label.setGraphic(FontAwesome.createLabel(vertex.getIcon()));
    }
    label.setText(vertex.getLabel());
    label.setCursor(Cursor.HAND);

    ellipse = new Ellipse();

    updateCoordinates(vertex.getXPosition(), vertex.getYPosition());

    // bidirectional connection
    this.vertex = vertex;
    this.vertex.addObserver(this);
    ellipse.centerXProperty().addListener(this::xPropertyListener);
    ellipse.centerYProperty().addListener(this::yPropertyListener);

    setStyles();
  }

  private void setStyles() {
    NodeStyle style = vertex.getStyle();
    
    String fillColor = style.getFillColor().getColor();
    String lineColor = style.getLineColor().getColor();
    String lineStyle = style.getLineStyle().getStyle();
    String lineThickness = style.getLineThickness().getThickness();
    
    label.getStyleClass().add("vertex");
    
    ellipse.getStyleClass().add("fill-" + fillColor);
    ellipse.getStyleClass().add("line-" + lineColor);
    ellipse.getStyleClass().add(lineStyle + "-" + lineThickness);

    if (style.getDarkColors().contains(style.getFillColor())) {
      label.textFillProperty().set(Color.WHITE);
    }
  }

  /**
   * Inform the business logic {@link DefaultVertex} about changes, made in the
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
   * Inform the business logic {@link DefaultVertex} about changes, made in the
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
   * Create the binding from the business logic {@link DefaultVertex} to the
   * presentation {@link VertexViewModel} properties.
   * 
   * As soon as any {@link DefaultVertex} field is updated, the corresponding
   * property will be notified.
   * 
   * @param o
   *          updated {@link DefaultVertex} object
   * @param arg
   *          additional arguments
   */
  @Override
  public void update(Observable o, Object arg) {
    // Hand updates over to JavaFX Thread
    Platform.runLater(() -> {
      // logger level debug, because this will happen very often when layouting
      updateCoordinates(vertex.getXPosition(), vertex.getYPosition());
    });
  }

  private void updateCoordinates(double x, double y) {
    ellipse.setCenterX(x);
    ellipse.setCenterY(y);
  }

  public void draw(ScalableContentPane p) {
    logger.info("Drawing VertexViewModel.");
    p.getContentPane().getChildren().addAll(ellipse, label);

    // this hack forces the label to compute its height and width
    label.applyCss();
    double xRadius = label.prefWidth(-1) / 2;
    double yRadius = label.prefHeight(-1) / 2;

    ellipse.setRadiusX(xRadius);
    ellipse.setRadiusY(yRadius);

    label.layoutXProperty().bind(ellipse.centerXProperty().subtract(xRadius));
    label.layoutYProperty().bind(ellipse.centerYProperty().subtract(yRadius));

    dragSupport(p);
  }

  private void dragSupport(ScalableContentPane graphPane) {
    logger.info("Adding drag support on VertexViewModel.");

    label.setOnMousePressed(e -> {
      dragOriginalSceneX = e.getSceneX();
      dragOriginalSceneY = e.getSceneY();
    });

    label.setOnMouseDragged(e -> {
      // logger level debug, because this will happen very often
      logger.debug("Mouse drag on VertexViewModel detected.");
      ellipse.setCursor(Cursor.HAND);

      double offsetX = (e.getSceneX() - dragOriginalSceneX)
          / graphPane.getContentScaleTransform().getX();
      double offsetY = (e.getSceneY() - dragOriginalSceneY)
          / graphPane.getContentScaleTransform().getY();

      double newX = ellipse.getCenterX() + offsetX;
      double newY = ellipse.getCenterY() + offsetY;

      newX = checkXBoundaries(newX, graphPane);
      newY = checkYBoundaries(newY, graphPane);

      updateCoordinates(newX, newY);
      vertex.setUserPositioned(true);

      // remember last coordinates
      dragOriginalSceneX = e.getSceneX();
      dragOriginalSceneY = e.getSceneY();
    });
  }

  private double checkXBoundaries(double newX, ScalableContentPane p) {
    double minimum = ellipse.getRadiusX();
    double maximumX = p.getBoundsInLocal().getWidth();

    if (newX < minimum) {
      newX = minimum;
    }
    if (newX > maximumX) {
      newX = maximumX;
    }
    return newX;
  }

  private double checkYBoundaries(double newY, ScalableContentPane p) {
    double minimum = ellipse.getRadiusY();
    double maximumY = p.getBoundsInLocal().getHeight();

    if (newY < minimum) {
      newY = minimum;
    }
    if (newY > maximumY) {
      newY = maximumY;
    }
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

  private boolean equalDouble(double p1, double p2) {
    return Math.abs(p1 - p2) < 1e-6;
  }
}
