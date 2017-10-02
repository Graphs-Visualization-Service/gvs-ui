package gvs.ui.graph.layout.ticker;

/**
 * Interface which is implemented by the area
 * 
 * @author aegli
 *
 */
public interface Tickable {

  public void tick(double rate, double rateRatio, boolean drop, long iteration,
      long time);

}
