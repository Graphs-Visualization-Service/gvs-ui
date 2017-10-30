package gvs.business.logic;

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
  private static Monitor myMonitor = null;

  private Monitor() {
  }

  /**
   * Returns an instance of the looking monitor
   */
  public static synchronized Monitor getInstance() {
    if (getMyMonitor() == null) {
      setMyMonitor(new Monitor());
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

  public static Monitor getMyMonitor() {
    return myMonitor;
  }

  public static void setMyMonitor(Monitor myMonitor) {
    Monitor.myMonitor = myMonitor;
  }
}
