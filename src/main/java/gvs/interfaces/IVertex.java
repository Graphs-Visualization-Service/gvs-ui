package gvs.interfaces;

import java.util.Observer;

import gvs.business.model.graph.NodeStyle;
import gvs.util.FontAwesome.Glyph;

public interface IVertex {

  long getId();

  String getLabel();

  double getXPosition();

  double getYPosition();

  void setXPosition(double xPos);

  void setYPosition(double yPos);

  void updateCoordinates(double xPos, double yPos);

  boolean isFixedPositioned();
  
  boolean isStable();

  void setIsStable(boolean isStable);

  Glyph getIcon();

  NodeStyle getStyle();

  void addObserver(Observer o);

}
