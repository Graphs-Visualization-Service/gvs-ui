package gvs.business.logic.layouter.graph;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author mwieland
 *
 */
public interface AreaTickerFactory {

  AreaTicker create(Tickable tickable, double desiredRate);
}
