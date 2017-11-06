package gvs.ui.model.graph;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.Edge;
import gvs.interfaces.IEdge;

/**
 * Contains JavaFX Properties which are used for bidirectional bindings.
 * 
 * @author Michi
 */
public class EdgeViewModel implements Observer {

  private IEdge edge;
  private VertexViewModel startVertex;
  private VertexViewModel endVertex;
  private NodeStyleViewModel style;

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
    
    // bidirectional connection
    edge.addObserver(this);
  }

  @Override
  public void update(Observable o, Object arg) {
    Edge updatedEdge = (Edge) o;
    this.edge = updatedEdge;
    this.startVertex = new VertexViewModel(updatedEdge.getStartVertex());
    this.endVertex = new VertexViewModel(updatedEdge.getEndVertex());
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
}
