package gvs.access;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * @author muriele
 *
 */
@Singleton
public class Watchdog implements Runnable{
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
    logger.info("Watchdog goes to sleep");
  }

  public void feed() {
    logger.info("Watchdog is beeing fed.");
    stopwatch = WATCH_TIME;
  }

  public void stopWatching() {
    isWatching = false;
  }
  
  public boolean isWatching() {
    return isWatching;
  }
}
