package gvs.interfaces;

import java.awt.Dimension;
import java.awt.Graphics;

public interface IVertexComponent {

  public abstract void moveBy(int pDx, int pDy);

  public abstract void paint(Graphics g);

  public abstract void setDimension(Dimension pDim);

  public abstract void setActive(boolean pState);

  public abstract double getXPosition();

  public abstract double getYPosition();

}
