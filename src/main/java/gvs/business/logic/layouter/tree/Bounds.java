package gvs.business.logic.layouter.tree;

import gvs.business.model.tree.TreeVertex;
import javafx.geometry.Rectangle2D;

public class Bounds {
  private double boundsTop = Double.MAX_VALUE;
  private double boundsRight = Double.MIN_VALUE;
  private double boundsBottom = Double.MIN_VALUE;
  private double boundsLeft = Double.MAX_VALUE;

  public void updateBounds(TreeVertex node, double centerX, double centerY) {
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

  public double getBoundsLeft() {
    return boundsLeft;
  }

  public void setBoundsLeft(double boundsLeft) {
    this.boundsLeft = boundsLeft;
  }

  public double getBoundsRight() {
    return boundsRight;
  }

  public void setBoundsRight(double boundsRight) {
    this.boundsRight = boundsRight;
  }

  public double getBoundsTop() {
    return boundsTop;
  }

  public void setBoundsTop(double boundsTop) {
    this.boundsTop = boundsTop;
  }

  public double getBoundsBottom() {
    return boundsBottom;
  }

  public void setBoundsBottom(double boundsBottom) {
    this.boundsBottom = boundsBottom;
  }
}
