package gvs.mock;
import java.util.ArrayList;
import java.util.List;

import gvs.business.logic.Session;
import gvs.business.model.SessionHolder;

public class SessionHolderMock extends SessionHolder {
  private final List<Session> sessions = new ArrayList<>();

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
  
  @Override
  public String toString() {
    return "GVS Mock of SessionHolder";
  }
}
