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

  private String label = null;
  private Color lineColor = null;
  private Stroke lineStroke = null;
  private boolean isDirected = false;
  private IVertex startVertex = null;
  private IVertex endVertex = null;

  /**
   * Builds an edge instance
   * 
   * @param pLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pIsDirected
   * @param pStartVertex
   * @param pEndVertex
   */
  public Edge(String pLabel, Color pLineColor, Stroke pLineStroke,
      boolean pIsDirected, IVertex pStartVertex, IVertex pEndVertex) {

    this.label = pLabel;
    this.lineColor = pLineColor;
    this.lineStroke = pLineStroke;
    this.isDirected = pIsDirected;
    this.startVertex = pStartVertex;
    this.endVertex = pEndVertex;
  }

  /**
   * Returns end vertex of edge
   */
  public IVertex getEndVertex() {
    return endVertex;
  }

  /**
   * Returns whether vertex is directed
   */
  public boolean isDirected() {
    return isDirected;
  }

  /**
   * Returns vertex label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns vertex color
   */
  public Color getLineColor() {
    return lineColor;
  }

  /**
   * Returns vertex stroke
   */
  public Stroke getLineStroke() {
    return lineStroke;
  }

  /**
   * Returns start vertex of edge
   */
  public IVertex getStartVertex() {
    return startVertex;
  }

}
