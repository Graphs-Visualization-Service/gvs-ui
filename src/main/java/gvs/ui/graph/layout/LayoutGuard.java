package gvs.ui.graph.layout;

import gvs.ui.graph.layout.helpers.Area;

import java.util.*;

/**
 * After a defined timeout, the layout process will be stopped
 * 
 * @author aegli
 *
 */
public class LayoutGuard extends TimerTask {

  private Area universe = null;

  public LayoutGuard(Area pUniverse) {
    universe = pUniverse;
  }

  public void run() {
    universe.setAreaState(true);
  }
}
