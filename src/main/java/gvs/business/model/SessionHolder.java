package gvs.business.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.Session;

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
   */
  public synchronized void setCurrentSession(Session newSession) {
    logger.info("Setting current session and notify observers.");

    this.currentSession = newSession;
    currentSession.changeCurrentGraphToFirst();
    setChanged();
    notifyObservers();
  }

  /**
   * Returns current session, which is held by the model.
   * 
   * @return session controller
   */
  public synchronized Session getCurrentSession() {
    return currentSession;
  }

  /**
   * Add a session.
   * 
   * @param session
   *          new session
   */
  public synchronized void addSession(Session session) {
    this.sessions.add(session);
  }

  /**
   * Remove a session.
   * 
   * @param session
   *          session to delete
   */
  public synchronized void removeSession(Session session) {
    this.sessions.remove(session);
  }

  /**
   * Returns available session for displaying in combobox.
   * 
   * @return sessionControllers
   */
  public synchronized List<Session> getSessions() {
    return sessions;
  }
}
