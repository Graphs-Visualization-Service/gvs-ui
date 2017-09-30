package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface IBinaryNode extends INode{

	public abstract Color getFillColor();

	public abstract IBinaryNode getLeftChild();

	public abstract Color getLineColor();

	public abstract Stroke getLineStroke();

	public abstract String getNodeLabel();

	public abstract IBinaryNode getRightChild();

	public abstract int getMyTreePosition();

	public abstract void setMyTreePosition(int myTreePosition);
	
	public abstract int getMyTreeLevel();

	public abstract void setMyTreeLevel(int myTreeLevel) ;

}