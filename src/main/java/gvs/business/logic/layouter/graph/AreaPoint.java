package gvs.business.logic.layouter.graph;

/**
 * A point in the layouting area
 * 
 * @author aegli
 *
 */
public class AreaPoint {

  private double x;
  private double y;

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
    this.setX(this.getX() + v.getX());
    this.setY(this.getY() + v.getY());
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
      return getX();
    } else {
      return getY();
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
      setX(d);
    } else {
      setY(d);
    }
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }
}
