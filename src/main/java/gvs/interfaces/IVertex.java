package gvs.interfaces;

import java.awt.Color;
import java.awt.Stroke;

public interface IVertex {

	public abstract boolean isRelative();

	public abstract String getLabel();

	public abstract Color getLineColor();

	public abstract Stroke getLineStroke();

	public abstract double getXPosition();

	public abstract double getYPosition();
	
	public abstract boolean isFixedPosition();

	public abstract void setXPosition(double position);

	public abstract void setYPosition(double position);
	
	public abstract void setFixedPosition(boolean isFixed);

	public abstract long getId();
}
