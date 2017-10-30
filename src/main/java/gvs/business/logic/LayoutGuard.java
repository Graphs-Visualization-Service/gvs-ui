package gvs.business.logic;

import java.util.TimerTask;

import gvs.business.logic.physics.helpers.Area;

/**
 * After a defined timeout, the layout process will be stopped
 * 
 * @author aegli
 *
 */
public class LayoutGuard extends TimerTask {

  private Area universe = null;

  /**
   * Set area.
   * 
   * @param pUniverse
   *          area
   */
  public LayoutGuard(Area pUniverse) {
    universe = pUniverse;
  }

  /**
   * Set state on area
   */
  public void run() {
    universe.setAreaState(true);
  }
}
