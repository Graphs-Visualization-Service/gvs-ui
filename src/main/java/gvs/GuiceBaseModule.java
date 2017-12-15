package gvs;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import gvs.access.ClientConnectionFactory;
import gvs.business.logic.SessionReplayFactory;
import gvs.business.logic.layouter.graph.AreaTickerFactory;
import gvs.model.SessionFactory;

/**
 * Base module for guice dependecy injection
 * 
 * @author mwieland
 *
 */
public class GuiceBaseModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(ClientConnectionFactory.class));
    install(new FactoryModuleBuilder().build(SessionFactory.class));
    install(new FactoryModuleBuilder().build(SessionReplayFactory.class));
    install(new FactoryModuleBuilder().build(AreaTickerFactory.class));
  }

}
