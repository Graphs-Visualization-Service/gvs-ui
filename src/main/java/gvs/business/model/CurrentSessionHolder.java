package gvs.business.model;

import java.util.Observable;

import com.google.inject.Singleton;

import gvs.interfaces.ISessionController;

/**
 * Holds the model of the current session.
 * 
 * @author aegli
 *
 */
@Singleton
public class CurrentSessionHolder extends Observable {

  private ISessionController currentSession = null;

  /**
   * Sets currently requested session.
   * 
   * @param newSession
   */
  public synchronized void setCurrentSession(ISessionController newSession) {
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
  public synchronized ISessionController getCurrentSession() {
    return currentSession;
  }
}
