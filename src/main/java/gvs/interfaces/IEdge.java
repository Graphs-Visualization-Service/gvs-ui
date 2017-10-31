package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;
import java.util.Observer;

public interface IEdge {

  IVertex getEndVertex();

  boolean isDirected();

  String getLabel();

  Color getLineColor();

  Stroke getLineStroke();

  IVertex getStartVertex();

  void addObserver(Observer o);

}
