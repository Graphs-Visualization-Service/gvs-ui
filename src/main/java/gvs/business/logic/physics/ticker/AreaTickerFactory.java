package gvs.business.logic.physics.ticker;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface AreaTickerFactory {

  AreaTicker create(Tickable tickable, double desiredRate);
}
