package gvs.ui.model.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.interfaces.IEdge;
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
  private NodeStyleViewModel style;

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
    this.style = new NodeStyleViewModel(edge.getStyle());
    this.label = new Label();
    this.line = new Line();

    this.label.setText(edge.getLabel());
    bindLineCoordinates();
    bindLabelCoordinates();
  }

  private void bindLineCoordinates() {
    line.startXProperty().bind(startVertex.centerXProperty());
    line.startYProperty().bind(startVertex.centerYProperty());
    line.endXProperty().bind(endVertex.centerXProperty());
    line.endYProperty().bind(endVertex.centerYProperty());
  }

  private void bindLabelCoordinates() {
    label.layoutXProperty()
        .bind(line.startXProperty().add(line.endXProperty()).divide(2));
    label.layoutYProperty()
        .bind(line.startYProperty().add(line.endYProperty()).divide(2));
  }

  public void draw(ScalableContentPane graphPane) {
    logger.info("Drawing VertexViewModel.");
    graphPane.getContentPane().getChildren().addAll(line, label);
  }
}
