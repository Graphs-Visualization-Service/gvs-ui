package gvs.ui.graph.model;

import java.awt.Color;
import java.awt.Image;
import java.util.Vector;

/**
 * Model of a graph which holds its vertices and other parameters
 * 
 * @author aegli
 *
 */
public class GraphModel {
  @SuppressWarnings("rawtypes")
  private Vector vertizes = null;
  @SuppressWarnings("rawtypes")
  private Vector edges = null;
  private Image backgroundImage = null;
  private Color backgroundColor = null;
  private String graphLabel = null;
  private boolean hasBackgroundImage = false;
  private int modelId = 0;
  private int maxLabelLength = 0;

  /**
   * Builds graph model with background image
   * 
   * @param pGraphLabel
   * @param pBackgroundColor
   * @param pVertizes
   * @param pEdges
   * @param pMaxLabelLength
   */
  @SuppressWarnings("rawtypes")
  public GraphModel(String pGraphLabel, Image pBackgroundImage,
      Vector pVertizes, Vector pEdges, int pMaxLabelLength) {
    this.graphLabel = pGraphLabel;
    this.backgroundImage = pBackgroundImage;
    this.vertizes = pVertizes;
    this.edges = pEdges;
    this.hasBackgroundImage = true;
    this.maxLabelLength = pMaxLabelLength;
  }

  /**
   * Builds graph model without background image
   * 
   * @param pGraphLabel
   * @param pBackgroundColor
   * @param pVertizes
   * @param pEdges
   * @param pMaxLabelLength
   */
  @SuppressWarnings("rawtypes")
  public GraphModel(String pGraphLabel, Color pBackgroundColor,
      Vector pVertizes, Vector pEdges, int pMaxLabelLength) {
    this.graphLabel = pGraphLabel;
    this.backgroundColor = pBackgroundColor;
    this.vertizes = pVertizes;
    this.edges = pEdges;
    this.hasBackgroundImage = false;
    this.maxLabelLength = pMaxLabelLength;
  }

  /**
   * Returns background color
   * 
   * @return
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Returns background image
   * 
   * @return
   */
  public Image getBackgroundImage() {
    return backgroundImage;
  }

  /**
   * Returns existing edges
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  public Vector getEdges() {
    return edges;
  }

  /**
   * Returns graph label
   * 
   * @return
   */
  public String getGraphLabel() {
    return graphLabel;
  }

  /**
   * Checks if graph has background image
   * 
   * @return
   */
  public boolean isHasBackgroundImage() {
    return hasBackgroundImage;
  }

  /**
   * Returns model id
   * 
   * @return
   */
  public int getModelId() {
    return modelId;
  }

  /**
   * Returns vertices
   * 
   * @return
   */
  // TODO refactor name: getVertices()
  @SuppressWarnings("rawtypes")
  public Vector getVertizes() {
    return vertizes;
  }

  /**
   * Returns maximale label length
   * 
   * @return
   */
  public int getMaxLabelLength() {
    return maxLabelLength;
  }

  /**
   * Sets model id
   * 
   * @param modelId
   */
  public void setModelId(int modelId) {
    this.modelId = modelId;
  }

}
