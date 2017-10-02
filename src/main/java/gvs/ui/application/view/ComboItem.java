package gvs.ui.application.view;

import gvs.interfaces.ISessionController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents all available sessions, which can be choosed by the user
 * 
 * @author aegli
 *
 */
public class ComboItem {

  private ISessionController sessionController = null;
  private String sessionName = null;
  private String comboText = null;
  private long sessionId = 0;
  private Date date = null;
  private SimpleDateFormat dateformat = null;
  private boolean isShown;

  /**
   * Represents available sessions which are displayed in combobox
   *
   */
  public ComboItem() {
    this.sessionName = "--Choose Session--";
    this.setEnabled(false);
  }

  /**
   * Representing an item in combobox
   * 
   * @param sessionController
   */
  public ComboItem(ISessionController sessionController) {
    this.sessionController = sessionController;
    this.sessionName = sessionController.getSessionName();
    this.sessionId = sessionController.getSessionId();

    this.date = new Date(sessionId);
    this.dateformat = new SimpleDateFormat("yyyyMMddHHmmssS");
    this.comboText = sessionName + "/" + dateformat.format(date);
  }

  /**
   * Returns combo text
   * 
   */
  public String getComboText() {
    return comboText;
  }

  /**
   * Is set true when item is currently selected
   * 
   * @param state
   */
  public void setEnabled(boolean state) {
    setShown(state);
  }

  /**
   * Returns whether item is currently selected
   */
  public boolean isShown() {
    return isShown;
  }

  /**
   * Returns a reference of currently selected session
   * 
   */
  public ISessionController getSessionController() {
    return sessionController;
  }

  /**
   * Returns an id of currently selected session
   * 
   */
  public long getSessionId() {
    return sessionId;
  }

  /**
   * Returns session name of selected session
   * 
   */
  public String getSessionName() {
    return sessionName;
  }

  public void setShown(boolean isShown) {
    this.isShown = isShown;
  }
}
