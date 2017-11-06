package gvs.business.logic.physics.ticker;

/**
 * Ticker for the layouting area
 * 
 * @author aegli
 *
 */
public class AreaTicker extends Thread {

  private double delay;
  private long iteration;
  private Tickable tickable;
  private double rate, desiredRate;
  private boolean drop = false;

  private HitsPerSecond hitsPerSecond;
  private boolean active;
  private boolean shouldStop;

  private static final String THREAD_NAME = "Area Ticker Thread";

  public AreaTicker(Tickable tickable, double desiredRate) {
    super(THREAD_NAME);

    setDesiredRateAndDelay(desiredRate);

    this.tickable = tickable;
    this.iteration = 0;
    this.hitsPerSecond = new HitsPerSecond(10);
    this.drop = false;
    this.active = true;

    setPriority(Thread.MIN_PRIORITY);
  }

  private void setDesiredRateAndDelay(double rate) {
    rate = Math.max(5.0, rate);
    rate = Math.min(50, rate);
    this.desiredRate = rate;
    this.delay = (1000 / rate);
  }

  public void run() {
    while (!shouldStop) {
      rate = (hitsPerSecond.getHitsPerSecond());
      if (rate < desiredRate) {
        delay *= .99999;
      } else if (rate > desiredRate) {
        delay *= 1.00001;
      }

      hitsPerSecond.doHit();
      if (active) {
        if ((rate) < (desiredRate) && iteration % 5 == 0) {
          drop = true;
        } else {
          drop = false;
        }

        long time = System.currentTimeMillis();

        // call layout controller
        tickable.tick(rate, rate / desiredRate, drop, iteration, time);
        try {
          sleep((long) delay, 10000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      iteration++;
    }
  }

  public void shutdown() {
    shouldStop = true;
  }

  public void activateTicker() {
    active = true;
  }

}
