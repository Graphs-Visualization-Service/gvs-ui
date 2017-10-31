package gvs;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import gvs.access.ClientConnectionFactory;

/**
 * Base module for guice dependecy injection
 * 
 * @author Michi
 *
 */
public class GuiceBaseModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new FactoryModuleBuilder()
        .build(ClientConnectionFactory.class));
  }

}
