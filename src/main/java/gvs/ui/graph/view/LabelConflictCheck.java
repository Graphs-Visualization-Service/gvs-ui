package gvs.ui.graph.view;

import java.util.Iterator;
import java.util.Vector;

/**
 * Checks if conflicts happens between edge labels
 * 
 * @author aegli
 *
 */
public class LabelConflictCheck {

  private static final double minDistanceX = 20;
  private static final double minDistanceY = 15;
  private Vector<EdgeComponent> reservedPosition = null;
  private double distanceX = 0;
  private double distanceY = 0;

  /**
   * Builds an instance of LabelConflictCheck
   *
   */
  public LabelConflictCheck() {
    reservedPosition = new Vector<EdgeComponent>();
  }

  /**
   * Adds all edge label positions
   * 
   * @param pComponent
   */
  public void addLabelPos(EdgeComponent pComponent) {
    if (reservedPosition.size() > 0) {
      checkAgainstAll(pComponent);
    }
    reservedPosition.add(pComponent);
  }

  /**
   * Checks if edge labels are overlapping each other
   * 
   * @param pComponent
   */
  public void checkAgainstAll(EdgeComponent pComponent) {
    Iterator it = reservedPosition.iterator();

    while (it.hasNext()) {
      EdgeComponent fixed = (EdgeComponent) it.next();
      if (fixed == pComponent) {
      } else {
        collisionDetechtion(fixed, pComponent);
      }
    }
  }

  /**
   * Moves overlapping edge labels as long as no conflicts occure
   * 
   * @param pIsFixed
   * @param pComponent
   */
  public void collisionDetechtion(EdgeComponent pIsFixed,
      EdgeComponent pComponent) {
    distanceX = Math.abs(Math.abs(pIsFixed.getLabelXPos())
        - Math.abs(pComponent.getLabelXPos()));
    distanceY = Math.abs(Math.abs(pIsFixed.getLabelYPos())
        - Math.abs(pComponent.getLabelYPos()));

    if ((distanceX < minDistanceX) && (distanceY < minDistanceY)) {
      pComponent.moveAlongEdge();
      checkAgainstAll(pComponent);
    }
  }
}
