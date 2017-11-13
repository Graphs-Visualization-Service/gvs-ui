package gvs.business.logic.graph;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface SessionFactory {

  /**
   * Construct a graph session controller with dependencies, which are not
   * automatically injectable.
   * 
   * @param sessionId
   *          id of the session
   * @param sessionName
   *          name of the session
   * @return new instance
   */
  Session create(long sessionId, String sessionName);
}
