package gvs.ui.graph.model;

import java.awt.Color;
import java.awt.Image;
import java.util.Vector;

import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Model of a graph which holds its vertices and other parameters.
 * 
 * @author aegli
 *
 */
public class GraphModel {
  private Vector<IVertex> vertizes = null;
  private Vector<IEdge> edges = null;
  private Image backgroundImage = null;
  private Color backgroundColor = null;
  private String graphLabel = null;
  private boolean hasBackgroundImage = false;
  private int modelId = 0;
  private int maxLabelLength = 0;

  /**
   * Builds graph model with background image.
   * 
   * @param pGraphLabel
   *          label
   * @param pBackgroundImage
   *          background image
   * @param pVertizes
   *          vertices
   * @param pEdges
   *          edges
   * @param pMaxLabelLength
   *          max label length
   */
  public GraphModel(String pGraphLabel, Image pBackgroundImage,
      Vector<IVertex> pVertizes, Vector<IEdge> pEdges, int pMaxLabelLength) {
    this.graphLabel = pGraphLabel;
    this.backgroundImage = pBackgroundImage;
    this.vertizes = pVertizes;
    this.edges = pEdges;
    this.hasBackgroundImage = true;
    this.maxLabelLength = pMaxLabelLength;
  }

  /**
   * Builds graph model without background image.
   * 
   * @param pGraphLabel
   *          graph label
   * @param pBackgroundColor
   *          background color
   * @param pVertizes
   *          vertices
   * @param pEdges
   *          edges
   * @param pMaxLabelLength
   *          max label length
   */
  public GraphModel(String pGraphLabel, Color pBackgroundColor,
      Vector<IVertex> pVertizes, Vector<IEdge> pEdges, int pMaxLabelLength) {
    this.graphLabel = pGraphLabel;
    this.backgroundColor = pBackgroundColor;
    this.vertizes = pVertizes;
    this.edges = pEdges;
    this.hasBackgroundImage = false;
    this.maxLabelLength = pMaxLabelLength;
  }

  /**
   * Returns background color.
   * 
   * @return background color
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Returns background image.
   * 
   * @return background image
   */
  public Image getBackgroundImage() {
    return backgroundImage;
  }

  /**
   * Returns existing edges.
   * 
   * @return vector of edges
   */
  public Vector<IEdge> getEdges() {
    return edges;
  }

  /**
   * Returns graph label.
   * 
   * @return graph label
   */
  public String getGraphLabel() {
    return graphLabel;
  }

  /**
   * Checks if graph has background image.
   * 
   * @return has background image
   */
  public boolean isHasBackgroundImage() {
    return hasBackgroundImage;
  }

  /**
   * Returns model id.
   * 
   * @return model id
   */
  public int getModelId() {
    return modelId;
  }

  /**
   * Returns vertices.
   * 
   * @return vector of vertices
   */
  public Vector<IVertex> getVertizes() {
    return vertizes;
  }

  /**
   * Returns maximale label length.
   * 
   * @return max label length
   */
  public int getMaxLabelLength() {
    return maxLabelLength;
  }

  /**
   * Sets model id.
   * 
   * @param modelId
   *          modelId
   */
  public void setModelId(int modelId) {
    this.modelId = modelId;
  }

}
