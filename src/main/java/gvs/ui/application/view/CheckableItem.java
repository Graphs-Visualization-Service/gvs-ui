package gvs.ui.application.view;

import gvs.interfaces.IGraphSessionController;
import gvs.interfaces.ISessionController;

/**
 * Displays all sessions which are available. Used for displaying the save menu
 * items
 * 
 * @author aegli
 *
 */
class CheckableItem {
  private ISessionController sessionController = null;
  private String sessionName = null;
  private String sessionTypeName = null;
  private long sessionId = 0;
  private boolean isSelected = false;

  /**
   * Creates checkable items in saving dialog based on available sessions in
   * queue
   * 
   * @param sessionController
   */
  public CheckableItem(ISessionController sessionController) {
    this.sessionController = sessionController;
    this.sessionId = sessionController.getSessionId();
    this.sessionName = sessionController.getSessionName();

    @SuppressWarnings("rawtypes")
    Class[] interfaces = sessionController.getClass().getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      if (interfaces[i] == IGraphSessionController.class) {
        sessionTypeName = " [GRAPH]      ";
      } else {
        sessionTypeName = " [TREE]          ";
      }
    }
    isSelected = false;
  }

  /**
   * Marks item(session) for saving
   * 
   * @param b
   */
  public void setSelected(boolean b) {
    isSelected = b;
  }

  /**
   * Returns whether item(session) is marked for saving
   * 
   */
  public boolean isSelected() {
    return isSelected;
  }

  /**
   * Returns session controller
   * 
   */
  public ISessionController getSessionController() {
    return sessionController;
  }

  /**
   * Returns session id
   * 
   */
  public long getSessionId() {
    return sessionId;
  }

  /**
   * Returns session name for displaying in save dialog
   * 
   */
  public String getSessionName() {
    return sessionName;
  }

  /**
   * Returns type of session for displaying. Available types are "Tree" and
   * "Graph"
   * 
   */
  public String getSessionTypeName() {
    return sessionTypeName;
  }
}
