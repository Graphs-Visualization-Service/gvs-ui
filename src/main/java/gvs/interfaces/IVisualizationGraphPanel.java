package gvs.interfaces;

import java.awt.event.MouseEvent;
import java.util.Observable;

public interface IVisualizationGraphPanel extends IVisualizationPanel {

  void update(Observable o, Object arg);

  void mouseDragged(MouseEvent evt);

  void mouseMoved(MouseEvent e);

  void mouseClicked(MouseEvent evt);

  void mousePressed(MouseEvent evt);

  void mouseReleased(MouseEvent e);

  void mouseEntered(MouseEvent e);

  void mouseExited(MouseEvent e);

}
