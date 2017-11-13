package gvs.interfaces;

import java.util.Observer;

import gvs.business.model.graph.NodeStyle;
import gvs.util.FontAwesome.Glyph;

public interface IVertex {

  boolean isFixedPositioned();

  String getLabel();

  double getXPosition();

  double getYPosition();

  void updateCoordinates(double xPos, double yPos);

  boolean isLayouted();

  void setXPosition(double position);

  void setYPosition(double position);

  void setIsLayouted(boolean isFixed);

  long getId();

  Glyph getIcon();

  NodeStyle getStyle();

  void addObserver(Observer o);

}
