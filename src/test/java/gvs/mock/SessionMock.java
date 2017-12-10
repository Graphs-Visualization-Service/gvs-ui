package gvs.mock;


import gvs.business.logic.ISessionType;
import gvs.business.logic.Session;

public class SessionMock extends Session {
  
  public SessionMock(
      long sessionId, String sessionName) {
    super(null, null, null, sessionId, sessionName);
  }
  
  @Override
  public ISessionType getSessionType() {
    return new SessionTypeMock();
  }
  
  @Override
  public String toString() {
    return "GVS Mock of Session";
  }
  

}
