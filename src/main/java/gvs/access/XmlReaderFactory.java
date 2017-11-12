package gvs.access;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface XmlReaderFactory {

  GvsXmlReader create(String fileName);
}
