package gvs.ui.model.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.NodeStyle;
import gvs.interfaces.IEdge;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
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

  private static final Logger logger = LoggerFactory
      .getLogger(EdgeViewModel.class);

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

    bindNodeDraggedListeners();
    setStyles();
  }

  /**
   * Bind node the center x and y position of the start and endnode to property
   * changed listener.
   */
  private void bindNodeDraggedListeners() {
    startVertex.getEllipse().centerXProperty()
        .addListener(this::nodeDraggedListener);
    startVertex.getEllipse().centerYProperty()
        .addListener(this::nodeDraggedListener);
    endVertex.getEllipse().centerXProperty()
        .addListener(this::nodeDraggedListener);
    endVertex.getEllipse().centerYProperty()
        .addListener(this::nodeDraggedListener);
  }

  /**
   * Recompute edge position if node is dragged.
   * 
   * @param observable
   *          observable
   * @param oldValue
   *          old vertex center position
   * @param newValue
   *          new vertex center position
   */
  private void nodeDraggedListener(ObservableValue<? extends Number> observable,
      Number oldValue, Number newValue) {
    computeCoordinates();
  }

  /**
   * Set edge styles for line and optionally arrowhead
   */
  private void setStyles() {
    NodeStyle style = edge.getStyle();

    String lineColor = style.getLineColor().getColor();
    String lineStyle = style.getLineStyle().getStyle();
    String lineThickness = style.getLineThickness().getThickness();

    label.getStyleClass().add("edge-label");

    edgePath.getStyleClass().add("line-" + lineColor);
    edgePath.getStyleClass().add(lineStyle + "-" + lineThickness);

    if (edge.isDirected()) {
      arrowPath.getStyleClass().add("line-" + lineColor);
      arrowPath.getStyleClass().add("thickness" + lineThickness);
    }
  }

  /**
   * Draw the edge on given graph pane
   * 
   * @param graphPane
   *          scaleable graph pane
   */
  public void draw(ScalableContentPane graphPane) {
    logger.info("Drawing EdgeViewModel");
    computeCoordinates();
    graphPane.getContentPane().getChildren().addAll(edgePath, arrowPath, label);
  }

  /**
   * Compute x and y position of the edge.
   * 
   * Finds the intersection points of the line with the start and end vertex.
   * Once found, a arrow head is added (if needed)
   */
  private void computeCoordinates() {
    // clear previously drawn lines/arrows
    edgePath.getElements().clear();
    arrowPath.getElements().clear();

    Point2D startVertexCenter = new Point2D(
        startVertex.getEllipse().getCenterX(),
        startVertex.getEllipse().getCenterY());
    Point2D endVertexCenter = new Point2D(endVertex.getEllipse().getCenterX(),
        endVertex.getEllipse().getCenterY());

    // find intersection points of line and vertices
    Point2D startPoint = startVertex.findIntersectionPoint(endVertexCenter,
        startVertexCenter);
    Point2D endPoint = endVertex.findIntersectionPoint(startVertexCenter,
        endVertexCenter);

    drawEdgeLabel(startPoint, endPoint);

    double length = startPoint.distance(endPoint);
    drawHorizontalEdge(startPoint, endPoint, length);

    if (edge.isDirected()) {
      double endX = startPoint.getX() + length;
      double startY = startPoint.getY();
      drawArrowHead(endX, startY);
    }

    rotateEdge(startPoint, endPoint);
  }

  /**
   * Position the label between the two intersection points.
   * 
   * @param startPoint
   *          start intersection point
   * @param endPoint
   *          end intersection point
   */
  private void drawEdgeLabel(Point2D startPoint, Point2D endPoint) {
    Point2D middle = startPoint.midpoint(endPoint);
    label.setLayoutX(middle.getX());
    label.setLayoutY(middle.getY());
  }

  /**
   * Draw a line from the start point horizontally with the correct length. The
   * line will be rotated later
   *
   * @param startPoint
   *          start intersection point
   * @param endPoint
   *          end intersection point
   * @param length
   *          length of the edge
   */
  private void drawHorizontalEdge(Point2D startPoint, Point2D endPoint,
      double length) {
    edgePath.getElements().addAll(
        new MoveTo(startPoint.getX(), startPoint.getY()),
        new LineTo(startPoint.getX() + length, startPoint.getY()));
  }

  /**
   * Draw an arrow head at the end of the edge
   * 
   * @param startX
   *          start point on the x axis (at the edge end)
   * @param startY
   *          start point on the y axis (at the edge end)
   */
  private void drawArrowHead(double startX, double startY) {
    arrowPath.getElements().addAll(new MoveTo(startX, startY),
        new LineTo(startX - 5, startY + 2), new MoveTo(startX, startY),
        new LineTo(startX - 5, startY - 2));
  }

  /**
   * Rotate the horizontal edge to the right position
   * 
   * @param startPoint
   *          start intersection point
   * @param endPoint
   *          end intersection point
   */
  private void rotateEdge(Point2D startPoint, Point2D endPoint) {
    // compute rotation angle
    double angle = Math.atan2(endPoint.getY() - startPoint.getY(),
        endPoint.getX() - startPoint.getX());
    angle = Math.toDegrees(angle);

    edgePath.getTransforms().clear();
    arrowPath.getTransforms().clear();

    // rotates the drawn line and arrow into the correct position
    edgePath.getTransforms()
        .add(new Rotate(angle, startPoint.getX(), startPoint.getY()));
    arrowPath.getTransforms()
        .add(new Rotate(angle, startPoint.getX(), startPoint.getY()));
  }

}
