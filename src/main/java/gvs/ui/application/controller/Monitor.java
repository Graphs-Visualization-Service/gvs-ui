package gvs.ui.application.controller;

/**
 * In order to layout graphs in their own threads, it must be ensured that no
 * other data is sent to the layout modul. For this reason the monitor locks the
 * visualization modul until layouting
 * 
 * @author aegli
 *
 */
public class Monitor {

  private boolean locked = false;
  public static Monitor myMonitor = null;

  private Monitor() {
  }

  /**
   * Returns an instance of the looking monitor
   */
  public synchronized static Monitor getInstance() {
    if (myMonitor == null) {
      myMonitor = new Monitor();
    }
    return myMonitor;
  }

  /**
   * Locks monitor, so other threads have to wait until former thread releases
   * lock
   */
  public synchronized void lock() throws InterruptedException {
    while (locked)
      wait();
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
