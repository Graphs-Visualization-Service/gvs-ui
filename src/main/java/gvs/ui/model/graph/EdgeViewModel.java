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
import javafx.scene.transform.Affine;
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
  private final Path arrowHead;

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
    this.arrowHead = new Path();

    this.label.setText(edge.getLabel());

    startVertex.getEllipse().centerXProperty()
        .addListener(this::ellipsePropertyListener);
    startVertex.getEllipse().centerYProperty()
        .addListener(this::ellipsePropertyListener);
    endVertex.getEllipse().centerXProperty()
        .addListener(this::ellipsePropertyListener);
    endVertex.getEllipse().centerYProperty()
        .addListener(this::ellipsePropertyListener);

    bindLineCoordinates();

    setStyles();
  }

  private void ellipsePropertyListener(
      ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    bindLineCoordinates();
  }

  private void setStyles() {
    NodeStyle style = edge.getStyle();
    // line.getStyleClass().add("line-" + style.getLineColor().getColor());
    // line.getStyleClass().add(style.getLineStyle().getStyle() + "-"
    // + style.getLineThickness().getThickness());
  }

  private void bindLineCoordinates() {

    arrowHead.getElements().clear();
    
    Point2D endVertexCenter = new Point2D(endVertex.getEllipse().getCenterX(),
        endVertex.getEllipse().getCenterY());
    Point2D startVertexCenter = new Point2D(
        startVertex.getEllipse().getCenterX(),
        startVertex.getEllipse().getCenterY());

    Point2D startPoint = startVertex.findBoundaryPoint(endVertexCenter,
        startVertexCenter);
    Point2D endPoint = endVertex.findBoundaryPoint(startVertexCenter,
        endVertexCenter);

    Point2D middle = startPoint.midpoint(endPoint);
    label.setLayoutX(middle.getX());
    label.setLayoutY(middle.getY());

    double length = Math.hypot(startPoint.getX(), endPoint.getY());

    // Line
    arrowHead.getElements()
        .add(new MoveTo(startPoint.getX(), startPoint.getY()));
    arrowHead.getElements().add(new LineTo(endPoint.getX(), endPoint.getY()));

    // ArrowHead
    if (edge.isDirected()) {
      double angle = Math.atan2((endPoint.getY() - endPoint.getX()),
          (endPoint.getX() - startPoint.getX())) - Math.PI / 2.0;
      double sin = Math.sin(angle);
      double cos = Math.cos(angle);
      // point1
      double x1 = (-1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE
          + endPoint.getX();
      double y1 = (-1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE
          + endPoint.getY();
      // point2
      double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * ARROW_HEAD_SIZE
          + endPoint.getX();
      double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * ARROW_HEAD_SIZE
          + endPoint.getY();
      arrowHead.getElements().add(new LineTo(x1, y1));
      arrowHead.getElements().add(new LineTo(x2, y2));
      arrowHead.getElements().add(new LineTo(endPoint.getX(), endPoint.getY()));
    }
  }

  public void draw(ScalableContentPane graphPane) {
    logger.info("Drawing EdgeViewModel.");
    bindLineCoordinates();
    graphPane.getContentPane().getChildren().addAll(arrowHead, label);
  }

}
