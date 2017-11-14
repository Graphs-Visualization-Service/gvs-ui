package gvs.ui.model.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.NodeStyle;
import gvs.interfaces.IEdge;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
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
  private final Line line;

  private static final String CSS_EDGE_LABEL = "edge-label";
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
    this.line = new Line();

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
    bindLabelCoordinates();

    setStyles();
  }

  private void ellipsePropertyListener(
      ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    bindLineCoordinates();
  }

  private void setStyles() {
    NodeStyle style = edge.getStyle();
    line.getStyleClass().add("line-" + style.getLineColor().getColor());
    line.getStyleClass().add(style.getLineStyle().getStyle() + "-"
        + style.getLineThickness().getThickness());
  }

  private void bindLineCoordinates() {

    Point2D endVertexCenter = new Point2D(endVertex.getEllipse().getCenterX(),
        endVertex.getEllipse().getCenterY());
    Point2D startVertexCenter = new Point2D(
        startVertex.getEllipse().getCenterX(),
        startVertex.getEllipse().getCenterY());

    Point2D startPoint = startVertex.findBoundaryPoint(endVertexCenter,
        startVertexCenter);
    Point2D endPoint = endVertex.findBoundaryPoint(startVertexCenter,
        endVertexCenter);

    line.setStartX(startPoint.getX());
    line.setStartY(startPoint.getY());
    line.setEndX(endPoint.getX());
    line.setEndY(endPoint.getY());
  }

  private void bindLabelCoordinates() {
    label.layoutXProperty()
        .bind(line.startXProperty().add(line.endXProperty()).divide(2));
    label.layoutYProperty()
        .bind(line.startYProperty().add(line.endYProperty()).divide(2));
  }

  public void draw(ScalableContentPane graphPane) {
    logger.info("Drawing EdgeViewModel.");
    bindLineCoordinates();
    graphPane.getContentPane().getChildren().addAll(line, label);
  }

}
