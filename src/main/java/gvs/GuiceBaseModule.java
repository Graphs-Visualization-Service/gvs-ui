package gvs;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import gvs.access.ClientConnectionFactory;
import gvs.business.logic.graph.GraphSessionControllerFactory;

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
    install(
        new FactoryModuleBuilder().build(GraphSessionControllerFactory.class));
  }

}
