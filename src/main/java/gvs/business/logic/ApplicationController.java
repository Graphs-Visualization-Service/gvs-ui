package gvs.business.logic;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.access.Persistor;
import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.SessionHolder;

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
   * @param sessionFactory
   *          factory for new sessions
   */
  @Inject
  public ApplicationController(SessionHolder sessionHolder, Persistor persistor,
      SessionFactory sessionFactory) {

    this.sessionHolder = sessionHolder;
    this.persistor = persistor;
    this.sessionFactory = sessionFactory;
  }

  /**
   * Loads a session from a specific file.
   * 
   * @param fileName
   *          fileName
   */
  public synchronized void loadStoredSession(String fileName) {
    logger.info("Loading session from filesystem...");
    Session loadedSession = persistor.loadFile(fileName);

    if (loadedSession != null) {
      logger.info("Set loaded session as current session");
      loadedSession = sessionHolder.addSession(loadedSession);
      sessionHolder.setCurrentSession(loadedSession);

      logger.info("Layouting loaded session...");
      ILayouter layouter = loadedSession.getSessionType().getLayouter();
      layouter.layout(loadedSession, true, null);
    }

  }

  /**
   * Save a session to a specific file.
   * 
   * @param file
   *          file
   */
  public void saveSession(Session session, File file) {
    persistor.saveToDisk(session, file);
  }

  /**
   * Deletes an active session.
   * 
   * @param session
   *          the session to be deleted
   */
  public synchronized void deleteSession(Session session) {
    logger.info("Delete session");

    sessionHolder.removeSession(session);

    if (sessionHolder.getSessions().size() > 0) {
      logger.debug("Session controller deleted. Set former graph session");

      int size = sessionHolder.getSessions().size();
      Session mostRecentSession = sessionHolder.getSessions().get(size - 1);
      sessionHolder.setCurrentSession(mostRecentSession);
    } else {
      sessionHolder.setCurrentSession(null);
    }
  }

  /**
   * Set session, informs model and updates view.
   * 
   * @param session
   *          new current session
   */
  public synchronized void changeCurrentSession(Session session) {
    sessionHolder.setCurrentSession(session);
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
   * @param sessionType
   *          session type
   */
  public synchronized void addGraphToSession(Graph graph, long sessionId,
      String sessionName, ISessionType sessionType) {

    logger.info("Received new graph");

    Session session = null;
    for (Session existingSession : sessionHolder.getSessions()) {
      if (existingSession.getId() == sessionId) {
        logger.info("Add graph to exsting session");
        session = existingSession;
      }
    }
    if (session == null) {
      logger.info("Create new session");
      session = sessionFactory.createSession(sessionType, sessionId,
          sessionName);
      sessionHolder.addSession(session);
    }

    session.addGraph(graph);

    ILayouter layouter = session.getSessionType().getLayouter();
    layouter.layout(session, true, null);

    sessionHolder.setCurrentSession(session);
    session.getGraphHolder().setCurrentGraph(graph);
  }
}
