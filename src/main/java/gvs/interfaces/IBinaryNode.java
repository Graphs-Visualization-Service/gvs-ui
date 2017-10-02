package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface IBinaryNode extends INode {

  Color getFillColor();

  IBinaryNode getLeftChild();

  Color getLineColor();

  Stroke getLineStroke();

  String getNodeLabel();

  IBinaryNode getRightChild();

  int getMyTreePosition();

  void setMyTreePosition(int myTreePosition);

  int getMyTreeLevel();

  void setMyTreeLevel(int myTreeLevel);

}
