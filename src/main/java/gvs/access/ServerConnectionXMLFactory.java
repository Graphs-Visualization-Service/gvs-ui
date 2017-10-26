package gvs.access;

import java.net.Socket;

import gvs.server.socket.ServerConnectionXML;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface ServerConnectionXMLFactory {

  ServerConnectionXML create(Socket clients);
}
