package gvs.business.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.interfaces.ISession;

/**
 * Holds the model of the current session.
 * 
 * @author aegli
 *
 */
@Singleton
public class SessionHolder extends Observable {

  private ISession currentSession = null;
  private final Collection<ISession> sessions;

  private static final Logger logger = LoggerFactory
      .getLogger(SessionHolder.class);

  @Inject
  public SessionHolder() {
    this.sessions = new HashSet<>();
  }

  /**
   * Sets currently requested session.
   * 
   * @param newSession
   */
  public synchronized void setCurrentSession(ISession newSession) {
    logger.info("Setting current session and notify observers.");

    this.currentSession = newSession;
    this.currentSession.changeCurrentGraphToFirst();
    setChanged();
    notifyObservers();
  }

  /**
   * Returns current session, which is held by the model.
   * 
   * @return session controller
   */
  public synchronized ISession getCurrentSession() {
    return currentSession;
  }

  /**
   * Add a session.
   * 
   * @param session
   *          new session
   */
  public synchronized void addSession(ISession session) {
    this.sessions.add(session);
  }

  /**
   * Remove a session.
   * 
   * @param session
   *          session to delete
   */
  public synchronized void removeSession(ISession session) {
    this.sessions.remove(session);
  }

  /**
   * Returns available session for displaying in combobox.
   * 
   * @return sessionControllers
   */
  public synchronized Collection<ISession> getSessions() {
    return sessions;
  }
}
