package gvs.ui.tree.model;

import java.awt.Color;
import java.awt.Stroke;
import java.util.Vector;

import gvs.interfaces.IDefaultNode;
import gvs.interfaces.INode;

/**
 * Model of a default node
 * 
 * @author aegli
 *
 */
public class DefaultNode implements IDefaultNode {

  private String nodeLabel = null;
  private Color lineColor = null;
  private Stroke lineStroke = null;
  private Color fillColor = null;
  private Vector<INode> childs = null;
  private long nodeId = 0;
  private double xPosition = 0;
  private double yPosition = 0;
  private long[] childIds = null;

  /**
   * Builds an instance of a DefaultNode
   * 
   * @param pNodeId
   * @param pNodeLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pFillColor
   * @param pChildIds
   */
  public DefaultNode(long pNodeId, String pNodeLabel, Color pLineColor,
      Stroke pLineStroke, Color pFillColor, long[] pChildIds) {
    this.nodeId = pNodeId;
    this.nodeLabel = pNodeLabel;
    this.lineColor = pLineColor;
    this.lineStroke = pLineStroke;
    this.fillColor = pFillColor;
    this.childIds = pChildIds;
    this.childs = new Vector<INode>();
  }

  /**
   * Returns children
   */
  // TODO refactor name -> getChildren
  @SuppressWarnings("rawtypes")
  public Vector getChilds() {
    return childs;
  }

  /**
   * Returns fillColor
   */
  public Color getFillColor() {
    return fillColor;
  }

  /**
   * Returns lineColor
   */
  public Color getLineColor() {
    return lineColor;
  }

  /**
   * Returns lineStroke
   */
  public Stroke getLineStroke() {
    return lineStroke;
  }

  /**
   * Returns nodeLabel
   */
  public String getNodeLabel() {
    return nodeLabel;
  }

  /**
   * Sets x position of node
   */
  public void setXPosition(double position) {
    xPosition = position;
  }

  /**
   * Returns x position of node
   */
  public double getXPosition() {
    return xPosition;
  }

  /**
   * Returns y position of node
   */
  public double getYPosition() {
    return yPosition;
  }

  /**
   * Sets y position of node
   */
  public void setYPosition(double position) {
    yPosition = position;
  }

  /**
   * Adds new child to node
   * 
   * @param pChild
   */
  public void addChild(INode pChild) {
    this.childs.add(pChild);
  }

  /**
   * Returns all childId's
   * 
   * @return
   */
  public long[] getChildIds() {
    return childIds;
  }

  /**
   * Returns node id
   */
  public long getNodeId() {
    return nodeId;
  }

  /**
   * Sets if node has parent
   */
  public void hasParent(boolean hasParent) {
    // TODO check: why is this empty?
  }

  /**
   * Returns whether node has parent
   */
  public boolean hasParent() {
    return false;
  }
}
