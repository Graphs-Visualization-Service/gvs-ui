package gvs.business.model.graph;

import java.awt.Color;
import java.awt.Stroke;
import java.util.Observable;

import gvs.interfaces.IIconVertex;
import gvs.util.FontAwesome.Glyph;

/**
 * Represents a vertex with an icon as foreground
 * 
 * @author aegli
 *
 */
public class IconVertex extends Observable implements IIconVertex {

  private long id;
  private String label;
  private Color lineColor;
  private Stroke lineStroke;
  private Glyph icon;
  private boolean isRelative;
  private boolean isFixed;
  private double xPosition;
  private double yPosition;

  /**
   * Builds relative icon vertex
   * 
   * @param pId
   * @param pLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pIcon
   */
  public IconVertex(long pId, String pLabel, Color pLineColor,
      Stroke pLineStroke, Glyph pIcon) {
    this.id = pId;
    this.label = pLabel;
    this.lineColor = pLineColor;
    this.lineStroke = pLineStroke;
    this.icon = pIcon;
    this.isRelative = false;
  }

  /**
   * Builds default icon vertex
   * 
   * @param pId
   * @param pLabel
   * @param pLineColor
   * @param pLineStroke
   * @param pIcon
   * @param pXPosition
   * @param pYPosition
   */
  public IconVertex(long pId, String pLabel, Color pLineColor,
      Stroke pLineStroke, Glyph pIcon, double pXPosition, double pYPosition) {
    this(pId, pLabel, pLineColor, pLineStroke, pIcon);
    this.xPosition = pXPosition;
    this.yPosition = pYPosition;
    this.isRelative = true;
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
   * Returns if vertex is relative
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
   * Returns line color
   */
  public Color getLineColor() {
    return lineColor;
  }

  /**
   * Returns line stroke
   */
  public Stroke getLineStroke() {
    return lineStroke;
  }

  /**
   * Returns background image
   */
  public Glyph getIcon() {
    return icon;
  }

  /**
   * Returns id
   */
  public long getId() {
    return id;
  }

  /**
   * Returns whether position is fixed
   */
  public boolean isFixedPosition() {
    return isFixed;
  }

  /**
   * Sets position as fixed
   */
  public void setFixedPosition(boolean pIsFixed) {
    isFixed = pIsFixed;
  }

  @Override
  public Color getFillColor() {
    return null;
  }
  
  
}
