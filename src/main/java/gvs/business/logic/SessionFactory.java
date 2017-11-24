package gvs.business.logic;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface SessionFactory {

  /**
   * Construct a graph session with dependencies, which are not automatically
   * injectable.
   * 
   * @param sessionType
   *          type of the session (graph, tree, binary tree)
   * @param sessionId
   *          id of the session
   * @param sessionName
   *          name of the session
   * @return new instance
   */
  Session createSession(ISessionType sessionType, long sessionId,
      String sessionName);
}
