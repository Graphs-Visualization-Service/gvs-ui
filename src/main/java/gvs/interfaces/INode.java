package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface INode {

  Color getFillColor();

  Color getLineColor();

  Stroke getLineStroke();

  String getNodeLabel();

  double getXPosition();

  double getYPosition();

  long getNodeId();

  void setXPosition(double position);

  void setYPosition(double position);

  void hasParent(boolean hasParent);

  boolean hasParent();
}
