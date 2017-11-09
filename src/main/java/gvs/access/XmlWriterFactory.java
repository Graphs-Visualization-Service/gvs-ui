package gvs.access;

import java.net.Socket;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface XmlWriterFactory {

  XmlWriter create(String filename);
}
