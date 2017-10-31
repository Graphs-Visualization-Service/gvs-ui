package gvs.business.model.graph;

import java.util.List;

import gvs.business.model.Color;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Represents a graph
 * 
 * @author Michi
 *
 */
public class Graph {

  private int id;
  private List<IVertex> vertices;
  private List<IEdge> edges;
  private String snapshotDescription;

  // TODO Image backgroundImage
  private Color backgroundColor;
  private int maxLabelLength;
  private boolean hasBackgroundImage;

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
  public Graph(int graphId, List<IVertex> vertices, List<IEdge> edges) {
    this.id = graphId;
    this.vertices = vertices;
    this.edges = edges;
    this.snapshotDescription = null;
    this.maxLabelLength = 0;
    this.hasBackgroundImage = false;
  }

  public List<IVertex> getVertices() {
    return this.vertices;
  }

  public List<IEdge> getEdges() {
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

  public void setVertices(List<IVertex> vertices) {
    this.vertices = vertices;
  }

  public void setEdges(List<IEdge> edges) {
    this.edges = edges;
  }

}
