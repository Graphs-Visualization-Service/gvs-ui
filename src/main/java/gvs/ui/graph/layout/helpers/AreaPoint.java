package gvs.ui.graph.layout.helpers;

import java.awt.Point;

/**
 * A point in the layouting area
 * 
 * @author aegli
 *
 */
public class AreaPoint {
  public double x = 0;
  public double y = 0;

  /**
   * Represents a default particle point in area
   *
   */
  public AreaPoint() {
    this(0, 0);
  }

  /**
   * Sets a particle position by point
   * 
   * @param point
   */
  public AreaPoint(Point point) {
    this.x = point.x;
    this.y = point.y;
  }

  /**
   * Sets a particle position by coordinates
   * 
   * @param x
   * @param y
   */
  public AreaPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Adds an acceleretation vecor to a point
   * 
   * @param v
   */
  public void addVector(AreaVector v) {
    this.x += v.getX();
    this.y += v.getY();
  }

  /**
   * Returns x position of a point
   * 
   * @return
   */
  public double getX() {
    return this.x;
  }

  /**
   * Returns y position of a point
   * 
   * @return
   */
  public double getY() {
    return this.y;
  }

  /**
   * Returns x or y position of point according to request
   * 
   * @param i
   * @return
   */
  public double getField(int i) {
    if (i == 0) {
      return x;
    } else {
      return y;
    }
  }

  /**
   * Sets x or y postion according to param i.
   * 
   * @param i
   * @param d
   */
  public void setField(int i, double d) {
    if (i == 0) {
      x = d;
    } else {
      y = d;
    }
  }
}
