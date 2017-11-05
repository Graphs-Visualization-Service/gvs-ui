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
  public void setCurrentSession(ISessionController newSession) {
    this.currentSession = newSession;
    // TODO: this is important, so the currentGraphHolder notifies its
    // observers. but maybe this is strange for the user??
    this.currentSession.changeCurrentGraphToLast();
    setChanged();
    notifyObservers();
  }

  /**
   * Returns current session, which is held by the model.
   * 
   * @return session controller
   */
  public ISessionController getCurrentSession() {
    return currentSession;
  }
}
