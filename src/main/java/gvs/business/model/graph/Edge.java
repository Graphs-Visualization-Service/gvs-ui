package gvs.business.model.graph;

import java.awt.Color;
import java.awt.Stroke;
import java.util.Observable;

import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Model of an edge
 * 
 * @author aegli
 *
 */
public class Edge extends Observable implements IEdge {

  private NodeStyle style;
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
  public Edge(String pLabel, NodeStyle style, boolean pIsDirected,
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

  public NodeStyle getStyle() {
    return style;
  }

  public IVertex getStartVertex() {
    return startVertex;
  }

}
