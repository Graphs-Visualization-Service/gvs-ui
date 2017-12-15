package gvs.model;

import java.util.Observer;

import gvs.model.styles.GVSStyle;
import gvs.util.FontAwesome.Glyph;

public interface IVertex {

  long getId();

  String getLabel();

  double getXPosition();

  double getYPosition();

  void setXPosition(double xPos);

  void setYPosition(double yPos);

  void updateCoordinates(double xPos, double yPos);

  boolean isUserPositioned();

  void setUserPositioned(boolean isUserPositioned);

  Glyph getIcon();

  int getMaxLabelLength();

  GVSStyle getStyle();

  void addObserver(Observer o);

  boolean isTreeVertex();

}
