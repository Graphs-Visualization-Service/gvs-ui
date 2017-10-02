package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;
import java.util.Vector;

public interface IDefaultNode extends INode {

  public abstract Vector getChilds();

  public abstract Color getFillColor();

  public abstract Color getLineColor();

  public abstract Stroke getLineStroke();

  public abstract String getNodeLabel();

}
