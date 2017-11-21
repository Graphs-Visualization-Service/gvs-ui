package gvs.business.logic;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.logic.physics.helpers.Area;

/**
 * Guarantees that the layout is not endlessly calculated.
 * 
 * Once scheduled, this LayoutGuard will set the area to stable, which will stop
 * the calculation.
 * 
 * The {@link GraphLayouter} schedules an instance of this class according to the
 * default delay.
 * 
 * @author mwieland
 *
 */
public class GraphLayoutGuard extends TimerTask {

  private Area currentArea = null;
  private static final Logger logger = LoggerFactory
      .getLogger(GraphLayoutGuard.class);

  /**
   * Constructor.
   * 
   * @param area
   *          area to guard
   */
  public GraphLayoutGuard(Area area) {
    currentArea = area;
  }

  /**
   * Stabilize stored area.
   */
  public void run() {
    logger.info("Layout Guard activated. Layouting took more than 10 seconds");
    currentArea.setIsStable(true);
  }
}
