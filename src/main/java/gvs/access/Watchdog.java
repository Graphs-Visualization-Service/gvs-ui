package gvs.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.util.Configuration;

/**
 * The Watchdog is responsible for severing stale client connections. Whenever a
 * client connection is established, the watchdog is started in a new thread.
 * During the connection the watchdog is being ''fed'' by the creator thread. If
 * the connection goes stale, i.e. if no new data is received for a certain time
 * interval, the watchdog goes hungry and forcefully severs the client
 * connection, so the service is accessible for another client.
 * 
 * @author mtrentini
 *
 */
@Singleton
public class Watchdog implements Runnable {
  private int stopwatch;
  private boolean isWatching;

  private final ConnectionMonitor monitor;

  private static final int WATCH_TIME = 1000;
  private static final int INTERVAL = 100;
  private static final Logger logger = LoggerFactory.getLogger(Watchdog.class);

  @Inject
  public Watchdog(ConnectionMonitor monitor) {
    this.monitor = monitor;
  }

  /**
   * Forcefully sever client connections, if the watchdog is not being fed.
   */
  @Override
  public void run() {
    stopwatch = WATCH_TIME;
    isWatching = true;
    while (isWatching && stopwatch > 0) {
      try {
        stopwatch -= INTERVAL;
        Thread.sleep(INTERVAL);
        logger.info("Watchdog is watching");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (isWatching) {
      logger.info("Watchdog forcefully releases service");
      monitor.releaseService(Configuration.getWatchdog());
    }
    isWatching = false;
    logger.info("Watchdog goes to sleep");
  }

  /**
   * Set the watchdog's timer back.
   */
  public void feed() {
    logger.info("Watchdog is beeing fed.");
    stopwatch = WATCH_TIME;
  }

  /**
   * Informs the watchdog, that its service is no longer required.
   */
  public void stopWatching() {
    isWatching = false;
  }

  /**
   * 
   * @return wheter the watchdog is active.
   */
  public boolean isWatching() {
    return isWatching;
  }
}
