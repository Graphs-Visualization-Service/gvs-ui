package gvs;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

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
    //TODO add bindings here
    // bind(Interface.class).to(ConcreteClass.class);
    bind(IPersistor.class).to(Persistor.class);//.in(Singleton.class);
  }

}
