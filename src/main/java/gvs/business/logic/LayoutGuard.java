package gvs.business.logic;

import java.util.TimerTask;

import gvs.business.logic.physics.helpers.Area;

/**
 * Guarantees that the layout is not endlessly calculated.
 * 
 * Once scheduled, this LayoutGuard will set the area to stable, which will stop
 * the calculation.
 * 
 * The {@link Layouter} schedules an instance of this class according to
 * the default delay.
 * 
 * @author mwieland
 *
 */
public class LayoutGuard extends TimerTask {

  private Area currentArea = null;

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
