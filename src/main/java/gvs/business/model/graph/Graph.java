package gvs.business.model.graph;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Represents a graph
 * 
 * @author mwieland
 *
 */
public class Graph {

  private int id;
  private String snapshotDescription;
  private Collection<IVertex> vertices;
  private Collection<IEdge> edges;

  private static final Logger logger = LoggerFactory.getLogger(Graph.class);

  /**
   * New graph representation
   * 
   * @param vertices
   *          vertices
   * @param edges
   *          edges
   */
  public Graph(Collection<IVertex> vertices, Collection<IEdge> edges) {

    logger.info("Building new Graph.");
    this.vertices = vertices;
    this.edges = edges;
    // TODO: maybe better change persistor behaviour
    // initialize to empty string -> otherwise error when saving session
    this.snapshotDescription = new String();
  }

  public boolean isLayoutable() {
    return !getVertices().stream().allMatch(v -> v.isUserPositioned());
  }

  public Collection<IVertex> getVertices() {
    return this.vertices;
  }

  public Collection<IEdge> getEdges() {
    return this.edges;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getSnapshotDescription() {
    return snapshotDescription;
  }

  public void setSnapshotDescription(String snapshotDescription) {
    this.snapshotDescription = snapshotDescription;
  }

  public void setVertices(Set<IVertex> vertices) {
    this.vertices = vertices;
  }

  public void setEdges(Set<IEdge> edges) {
    this.edges = edges;
  }
  
  public String toString() {
    String verticesString = "";
    for (IVertex v : vertices) {
      verticesString += v.toString();
    }
    String edgesString = "";
    for (IEdge e : edges) {
      edgesString += e.toString();
    }
    return String.format("%d \t Vertices: [%s], Edges: [%s]", id, verticesString, edgesString);
  }

}
