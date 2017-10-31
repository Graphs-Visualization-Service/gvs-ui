package gvs.business.model.tree;

import java.awt.Color;
import java.util.Vector;

import gvs.interfaces.INode;

/**
 * Model of a tree which holds its nodes and other parameters.
 * 
 * @author aegli
 *
 */
public class TreeModel {
  private String treeLabel;
  private Color backgroundColor;
  private Vector<INode> nodes;
  private INode rootNode;
  private int modelId;
  private int maxLabelLength;

  /**
   * Builds an instance of a TreeModel.
   * 
   * @param pTreeLabel
   *          label
   * @param maxLabelLength
   *          maximal label length
   * @param pBackgroundColor
   *          background color
   * @param pRootNode
   *          root node
   * @param pNodes
   *          vector of nodes
   */
  public TreeModel(String pTreeLabel, int maxLabelLength,
      Color pBackgroundColor, INode pRootNode, Vector<INode> pNodes) {
    this.treeLabel = pTreeLabel;
    this.backgroundColor = pBackgroundColor;
    this.rootNode = pRootNode;
    this.nodes = pNodes;
    this.maxLabelLength = maxLabelLength;
  }

  /**
   * Builds an instance of a TreeModel.
   * 
   * @param pTreeLabel
   *          label
   * @param pModelId
   *          model id
   * @param maxLabelLength
   *          label length
   * @param pBackgroundColor
   *          background color
   * @param pRootNode
   *          root node
   * @param pNodes
   *          vector of nodes
   */
  public TreeModel(String pTreeLabel, int pModelId, int maxLabelLength,
      Color pBackgroundColor, INode pRootNode, Vector<INode> pNodes) {
    this.treeLabel = pTreeLabel;
    this.backgroundColor = pBackgroundColor;
    this.rootNode = pRootNode;
    this.nodes = pNodes;
    this.maxLabelLength = maxLabelLength;
    this.modelId = pModelId;
  }

  /**
   * Returns backgroundColor.
   * 
   * @return color
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Returns modelId.
   * 
   * @return
   */
  public int getModelId() {
    return modelId;
  }

  /**
   * Returns nodes of model.
   * 
   * @return
   */
  public Vector<INode> getNodes() {
    return nodes;
  }

  /**
   * Returns root node of model
   * 
   * @return
   */
  public INode getRootNode() {
    return rootNode;
  }

  /**
   * Returns treeLabel
   * 
   * @return
   */
  public String getTreeLabel() {
    return treeLabel;
  }

  /**
   * Returns the maximal label length which is allowed to be displayed
   * 
   * @return
   */
  public int getMaxLabelLength() {
    return maxLabelLength;
  }

  /**
   * Sets modelId, used by tree controller
   * 
   * @param modelId
   */
  public void setModelId(int modelId) {
    this.modelId = modelId;
  }
}