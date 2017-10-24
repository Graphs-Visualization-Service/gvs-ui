package gvs.business.model.graph;

import java.util.ArrayList;
import java.util.List;

import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Represents a graph
 * 
 * @author Michi
 *
 */
public class Graph {

  private final int id;
  private List<IVertex> vertices;
  private List<IEdge> edges;
  private String snapshotDescription;

  /**
   * New graph representation
   * 
   * @param graphId
   */
  public Graph(int graphId) {
    this.id = graphId;
    this.vertices = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.snapshotDescription = null;
  }

  public List<IVertex> getVertices() {
    return this.vertices;
  }

  public List<IEdge> getEdges() {
    return this.edges;
  }

}
