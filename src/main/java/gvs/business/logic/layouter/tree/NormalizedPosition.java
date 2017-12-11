package gvs.business.logic.layouter.tree;

/**
 * The algorithm calculates the position starting with the root at 0. I.e. the
 * left children will get negative positions.
 * <p>
 * {@link NormalizedPosition} will normalize the position (given relative to the
 * root position), taking the current bounds into account. This way the left
 * most node bounds will start at x = 0, the top most node bounds at y = 0.
 */
public class NormalizedPosition {
  private double xRelativeToRoot;
  private double yRelativeToRoot;
  private Bounds bounds;

  public NormalizedPosition(double relativeToRootX, double relativeToRootY,
      Bounds bounds) {
    setLocation(relativeToRootX, relativeToRootY);
    this.bounds = bounds;
  }

  public double getX() {
    return xRelativeToRoot - bounds.getBoundsLeft();
  }

  public double getY() {
    return yRelativeToRoot - bounds.getBoundsTop();
  }

  public void setLocation(double relativeToRootX, double relativeToRootY) {
    this.xRelativeToRoot = relativeToRootX;
    this.yRelativeToRoot = relativeToRootY;
  }

  @Override
  public String toString() {
    return xRelativeToRoot + "," + yRelativeToRoot;
  }

}
