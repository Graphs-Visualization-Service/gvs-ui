package gvs.ui.application.model;

import java.util.Observable;

import gvs.interfaces.ISessionController;

/**
 * Holds the model of the current session
 * 
 * @author aegli
 *
 */
public class ApplicationModel extends Observable {

  private ISessionController sessionController = null;

  /**
   * Builds an instance of the application model
   *
   */
  public ApplicationModel() {

  }

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
