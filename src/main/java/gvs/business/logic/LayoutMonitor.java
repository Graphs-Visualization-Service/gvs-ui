package gvs.business.logic;

import com.google.inject.Singleton;

/**
 * In order to compute the layout of a graph in its own thread, it must be
 * ensured that only one graph is layouted at a time. Therefore the monitor
 * locks the layouting process until its complition.
 * 
 * @author aegli
 *
 */
@Singleton
public class LayoutMonitor {

  private boolean locked = false;

  /**
   * Locks monitor, so other threads have to wait until former thread releases
   * lock
   */
  public synchronized void lock() throws InterruptedException {
    while (locked) {
      wait();
    }
    locked = true;
  }

  /**
   * Releases lock, so other threads can request lock
   *
   */
  public synchronized void unlock() {
    locked = false;
    notifyAll();
  }
}
