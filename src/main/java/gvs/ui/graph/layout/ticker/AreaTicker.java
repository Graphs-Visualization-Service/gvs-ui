package gvs.ui.graph.layout.ticker;

/**
 * Ticker for the layouting area
 * 
 * @author aegli
 *
 */
public class AreaTicker extends Thread {

  private double delay = 0;
  private long iteration = 0;
  private Tickable tickable = null;
  private double rate, desiredRate;
  private boolean drop = false;

  private HitsPerSecond hitsPerSecond = null;
  private boolean active = false;
  private boolean shouldStop = false;

  public AreaTicker(Tickable tickable, double desiredRate) {
    setDesiredRate(desiredRate);
    this.tickable = tickable;
    this.iteration = 0;
    this.hitsPerSecond = new HitsPerSecond(10);
    drop = false;

    setPriority(Thread.MIN_PRIORITY);
    active = true;
  }

  public void setDesiredRate(double desiredRate) {
    desiredRate = Math.max(5.0, desiredRate);
    desiredRate = Math.min(50, desiredRate);
    this.desiredRate = desiredRate;
    this.delay = (1000 / desiredRate);
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

  public void startTicking() {
    active = true;
  }

}
