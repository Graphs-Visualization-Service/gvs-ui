package gvs;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import gvs.access.ClientConnectionFactory;
import gvs.access.Persistor;
import gvs.interfaces.IPersistor;

/**
 * Base module for guice dependecy injection
 * 
 * @author Michi
 *
 */
public class GuiceBaseModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IPersistor.class).to(Persistor.class);
    
    install(new FactoryModuleBuilder()
        .build(ClientConnectionFactory.class));
  }

}
