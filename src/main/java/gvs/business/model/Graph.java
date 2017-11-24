package gvs.business.model;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a graph
 * 
 * @author mwieland
 *
 */
public class Graph {

  private int id;
  private boolean isLayouted;
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
  public Graph(String snapshotDescription, Collection<IVertex> vertices,
      Collection<IEdge> edges) {

    logger.info("Building new Graph.");
    this.vertices = vertices;
    this.edges = edges;
    this.snapshotDescription = snapshotDescription;
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
    return String.format("%d \t Vertices: [%s], Edges: [%s]", id,
        verticesString, edgesString);
  }

  public boolean isLayoutable() {
    return !isLayouted
        && !getVertices().stream().allMatch(v -> v.isUserPositioned());
  }

  public void setLayouted(boolean isLayouted) {
    this.isLayouted = isLayouted;
  }

}
