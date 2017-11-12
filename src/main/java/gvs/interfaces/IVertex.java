package gvs.interfaces;

import java.util.Observer;

import gvs.business.model.graph.NodeStyle;
import gvs.util.FontAwesome.Glyph;

public interface IVertex {

  boolean isRelative();

  String getLabel();

  double getXPosition();

  double getYPosition();

  boolean isFixedPosition();

  void setXPosition(double position);

  void setYPosition(double position);

  void setFixedPosition(boolean isFixed);

  long getId();
  
  Glyph getIcon();

  NodeStyle getStyle();
  
  void addObserver(Observer o);
  
}
