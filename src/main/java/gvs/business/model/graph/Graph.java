package gvs.business.model.graph;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.Color;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Represents a graph
 * 
 * @author mWieland
 *
 */
public class Graph {

  private int id;
  private Collection<IVertex> vertices;
  private Collection<IEdge> edges;
  private String snapshotDescription;

  // TODO Image backgroundImage
  private Color backgroundColor;
  private int maxLabelLength;
  private boolean hasBackgroundImage;

  private static final Logger logger = LoggerFactory.getLogger(Graph.class);

  /**
   * New graph representation
   * 
   * @param vertices
   *          vertices
   * @param edges
   *          edges
   * @param graphId
   *          model id
   */
  public Graph(Collection<IVertex> vertices, Collection<IEdge> edges) {
    logger.info("Building new Graph.");
    this.vertices = vertices;
    this.edges = edges;
    // TODO: maybe better change persistor behaviour
    // initialize to empty string -> otherwise error when saving session
    this.snapshotDescription = "";
    this.maxLabelLength = 0;
    this.hasBackgroundImage = false;
  }

  public boolean doLayout() {
    return getVertices().stream().noneMatch(v -> v.isRelative());
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

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public int getMaxLabelLength() {
    return maxLabelLength;
  }

  public void setMaxLabelLength(int maxLabelLength) {
    this.maxLabelLength = maxLabelLength;
  }

  public boolean isHasBackgroundImage() {
    return hasBackgroundImage;
  }

  public void setHasBackgroundImage(boolean hasBackgroundImage) {
    this.hasBackgroundImage = hasBackgroundImage;
  }

  public void setVertices(Set<IVertex> vertices) {
    this.vertices = vertices;
  }

  public void setEdges(Set<IEdge> edges) {
    this.edges = edges;
  }

}
