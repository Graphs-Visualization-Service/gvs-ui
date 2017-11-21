package gvs.business.logic.layouter.graph;

/**
 * Represents the dimension of the layouting area
 * 
 * @author aegli
 *
 */
public class AreaDimension {
  private AreaVector vector = null;
  private AreaPoint offset = new AreaPoint(0, 0);

  /**
   * Sets new area dimension
   * 
   * @param dx
   * @param dy
   */
  public AreaDimension(double dx, double dy) {
    this(new AreaVector(dx, dy));
  }

  /**
   * Sets new area dimension
   * 
   * @param vector
   */
  public AreaDimension(AreaVector vector) {
    this.vector = vector;
  }

  /**
   * Returns center of area
   * 
   * @return area point
   */
  public AreaPoint getCenter() {
    return new AreaPoint(vector.getX() / 2, vector.getY() / 2);
  }

  /**
   * Returns width of dimension, used by Layout Controller to set random x
   * points for each particle.
   * 
   * @return X dimension
   */
  public double dimensionWidth() {
    return vector.getX();
  }

  /**
   * Returns height of dimension, used by Layout Controller to set random y
   * points for each particle.
   * 
   * @return
   */
  public double dimensionHeight() {
    return vector.getY();
  }

  /**
   * Used for ckecking boundaries
   * 
   * @param i
   * @return
   */
  public double getStart(int i) {
    return 0 + offset.getField(i);
  }

  /**
   * Used for ckecking boundaries
   * 
   * @param i
   * @return
   */
  public double getEnd(int i) {
    return vector.getField(i);
  }

}
