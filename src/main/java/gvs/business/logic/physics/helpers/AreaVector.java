package gvs.business.logic.physics.helpers;

/**
 * Represents a speed or acceleration value in a given direction
 * 
 * @author aegli
 *
 */

public class AreaVector {

  private double x = 0;
  private double y = 0;
  private double length = 0;

  /**
   * Represents a default vector
   *
   */
  public AreaVector() {
    this.setX(0);
    this.setY(0);
  }

  /**
   * Represents a vector based on x and y components
   * 
   * @param x
   * @param y
   */
  public AreaVector(double x, double y) {
    this.setX(x);
    this.setY(y);
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

    setX(px - tx);
    setY(py - ty);

    update();
  }

  /**
   * Represents a scale. Multiplicated with a vector, it has an influence on how
   * fast particles are moving
   * 
   * @param newLength
   */
  public void scaleTo(double newLength) {
    setX(getX() * (newLength
        / Math.abs(getLength())/* Nur Vorzeichenwechsel immer ins Positive */));
    setY(getY() * (newLength / Math.abs(getLength())));

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
    this.setX(this.getX() - v.getX() * scale);
    this.setY(this.getY() - v.getY() * scale);

    update();
  }

  /**
   * Calculates the length of the hypotenuse based on x and y length
   *
   */
  private void update() {
    double pyth = getX() * getX() + getY() * getY();
    this.setLength(Math.sqrt(pyth));
  }

  /**
   * Adds another vector to the given vector. Represents new speed/acceleration
   * of a particle in the given direction
   * 
   * @param v
   */
  public void add(AreaVector v) {
    this.setX(this.getX() + v.getX());
    this.setY(this.getY() + v.getY());

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
    setX(0.0);
    setY(0.0);

    update();
  }

  /**
   * Inverts vector. Used by tractions and forces
   *
   */
  // TODO refactor name -> eg invert
  public void changeOfSign() {
    this.setX(-(this.getX()));
    this.setY(-(this.getY()));
  }

  /**
   * Reduces or stretches vector
   * 
   * @param v
   */
  public void reduceMultiplicator(double v) {
    this.setX(this.getX() * v);
    this.setY(this.getY() * v);
    update();
  }

  /**
   * Returns hypotenuse length of a vector
   * 
   * @return
   */
  public double getDistance() {
    return getLength();
  }

  /**
   * Returns the sum of speed in x and y direction. If this value is small, the
   * particle will be set to fixed
   * 
   * @return
   */
  // TODO refactor name -> getSpeedSum
  public double getSpeedSummary() {
    return getX() + getY();
  }

  /**
   * Returns length of x or y depending on input
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
   * Sets a new length x or y depending on input
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

    update();
  }

  public double getLength() {
    return length;
  }

  public void setLength(double length) {
    this.length = length;
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }
}
