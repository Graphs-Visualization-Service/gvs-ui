package gvs;

import com.google.inject.AbstractModule;

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
  }

}
