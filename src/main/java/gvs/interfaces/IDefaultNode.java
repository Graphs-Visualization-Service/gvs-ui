package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;
import java.util.Vector;

/**
 * Default Node Interface.
 */
public interface IDefaultNode extends INode {

  /**
   * getChilds.
   * 
   * @return vector of childs
   */
  @SuppressWarnings("rawtypes")
  Vector getChilds();

  /**
   * getFillColor.
   * 
   * @return color
   */
  Color getFillColor();

  /**
   * getLineColor.
   * 
   * @return color
   */
  Color getLineColor();

  /**
   * getLineStroke.
   * 
   * @return stroke
   */
  Stroke getLineStroke();

  /**
   * getNodeLabel.
   * 
   * @return label
   */
  String getNodeLabel();

}
