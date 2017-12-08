package gvs.business.logic.layouter.tree;

import gvs.Configuration;
import gvs.business.model.tree.TreeVertex;

public class Bounds {
  private static final int VERTEX_LABEL_MARGIN = 30;
  private static final int VERTEX_HEIGHT = 60;
  private double boundsTop = Double.MAX_VALUE;
  private double boundsRight = Double.MIN_VALUE;
  private double boundsBottom = Double.MIN_VALUE;
  private double boundsLeft = Double.MAX_VALUE;

  public void updateBounds(TreeVertex vertex, double centerX, double centerY) {
    double vertexWidth = getVertexWidth(vertex);
    double vertexHeight = VERTEX_HEIGHT;
    double left = centerX - vertexWidth / 2;
    double right = centerX + vertexWidth / 2;
    double top = centerY - vertexHeight / 2;
    double bottom = centerY + vertexHeight / 2;
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
  
  private int getVertexWidth(TreeVertex v) {
    int labelWidth = v.getLabel().length() + VERTEX_LABEL_MARGIN;
    int maxWidth = Configuration.getMaxLabelLengthForTree();
    return Math.min(labelWidth, maxWidth);
  }
}
