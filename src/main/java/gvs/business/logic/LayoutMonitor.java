package gvs.business.logic;

/**
 * In order to layout graphs in their own threads, it must be ensured that no
 * other data is sent to the layout modul. For this reason the monitor locks the
 * visualization modul until layouting
 * 
 * @author aegli
 *
 */
public class LayoutMonitor {

  private boolean locked = false;
  private static LayoutMonitor myMonitor = null;

  private LayoutMonitor() {
  }

  /**
   * Returns an instance of the looking monitor
   */
  public static synchronized LayoutMonitor getInstance() {
    if (getMyMonitor() == null) {
      setMyMonitor(new LayoutMonitor());
    }
    return getMyMonitor();
  }

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

  public static LayoutMonitor getMyMonitor() {
    return myMonitor;
  }

  public static void setMyMonitor(LayoutMonitor myMonitor) {
    LayoutMonitor.myMonitor = myMonitor;
  }
}
