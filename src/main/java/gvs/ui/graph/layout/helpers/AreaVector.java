package gvs.ui.graph.layout.helpers;

/**
 * Represents a speed or acceleration value in a given direction
 * 
 * @author aegli
 *
 */

public class AreaVector {

  public double x = 0;
  public double y = 0;
  public double length = 0;

  /**
   * Represents a default vector
   *
   */
  public AreaVector() {
    this.x = 0;
    this.y = 0;
  }

  /**
   * Represents a vector based on x and y components
   * 
   * @param x
   * @param y
   */
  public AreaVector(double x, double y) {
    this.x = x;
    this.y = y;
    update();
  }

  /**
   * Represents a distance vector based on a start and end point
   * 
   * @param point
   * @param toPoint
   */
  public AreaVector(AreaPoint point, AreaPoint toPoint) {
    double px = point.getX();
    double py = point.getY();
    double tx = toPoint.getX();
    double ty = toPoint.getY();

    x = px - tx;
    y = py - ty;

    update();
  }

  /**
   * Represents a scale. Multiplicated with a vector, it has an influence on how
   * fast particles are moving
   * 
   * @param newLength
   */
  public void scaleTo(double newLength) {
    x *= newLength
        / Math.abs(length)/* Nur Vorzeichenwechsel immer ins Positive */;
    y *= newLength / Math.abs(length);

    update();
  }

  /**
   * Adds another vector to the given vector and multiply it with a scale. This
   * new distance vector represents a new acceleration/speed of a particle
   * 
   * @param v
   * @param scale
   */
  public void add(AreaVector v, double scale) {
    this.x -= v.getX() * scale;
    this.y -= v.getY() * scale;

    update();
  }

  /**
   * Calculates the length of the hypotenuse based on x and y length
   *
   */
  private void update() {
    double pyth = x * x + y * y;
    this.length = Math.sqrt(pyth);
  }

  /**
   * Adds another vector to the given vector. Represents new speed/acceleration
   * of a particle in the given direction
   * 
   * @param v
   */
  public void add(AreaVector v) {
    this.x += v.getX();
    this.y += v.getY();

    update();
  }

  /**
   * Returns x component of vector
   * 
   * @return
   */
  public double getX() {
    return x;
  }

  /**
   * Returns y component of vector
   * 
   * @return
   */
  public double getY() {
    return y;
  }

  /**
   * Resets vector components
   *
   */
  public void resetAcc() {
    x = 0.0;
    y = 0.0;

    update();
  }

  /**
   * Inverts vector. Used by tractions and forces
   *
   */
  // TODO: refactor name -> eg invert
  public void changeOfSign() {
    this.x = -(this.x);
    this.y = -(this.y);
  }

  /**
   * Reduces or stretches vector
   * 
   * @param v
   */
  public void reduceMultiplicator(double v) {
    this.x *= v;
    this.y *= v;
    update();
  }

  /**
   * Returns hypotenuse length of a vector
   * 
   * @return
   */
  public double getDistance() {
    return length;
  }

  /**
   * Returns the sum of speed in x and y direction. If this value is small, the
   * particle will be set to fixed
   * 
   * @return
   */
  // TODO: refactor name -> getSpeedSum
  public double getSpeedSummary() {
    return x + y;
  }

  /**
   * Returns length of x or y depending on input
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
   * Sets a new length x or y depending on input
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

    update();
  }
}
