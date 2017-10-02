package gvs.ui.tree.model;

import gvs.interfaces.IBinaryNode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

/**
 * Model of a binary node
 * 
 * @author aegli
 *
 */
public class BinaryNode implements IBinaryNode {

  private long nodeId = 0;
  private String nodeLabel = null;
  private Color lineColor = null;
  private BasicStroke lineStroke = null;
  private Color fillColor = null;
  private IBinaryNode leftChild = null;
  private IBinaryNode rightChild = null;
  private long leftChildId = 0;
  private long rightChildId = 0;
  private double xPosition = 0;
  private double yPosition = 0;
  private int myTreePosition = 0;
  private int myTreeLevel = 0;
  private boolean hasParent = false;

  @Override
  public String toString() {
    return nodeLabel + ": " + xPosition + "/" + yPosition;
  }

  /**
   * Creates an instance of a BinaryNode which includes child references
   * 
   * @param pNodeId
   * @param pNodeLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pFillColor
   * @param pLeftChild
   * @param pRightChild
   */
  public BinaryNode(long pNodeId, String pNodeLabel, Color pLineColor,
      BasicStroke pLineStroke, Color pFillColor, IBinaryNode pLeftChild,
      IBinaryNode pRightChild) {
    this.nodeId = pNodeId;
    this.nodeLabel = pNodeLabel;
    this.lineColor = pLineColor;
    this.lineStroke = pLineStroke;
    this.fillColor = pFillColor;
    this.leftChild = pLeftChild;
    this.rightChild = pRightChild;
  }

  /**
   * Creates an instance of a BinaryNode which includes child id's
   * 
   * @param pNodeId
   * @param pNodeLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pFillColor
   * @param pLeftChildId
   * @param pRightChildId
   */
  public BinaryNode(long pNodeId, String pNodeLabel, Color pLineColor,
      BasicStroke pLineStroke, Color pFillColor, long pLeftChildId,
      long pRightChildId) {
    this.nodeId = pNodeId;
    this.nodeLabel = pNodeLabel;
    this.lineColor = pLineColor;
    this.lineStroke = pLineStroke;
    this.fillColor = pFillColor;
    this.leftChildId = pLeftChildId;
    this.rightChildId = pRightChildId;
  }

  /**
   * Creates an instance of a BinaryNode which was loaded from persistor
   * 
   * @param pNodeId
   * @param pNodeLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pFillColor
   * @param pLeftChildId
   * @param pRightChildId
   * @param pXPosition
   * @param pYPosition
   */
  public BinaryNode(long pNodeId, String pNodeLabel, Color pLineColor,
      BasicStroke pLineStroke, Color pFillColor, long pLeftChildId,
      long pRightChildId, double pXPosition, double pYPosition) {
    this.nodeId = pNodeId;
    this.nodeLabel = pNodeLabel;
    this.lineColor = pLineColor;
    this.lineStroke = pLineStroke;
    this.fillColor = pFillColor;
    this.leftChildId = pLeftChildId;
    this.rightChildId = pRightChildId;
    this.xPosition = pXPosition;
    this.yPosition = pYPosition;
  }

  /**
   * Returns fillcolor
   */
  public Color getFillColor() {
    return fillColor;
  }

  /**
   * Returns leftChild
   */
  public IBinaryNode getLeftChild() {
    return leftChild;
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
   * Returns rightChild
   */
  public IBinaryNode getRightChild() {
    return rightChild;
  }

  /**
   * Sets rightChild
   * 
   * @param pRigthChild
   */
  public void setRigthChild(IBinaryNode pRigthChild) {
    this.rightChild = pRigthChild;
  }

  /**
   * Sets leftChild
   * 
   * @param pLeftChild
   */
  public void setLeftChild(IBinaryNode pLeftChild) {
    this.leftChild = pLeftChild;
  }

  /**
   * Returns x position
   */
  public double getXPosition() {
    return xPosition;
  }

  /**
   * Returns y position
   */
  public double getYPosition() {
    return yPosition;
  }

  /**
   * Sets x position
   */
  public void setXPosition(double position) {
    xPosition = position;
  }

  /**
   * Sets y position
   */
  public void setYPosition(double position) {
    yPosition = position;
  }

  /**
   * Returns leftChildId
   * 
   * @return
   */
  public long getLeftChildId() {
    return leftChildId;
  }

  /**
   * Returns rightChildId
   * 
   * @return
   */
  public long getRightChildId() {
    return rightChildId;
  }

  /**
   * Returns nodeId
   */
  public long getNodeId() {
    return nodeId;
  }

  /**
   * Returns actual treePosition
   */
  public int getMyTreePosition() {
    return myTreePosition;
  }

  /**
   * Sets nodePosition in a tree
   */
  public void setMyTreePosition(int myTreePosition) {
    this.myTreePosition = myTreePosition;
  }

  /**
   * Returns depth level of node
   */
  public int getMyTreeLevel() {
    return myTreeLevel;
  }

  /**
   * Sets depth level of node
   */
  public void setMyTreeLevel(int myTreeLevel) {
    this.myTreeLevel = myTreeLevel;
  }

  /**
   * Is set if node has parent
   */
  public void hasParent(boolean hasParent) {
    this.hasParent = hasParent;
  }

  /**
   * Returns whether node has parent
   */

  public boolean hasParent() {
    return hasParent;
  }

}
