package gvs.ui.model.graph;

import java.util.Observable;
import java.util.Observer;

import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;
import gvs.ui.graph.model.Edge;

/**
 * 
 * @author Michi
 */
public class EdgeViewModel implements Observer {

  private IVertex startVertex;
  private IVertex endVertex;

  /**
   * Create a new EdgeViewModel with the corresponding Vertices
   * 
   * @param startVertex
   *          start vertex
   * @param endVertex
   *          end vertex
   */
  public EdgeViewModel(IEdge edge) {
    this.startVertex = edge.getStartVertex();
    this.endVertex = edge.getEndVertex();
  }

  @Override
  public void update(Observable o, Object arg) {
    Edge updatedEdge = (Edge) o;
    this.startVertex = updatedEdge.getEndVertex();
    this.endVertex = updatedEdge.getStartVertex();
  }

}
