package gvs.business.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.access.Persistor;
import gvs.business.logic.graph.Session;
import gvs.business.logic.graph.SessionFactory;
import gvs.business.model.SessionHolder;
import gvs.business.model.graph.Graph;
import gvs.interfaces.ISession;

/**
 * The Application Controller reacts on events from the user or newly received
 * data. The requested operations will be executed.
 * 
 * @author aegli
 *
 */
@Singleton
public class ApplicationController {

  private final SessionFactory sessionFactory;
  private final Persistor persistor;
  private final SessionHolder sessionHolder;

  private static final Logger logger = LoggerFactory
      .getLogger(ApplicationController.class);

  /**
   * Constructor.
   * 
   * @param sessionHolder
   *          wrapper for the current session
   * @param persistor
   *          persistor
   * @param graphSessionFactory
   *          factory for new sessions
   */
  @Inject
  public ApplicationController(SessionHolder sessionHolder, Persistor persistor,
      SessionFactory graphSessionFactory) {

    this.sessionHolder = sessionHolder;
    this.persistor = persistor;
    this.sessionFactory = graphSessionFactory;
  }

  /**
   * Sets session chosen from drop down, informs model and updates view.
   * 
   * @param session
   *          new current session
   */
  public synchronized void changeCurrentSession(ISession session) {
    sessionHolder.setCurrentSession(session);
  }

  /**
   * Loads a session from a specific file.
   * 
   * @param fileName
   *          fileName
   */
  public synchronized void loadStoredSession(String fileName) {
    logger.info("Load session from filesystem");
    ISession loadedSession = persistor.loadFile(fileName);

    sessionHolder.addSession(loadedSession);
    sessionHolder.setCurrentSession(loadedSession);

    loadedSession.layoutCurrentGraph(null);
  }

  /**
   * Deletes a chosen session.
   * 
   * @param pSessionController
   *          SessionController
   */
  public synchronized void deleteSession(ISession pSessionController) {
    logger.info("Delete session");

    sessionHolder.removeSession(pSessionController);

    if (sessionHolder.getSessions().size() > 0) {
      logger.debug("Session controller deleted. Set former graph session");

      int size = sessionHolder.getSessions().size();
      ISession mostRecentSession = sessionHolder.getSessions().get(size - 1);
      sessionHolder.setCurrentSession(mostRecentSession);

    } else {
      // when the last session is deleted, create empty dummy controller
      // otherwise session-bindings for UI would have to be unbound etc.
      logger.debug("Set empty graph session");
      sessionHolder
          .setCurrentSession(sessionFactory.createSession(-1, "", false));
    }
  }

  /**
   * Adds a new graph model, if an associated session exists, adds model to
   * session. Otherwise, creates a new graph session
   * 
   * @param graph
   *          graphModel
   * @param sessionId
   *          Id
   * @param sessionName
   *          sessionName
   */
  public synchronized void addGraphToSession(Graph graph, long sessionId,
      String sessionName, boolean isTreeSession) {

    logger.info("Received new graph");

    boolean isSessionExisting = false;
    for (ISession session : sessionHolder.getSessions()) {
      if (session.getId() == sessionId) {

        logger.info("Add graph to exsting session");
        Session existingSession = (Session) session;
        existingSession.addGraph(graph);
        existingSession.changeCurrentGraphToLast();
        existingSession.layoutCurrentGraph(null);

        isSessionExisting = true;
      }
    }

    if (!isSessionExisting) {
      logger.info("Create new session");
      Session newSession = sessionFactory.createSession(sessionId, sessionName,
          isTreeSession);
      newSession.addGraph(graph);
      newSession.getGraphHolder().setCurrentGraph(graph);
      newSession.layoutCurrentGraph(null);

      sessionHolder.addSession(newSession);
      sessionHolder.setCurrentSession(newSession);
    }
  }
}
