package gvs.ui.model.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.NodeStyle;
import gvs.interfaces.IEdge;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 * Contains JavaFX Properties which are used for bidirectional bindings.
 * 
 * @author Michi
 */
public class EdgeViewModel {

  private IEdge edge;
  private VertexViewModel startVertex;
  private VertexViewModel endVertex;

  private final Label label;
  private final Path edgePath;
  private final Path arrowPath;

  private static final String CSS_EDGE_LABEL = "edge-label";
  private static final Logger logger = LoggerFactory
      .getLogger(EdgeViewModel.class);
  private static final double ARROW_HEAD_SIZE = 5;

  /**
   * Create a new EdgeViewModel with the corresponding vertices.
   * 
   * @param edge
   *          business layer edge
   * @param startVertex
   *          start vertex
   * @param endVertex
   *          end vertex
   */
  public EdgeViewModel(IEdge edge, VertexViewModel startVertex,
      VertexViewModel endVertex) {
    this.edge = edge;
    this.startVertex = startVertex;
    this.endVertex = endVertex;
    this.label = new Label();
    this.edgePath = new Path();
    this.arrowPath = new Path();

    this.label.setText(edge.getLabel());

    startVertex.getEllipse().centerXProperty()
        .addListener(this::ellipsePropertyListener);
    startVertex.getEllipse().centerYProperty()
        .addListener(this::ellipsePropertyListener);
    endVertex.getEllipse().centerXProperty()
        .addListener(this::ellipsePropertyListener);
    endVertex.getEllipse().centerYProperty()
        .addListener(this::ellipsePropertyListener);

    setStyles();
  }

  private void ellipsePropertyListener(
      ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    bindLineCoordinates();
  }

  private void setStyles() {
    NodeStyle style = edge.getStyle();
    edgePath.getStyleClass().add("line-" + style.getLineColor().getColor());
    arrowPath.getStyleClass().add("line-" + style.getLineColor().getColor());
    edgePath.getStyleClass().add(style.getLineStyle().getStyle() + "-"
        + style.getLineThickness().getThickness());
  }

  private void bindLineCoordinates() {
    // clear previously drawn lines/arrows
    edgePath.getElements().clear();
    arrowPath.getElements().clear();

    Point2D startVertexCenter = new Point2D(
        startVertex.getEllipse().getCenterX(),
        startVertex.getEllipse().getCenterY());
    Point2D endVertexCenter = new Point2D(endVertex.getEllipse().getCenterX(),
        endVertex.getEllipse().getCenterY());

    // find intersection points of line and vertices
    Point2D startPoint = startVertex.findBoundaryPoint(endVertexCenter,
        startVertexCenter);
    Point2D endPoint = endVertex.findBoundaryPoint(startVertexCenter,
        endVertexCenter);

    // position the label in between the intersection points
    Point2D middle = startPoint.midpoint(endPoint);
    label.setLayoutX(middle.getX());
    label.setLayoutY(middle.getY());

    // Draw a line from the startpoint horizontally with the correct
    // length
    double length = startPoint.distance(endPoint);
    edgePath.getElements().addAll(
        new MoveTo(startPoint.getX(), startPoint.getY()),
        new LineTo(startPoint.getX() + length, startPoint.getY()));
    if (edge.isDirected()) {
      // Draw an arrowhead
      arrowPath.getElements().addAll(
          new MoveTo(startPoint.getX() + length, startPoint.getY()),
          new LineTo(startPoint.getX() + length - 5, startPoint.getY() + 2),
          new MoveTo(startPoint.getX() + length, startPoint.getY()),
          new LineTo(startPoint.getX() + length - 5, startPoint.getY() - 2));
    }

    // tempEndPoint is the endpoint of the horizontally drawn line. It is used
    // to calculate the angle of rotation needed to transform the initial
    // horizontal line into the correct position
    Point2D tempEndPoint = new Point2D(startPoint.getX() + length,
        startPoint.getY());

    double angle = Math.atan2(endPoint.getY() - startPoint.getY(),
        endPoint.getX() - startPoint.getX());
    angle = Math.toDegrees(angle);

    // rotates the drawn line and arrow into the correct positio
    edgePath.getTransforms().clear();
    edgePath.getTransforms()
        .add(new Rotate(angle, startPoint.getX(), startPoint.getY()));
    arrowPath.getTransforms().clear();
    arrowPath.getTransforms()
        .add(new Rotate(angle, startPoint.getX(), startPoint.getY()));
  }

  public String toString() {
    return startVertex.getLabel() + " -> " + endVertex.getLabel();
  }

  public void draw(ScalableContentPane graphPane) {
    logger.info("Drawing EdgeViewModel.");
    bindLineCoordinates();
    graphPane.getContentPane().getChildren().addAll(edgePath, arrowPath, label);
  }

}
