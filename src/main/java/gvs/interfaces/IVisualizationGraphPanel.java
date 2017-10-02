package gvs.interfaces;

import java.awt.event.MouseEvent;
import java.util.Observable;

public interface IVisualizationGraphPanel extends IVisualizationPanel {

  public abstract void update(Observable o, Object arg);

  public abstract void mouseDragged(MouseEvent evt);

  public abstract void mouseMoved(MouseEvent e);

  public abstract void mouseClicked(MouseEvent evt);

  public abstract void mousePressed(MouseEvent evt);

  public abstract void mouseReleased(MouseEvent e);

  public abstract void mouseEntered(MouseEvent e);

  public abstract void mouseExited(MouseEvent e);

}
