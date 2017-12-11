import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.io.File;

import javax.inject.Inject;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.jukito.TestScope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import gvs.access.Persistor;
import gvs.business.logic.ApplicationController;
import gvs.business.logic.Session;
import gvs.business.logic.SessionFactory;
import gvs.business.model.SessionHolder;
import gvs.mock.PersistorMock;
import gvs.mock.SessionHolderMock;
import gvs.mock.SessionMock;

@RunWith(JukitoRunner.class)
public class ApplicationControllerTest {
  public static class Module extends JukitoModule {
    @Inject
    private SessionFactory factory;

    private static final String SESSION_NAME = "TestSession";

    @Override
    protected void configureTest() {
      bindSpy(Persistor.class, new PersistorMock(factory))
          .in(TestScope.SINGLETON);
      bindSpy(SessionHolder.class, new SessionHolderMock())
          .in(TestScope.SINGLETON);
      bindSpy(Session.class, new SessionMock(0, SESSION_NAME))
          .in(TestScope.SINGLETON);
      install(new AbstractModule() {
        @Override
        protected void configure() {
          install(new FactoryModuleBuilder().build(SessionFactory.class));
        }
      });
    }
  }

  /**
   * an actual ApplicationController instance (not a mock!)
   */
  @Inject
  private ApplicationController appController;
  private Session session;
  private SessionHolder holder;
  private Persistor persistor;

  /**
   * 
   * @param holderMock
   *          A mock. The same instance is shared by appController
   * @param persistorMock
   *          A mock. The same instance is shared by appController
   * @param sessionMock
   *          A mock. The same instance is shared by appController
   */
  @Before
  public void setUp(SessionHolder holderMock, Persistor persistorMock,
      Session sessionMock) {
    this.session = sessionMock;
    this.holder = holderMock;
    this.persistor = persistorMock;
    ((PersistorMock) persistorMock).setTestSession(sessionMock);
  }

  @Test
  public void setUpAppControllerTest() {
    assertTrue(appController != null);
    assertTrue(session != null);
    assertTrue(holder != null);
    assertTrue(persistor != null);
  }

  @Test
  public void loadsSpecifiedSession() {
    String fileName = "session.gvs";
    appController.loadStoredSession(fileName);
    verify(persistor).loadFile(fileName);
    assertEquals(session, holder.getCurrentSession());
    assertTrue(holder.getSessions().contains(session));
  }

  @Test
  public void savesSpecifiedSession() {
    String fileName = "session.gvs";
    File file = new File(fileName);
    appController.saveSession(session, file);
    verify(persistor).saveToDisk(session, file);
  }

  @Test
  public void deleteSpecifiedSession() {
    appController.deleteSession(session);
    assertFalse(holder.getSessions().contains(session));
    assertEquals(null, holder.getCurrentSession());
  }

  @Test
  public void transferChangingCurrentSessionToSessionHolder() {
    appController.changeCurrentSession(session);
    verify(holder).setCurrentSession(session);
  }
}
