package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface INode {

	public abstract Color getFillColor();

	public abstract Color getLineColor();

	public abstract Stroke getLineStroke();

	public abstract String getNodeLabel();
	
	public abstract double getXPosition();

	public abstract double getYPosition();
	
	public abstract long getNodeId(); 

	public abstract void setXPosition(double position);

	public abstract void setYPosition(double position);

	public abstract void hasParent(boolean hasParent);
	
	public abstract boolean hasParent();
}