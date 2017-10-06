package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface IVertex {

  boolean isRelative();

  String getLabel();

  Color getLineColor();

  Stroke getLineStroke();

  double getXPosition();

  double getYPosition();

  boolean isFixedPosition();

  void setXPosition(double position);

  void setYPosition(double position);

  void setFixedPosition(boolean isFixed);

  long getId();
}