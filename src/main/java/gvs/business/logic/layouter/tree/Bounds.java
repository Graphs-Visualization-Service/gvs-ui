package gvs.business.logic.layouter.tree;

import gvs.business.model.tree.TreeVertex;
import javafx.geometry.Rectangle2D;

public class Bounds {
  public static double boundsLeft = Double.MAX_VALUE;
  public static double boundsRight = Double.MIN_VALUE;
  public static double boundsTop = Double.MAX_VALUE;
  public static double boundsBottom = Double.MIN_VALUE;

  public static void updateBounds(TreeVertex node, double centerX,
      double centerY) {
    double width = node.getLabel().length();
    double height = 12;
    double left = centerX - width / 2;
    double right = centerX + width / 2;
    double top = centerY - height / 2;
    double bottom = centerY + height / 2;
    if (boundsLeft > left) {
      boundsLeft = left;
    }
    if (boundsRight < right) {
      boundsRight = right;
    }
    if (boundsTop > top) {
      boundsTop = top;
    }
    if (boundsBottom < bottom) {
      boundsBottom = bottom;
    }
  }

  /**
   * Returns the bounds of the tree layout.
   * <p>
   * The bounds of a TreeLayout is the smallest rectangle containing the bounds
   * of all nodes in the layout. It always starts at (0,0).
   * 
   * @return the bounds of the tree layout
   */
  public Rectangle2D getBounds() {
    return new Rectangle2D(0, 0, boundsRight - boundsLeft,
        boundsBottom - boundsTop);
  }
}
