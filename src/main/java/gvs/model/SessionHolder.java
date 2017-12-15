package gvs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Holds the model of the current session.
 * 
 * @author aegli
 *
 */
@Singleton
public class SessionHolder extends Observable {

  private Session currentSession = null;
  private final List<Session> sessions;

  private static final Logger logger = LoggerFactory
      .getLogger(SessionHolder.class);

  @Inject
  public SessionHolder() {
    this.sessions = new ArrayList<>();
  }

  /**
   * Sets currently requested session.
   * 
   * @param newSession
   *          new current session
   */
  public void setCurrentSession(Session newSession) {
    logger.info("Setting current session and notify observers.");

    currentSession = newSession;

    if (currentSession != null && currentSession.getGraphs().size() > 0) {
      Graph firstGraph = currentSession.getGraphs().get(0);
      currentSession.getGraphHolder().setCurrentGraph(firstGraph);

      setChanged();
      notifyObservers();
    }
  }

  /**
   * Returns current session, which is held by the model.
   * 
   * @return session controller
   */
  public Session getCurrentSession() {
    return currentSession;
  }

  /**
   * Add a session to the SessionList if the session is not already contained in
   * the List.
   * 
   * @param session
   *          new session
   * @return In the case of a new session, the added session is returned. In the
   *         case of a duplicated session, the original session is returned.
   */
  public Session addSession(Session session) {
    if (this.sessions.contains(session)) {
      int index = this.sessions.indexOf(session);
      return this.sessions.get(index);
    } else {
      this.sessions.add(session);
      return session;
    }

  }

  /**
   * Remove a session.
   * 
   * @param session
   *          session to delete
   */
  public void removeSession(Session session) {
    this.sessions.remove(session);
  }

  /**
   * Returns available session for displaying in combobox.
   * 
   * @return sessionControllers
   */
  public List<Session> getSessions() {
    return sessions;
  }
}
