package gvs.ui.model.graph;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.Edge;
import gvs.interfaces.IEdge;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Contains JavaFX Properties which are used for bidirectional bindings.
 * 
 * @author Michi
 */
public class EdgeViewModel{

  private IEdge edge;
  private VertexViewModel startVertex;
  private VertexViewModel endVertex;
  private NodeStyleViewModel style;
  
  private final StringProperty labelProperty = new SimpleStringProperty();

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
    this.labelProperty.set(edge.getLabel());
  }

  public VertexViewModel getStartVertex() {
    return this.startVertex;
  }

  public VertexViewModel getEndVertex() {
    return this.endVertex;
  }

  public NodeStyleViewModel getStyle() {
    return style;
  }

  public void setStyle(NodeStyleViewModel style) {
    this.style = style;
  }

  public StringProperty labelProperty() {
    return labelProperty;
  }
}
