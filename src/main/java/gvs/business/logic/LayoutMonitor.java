package gvs.business.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * In order to compute the layout of a graph in its own thread, it must be
 * ensured that only one graph is layouted at a time. Therefore the monitor
 * locks the layouting process until its completion.
 * 
 * @author aegli
 *
 */
@Singleton
public class LayoutMonitor {

  private boolean locked = false;
  private static final Logger logger = LoggerFactory
      .getLogger(LayoutMonitor.class);

  /**
   * Locks monitor, so other threads have to wait until former thread releases
   * lock
   */
  public synchronized void lock() throws InterruptedException {
    while (locked) {
      wait();
    }
    locked = true;
    logger.info("Layouter locked");
  }

  /**
   * Releases lock, so other threads can request lock
   *
   */
  public synchronized void unlock() {
    locked = false;
    notifyAll();
    logger.info("Layouter unlocked");
  }
}
