package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface IEdge {

  IVertex getEndVertex();

  boolean isDirected();

  String getLabel();

  Color getLineColor();

  Stroke getLineStroke();

  IVertex getStartVertex();

}
