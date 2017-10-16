package gvs.ui.graph.view;

import java.util.Iterator;
import java.util.Vector;

/**
 * Checks if conflicts happens between edge labels.
 * 
 * @author aegli
 *
 */
public class LabelConflictCheck {

  private static final double MIN_DISTANCE_X = 20;
  private static final double MIN_DISTANCE_Y = 15;
  private Vector<EdgeComponent> reservedPosition = null;
  private double distanceX = 0;
  private double distanceY = 0;

  /**
   * Builds an instance of LabelConflictCheck.
   *
   */
  public LabelConflictCheck() {
    reservedPosition = new Vector<EdgeComponent>();
  }

  /**
   * Adds all edge label positions.
   * 
   * @param pComponent edge component
   */
  public void addLabelPos(EdgeComponent pComponent) {
    if (reservedPosition.size() > 0) {
      checkAgainstAll(pComponent);
    }
    reservedPosition.add(pComponent);
  }

  /**
   * Checks if edge labels are overlapping each other.
   * 
   * @param pComponent
   *          edge component
   */
  public void checkAgainstAll(EdgeComponent pComponent) {
    Iterator<EdgeComponent> it = reservedPosition.iterator();

    while (it.hasNext()) {
      EdgeComponent fixed = (EdgeComponent) it.next();
      if (fixed != pComponent) {
        collisionDetechtion(fixed, pComponent);
      }
    }
  }

  /**
   * Moves overlapping edge labels as long as no conflicts occure.
   * 
   * @param pIsFixed
   *          edge componente
   * @param pComponent
   *          edge componente
   */
  public void collisionDetechtion(EdgeComponent pIsFixed,
      EdgeComponent pComponent) {
    distanceX = Math.abs(Math.abs(pIsFixed.getLabelXPos())
        - Math.abs(pComponent.getLabelXPos()));
    distanceY = Math.abs(Math.abs(pIsFixed.getLabelYPos())
        - Math.abs(pComponent.getLabelYPos()));

    if ((distanceX < MIN_DISTANCE_X) && (distanceY < MIN_DISTANCE_Y)) {
      pComponent.moveAlongEdge();
      checkAgainstAll(pComponent);
    }
  }
}
