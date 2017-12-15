package gvs.mock;

import gvs.model.ISessionType;
import gvs.model.Session;

public class SessionMock extends Session {

  public SessionMock(long sessionId, String sessionName) {
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
