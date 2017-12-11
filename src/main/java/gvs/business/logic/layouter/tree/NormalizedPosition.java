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
  private double x_relativeToRoot;
  private double y_relativeToRoot;
  private Bounds bounds;

  public NormalizedPosition(double x_relativeToRoot, double y_relativeToRoot,
      Bounds bounds) {
    setLocation(x_relativeToRoot, y_relativeToRoot);
    this.bounds = bounds;
  }

  public double getX() {
    return x_relativeToRoot - bounds.getBoundsLeft();
  }

  public double getY() {
    return y_relativeToRoot - bounds.getBoundsTop();
  }

  public void setLocation(double x_relativeToRoot, double y_relativeToRoot) {
    this.x_relativeToRoot = x_relativeToRoot;
    this.y_relativeToRoot = y_relativeToRoot;
  }

  @Override
  public String toString() {
    return x_relativeToRoot + "," + y_relativeToRoot;
  }

}
