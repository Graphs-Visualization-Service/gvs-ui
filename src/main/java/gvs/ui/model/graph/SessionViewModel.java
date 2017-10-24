package gvs.ui.model.graph;

import gvs.interfaces.ISessionController;

/**
 * Represents the currently loaded session
 * 
 * @author Michi
 *
 */
public class SessionViewModel {

  private ISessionController sessionController;

  /**
   * Creates a new graph view model
   * 
   * @param sessionController
   *          related session controller
   */
  public SessionViewModel(ISessionController sessionController) {
    this.sessionController = sessionController;
  }
}
