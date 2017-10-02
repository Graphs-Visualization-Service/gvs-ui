package gvs.ui.graph.model;

import java.awt.Color;
import java.awt.Stroke;

import gvs.interfaces.IDefaultVertex;

/**
 * Model of a default vertex
 * 
 * @author aegli
 *
 */
public class DefaultVertex implements IDefaultVertex {

  private long id = 0;
  private String label = null;
  private Color lineColor = null;
  private Stroke lineStroke = null;
  private Color fillColor = null;
  private boolean isFixed = false;
  private boolean isRelative = false;
  private double xPosition = 0;
  private double yPosition = 0;

  /**
   * Builds a default vertex without fixed positions
   * 
   * @param pId
   * @param pLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pFillColor
   */
  public DefaultVertex(long pId, String pLabel, Color pLineColor,
      Stroke pLineStroke, Color pFillColor) {
    this.id = pId;
    this.label = pLabel;
    this.lineColor = pLineColor;
    this.lineStroke = pLineStroke;
    this.fillColor = pFillColor;
    this.isRelative = false;
  }

  /**
   * Builds a default vertex with fixed positions
   * 
   * @param pId
   * @param pLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pFillColor
   */
  public DefaultVertex(long pId, String pLabel, Color pLineColor,
      Stroke pLineStroke, Color pFillColor, double pXPosition,
      double pYPosition) {
    this(pId, pLabel, pLineColor, pLineStroke, pFillColor);
    this.xPosition = pXPosition;
    this.yPosition = pYPosition;
    this.isRelative = true;
  }

  /**
   * Builds a default vertex
   * 
   * @param pId
   * @param pLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pFillColor
   */
  public DefaultVertex(long pId, String pLabel, Color pLineColor,
      Stroke pLineStroke, Color pFillColor, double pXPosition,
      double pYPosition, boolean pIsRelative) {
    this(pId, pLabel, pLineColor, pLineStroke, pFillColor);
    this.xPosition = pXPosition;
    this.yPosition = pYPosition;
    this.isRelative = pIsRelative;
  }

  /**
   * Returns x position
   */
  public double getXPosition() {
    return xPosition;
  }

  /**
   * Sets x position
   */
  public void setXPosition(double position) {
    xPosition = position;
  }

  /**
   * Returns y position
   */
  public double getYPosition() {
    return yPosition;
  }

  /**
   * Sets y position
   */
  public void setYPosition(double position) {
    yPosition = position;
  }

  /**
   * Returns whether vertex is relative
   */
  public boolean isRelative() {
    return isRelative;
  }

  /**
   * Returns vertex label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns vertex linecolor
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
   * Returns vertex fillcolor
   */
  public Color getFillColor() {
    return fillColor;
  }

  /**
   * Returns if vertex is fixed
   */
  public boolean isFixedPosition() {
    return isFixed;
  }

  /**
   * Sets vertex as fixed
   */
  public void setFixedPosition(boolean pIsFixed) {
    isFixed = pIsFixed;
  }

  /**
   * Returns vertex id
   */
  public long getId() {
    return id;
  }
}
