package gvs.business.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.graph.Session;

/**
 * In order to layout graphs in their own threads, it must be ensured that no
 * other data is sent to the layout modul. For this reason the monitor locks the
 * visualization modul until layouting
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
