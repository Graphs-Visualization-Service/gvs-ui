package gvs.business.model;

import java.util.Observable;

import com.google.inject.Singleton;

import gvs.interfaces.ISessionController;

/**
 * Holds the model of the current session
 * 
 * @author aegli
 *
 */
@Singleton
public class ApplicationModel extends Observable {

  private ISessionController sessionController = null;

  /**
   * Sets currently requested session
   * 
   * @param sc
   */
  public void setSession(ISessionController sc) {
    this.sessionController = sc;
    setChanged();
    notifyObservers();
  }

  /**
   * Returns current session, which is held by the model
   * 
   * @return session controller
   */
  public ISessionController getSession() {
    return sessionController;
  }
}
