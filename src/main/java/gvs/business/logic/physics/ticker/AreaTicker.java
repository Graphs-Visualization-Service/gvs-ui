package gvs.business.logic.physics.ticker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Ticker for the layouting area
 * 
 * @author aegli
 *
 */
public class AreaTicker extends Thread {

  private double delay;
  private Tickable callbackTickable;

  private volatile boolean stop;

  private static final String THREAD_NAME = "Area Ticker Thread ";

  private static final Logger logger = LoggerFactory
      .getLogger(AreaTicker.class);

  @Inject
  public AreaTicker(@Assisted Tickable tickable, @Assisted double tickRate) {
    int oneSecond = 1000;
    this.delay = (oneSecond / tickRate);
    this.callbackTickable = tickable;

    setPriority(Thread.MIN_PRIORITY);
  }

  /**
   * Stop this ticker thread.
   */
  public void terminate() {
    stop = true;
  }

  @Override
  public void run() {
    setName(THREAD_NAME + getId());

    while (!stop) {
      try {
        sleep((long) delay, 10_000);

        logger.info("Tick: Update view");
        callbackTickable.tick();

      } catch (InterruptedException e) {
        stop = true;
      }
    }
  }
}
