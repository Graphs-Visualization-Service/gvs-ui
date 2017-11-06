package gvs.business.logic;

import java.util.TimerTask;

import gvs.business.logic.physics.helpers.Area;

/**
 * Guarantees that the layout is not infinitely calculated.
 * 
 * Once scheduled, this LayoutGuard will set the area to stable, which will stop
 * the calculation.
 * 
 * The {@link LayoutController} schedules a instance of this class according to
 * default delay.
 * 
 * @author mwieland
 *
 */
public class LayoutGuard extends TimerTask {

  private Area currentArea = null;

  /**
   * Default constructor.
   * 
   * @param area
   *          area to stabilize
   */
  public LayoutGuard(Area area) {
    currentArea = area;
  }

  /**
   * Stabilize stored area.
   */
  public void run() {
    currentArea.setIsStable(true);
  }
}
