package gvs.interfaces;

import java.awt.Dimension;
import java.awt.Graphics;

public interface IVertexComponent {

  void moveBy(int pDx, int pDy);

  void paint(Graphics g);

  void setDimension(Dimension pDim);

  void setActive(boolean pState);

  double getXPosition();

  double getYPosition();

}
