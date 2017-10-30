package gvs.business.logic.physics.ticker;

/**
 * Interface which is implemented by the area
 * 
 * @author aegli
 *
 */
public interface Tickable {

  void tick(double rate, double rateRatio, boolean drop, long iteration,
      long time);
}
