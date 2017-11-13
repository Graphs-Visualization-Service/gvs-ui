package gvs;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import gvs.access.ClientConnectionFactory;
import gvs.access.XmlReaderFactory;
import gvs.business.logic.graph.SessionFactory;
import gvs.business.logic.graph.GraphSessionReplayFactory;
import gvs.business.logic.physics.ticker.AreaTickerFactory;

/**
 * Base module for guice dependecy injection
 * 
 * @author Michi
 *
 */
public class GuiceBaseModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder().build(ClientConnectionFactory.class));
    install(new FactoryModuleBuilder().build(SessionFactory.class));
    install(new FactoryModuleBuilder().build(GraphSessionReplayFactory.class));
    install(new FactoryModuleBuilder().build(XmlReaderFactory.class));
    install(new FactoryModuleBuilder().build(AreaTickerFactory.class));
  }

}
