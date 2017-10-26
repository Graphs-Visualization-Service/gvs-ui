package gvs;

import com.google.inject.AbstractModule;

import gvs.common.Persistor;
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
  }

}
