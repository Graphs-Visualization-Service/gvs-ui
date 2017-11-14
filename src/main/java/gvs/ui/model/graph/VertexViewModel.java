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
  private static final String CSS_NODE = "node";

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
    ellipse.getStyleClass().add(CSS_NODE);
    ellipse.getStyleClass().add("line-" + style.getLineColor().getColor());
    ellipse.getStyleClass().add(style.getLineStyle().getStyle() + "-"
        + style.getLineThickness().getThickness());
    ellipse.getStyleClass().add("fill-" + style.getFillColor().getColor());

    // TODO: find nicer method, maybe use getContrastColor()
    if (style.getDarkColors().contains(style.getFillColor())) {
      label.textFillProperty().set(Color.WHITE);
    }
  }

  // private static Color getContrastColor(Color color) {
  // double y = (299 * color.getRed() + 587 * color.getGreen() + 114 *
  // color.getBlue()) / 1000;
  // return y >= 128 ? Color.BLACK : Color.WHITE;
  // }

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
    if (Math.abs((double) oldValue - (double) newValue) <= 0.000001) {
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
    if (Math.abs((double) oldValue - (double) newValue) <= 0.000001) {
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

    // TODO describe hack
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

  public Ellipse getEllipse() {
    return ellipse;
  }

  public void toFront() {
    ellipse.toFront();
    label.toFront();
  }

  /**
   * Find boundaries of the node shape.
   * 
   * Works like binary search
   * 
   * @param outside
   * @param inside
   * @return
   */
  public Point2D findBoundaryPoint(Point2D outside, Point2D inside) {
    Point2D middle = middle(outside, inside);
    if (pointsLenSqr(outside, inside) < 1.) {
      return middle;
    } else {
      if (ellipse.contains(middle)) {
        return findBoundaryPoint(outside, middle);
      } else {
        return findBoundaryPoint(middle, inside);
      }
    }
  }

  private Point2D middle(Point2D outside, Point2D inside) {
    return new Point2D((outside.getX() + inside.getX()) / 2,
        (outside.getY() + inside.getY()) / 2);
  }

  private double pointsLenSqr(Point2D startPoint, Point2D endPoint) {
    return Math.pow(startPoint.getX() - endPoint.getX(), 2)
        + Math.pow(startPoint.getY() - endPoint.getY(), 2);
  }
}
