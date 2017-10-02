package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface IEdge {

  public abstract IVertex getEndVertex();

  public abstract boolean isDirected();

  public abstract String getLabel();

  public abstract Color getLineColor();

  public abstract Stroke getLineStroke();

  public abstract IVertex getStartVertex();

}
