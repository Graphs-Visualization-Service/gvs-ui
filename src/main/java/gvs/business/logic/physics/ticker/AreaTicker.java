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
  private double desiredRate;

  private HitsPerSecond hitsPerSecond;
  private Tickable callbackTickable;

  private volatile boolean stop;

  private static final String THREAD_NAME = "Area Ticker Thread";

  private static final Logger logger = LoggerFactory
      .getLogger(AreaTicker.class);

  @Inject
  public AreaTicker(@Assisted Tickable tickable, @Assisted double desiredRate) {
    super(THREAD_NAME);

    double maxRate = Math.max(5.0, desiredRate);
    double minRate = Math.min(50, maxRate);

    this.desiredRate = minRate;
    this.delay = (1000 / minRate);
    this.callbackTickable = tickable;
    this.hitsPerSecond = new HitsPerSecond(10);

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
    while (!stop) {
      try {
        double rate = hitsPerSecond.getHitsPerSecond();
        hitsPerSecond.insertCurrentTimestamp();
        if (rate < desiredRate) {
          delay *= .99999;
        } else if (rate > desiredRate) {
          delay *= 1.00001;
        }
        sleep((long) delay, 10000);

        logger.info("Tick: Update view");
        callbackTickable.tick();

      } catch (InterruptedException e) {
        stop = true;
      }
    }
  }
}
