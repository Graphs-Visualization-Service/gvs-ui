package gvs.mock;

import java.io.File;

import gvs.access.Persistor;
import gvs.model.Session;
import gvs.model.SessionFactory;

public class PersistorMock extends Persistor {
  private Session testSession;

  public PersistorMock(SessionFactory graphSessionFactory) {
    super(graphSessionFactory, null, null);
  }

  public void saveToDisk(Session session, File file) {

  }

  public Session loadFile(String pPath) {
    return testSession;
  }

  public void setTestSession(Session s) {
    testSession = s;
  }

  @Override
  public String toString() {
    return "GVS Mock of Persistor";
  }

}
