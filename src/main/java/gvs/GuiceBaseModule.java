package gvs;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import gvs.access.ClientConnectionFactory;
import gvs.access.XmlReaderFactory;
import gvs.access.XmlWriterFactory;
import gvs.business.logic.graph.GraphSessionControllerFactory;
import gvs.business.logic.graph.GraphSessionReplayFactory;

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
    install(new FactoryModuleBuilder().build(GraphSessionReplayFactory.class));
    install(new FactoryModuleBuilder().build(XmlWriterFactory.class));
    install(new FactoryModuleBuilder().build(XmlReaderFactory.class));
  }

}
