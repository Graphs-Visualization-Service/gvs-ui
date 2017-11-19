package gvs.business.model.graph;

import gvs.business.model.styles.GVSStyle;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Model of an edge
 * 
 * @author aegli
 *
 */
public class Edge implements IEdge {

  private GVSStyle style;
  private String label;
  private boolean isDirected;
  private IVertex startVertex;
  private IVertex endVertex;

  /**
   * Builds an edge instance
   * 
   * @param pLabel
   * @param style
   * @param pIsDirected
   * @param pStartVertex
   * @param pEndVertex
   */
  public Edge(String pLabel, GVSStyle style, boolean pIsDirected,
      IVertex pStartVertex, IVertex pEndVertex) {
    this.style = style;
    this.label = pLabel;
    this.isDirected = pIsDirected;
    this.startVertex = pStartVertex;
    this.endVertex = pEndVertex;
  }

  public IVertex getEndVertex() {
    return endVertex;
  }

  public boolean isDirected() {
    return isDirected;
  }

  public String getLabel() {
    return label;
  }

  public GVSStyle getStyle() {
    return style;
  }

  public IVertex getStartVertex() {
    return startVertex;
  }

}
