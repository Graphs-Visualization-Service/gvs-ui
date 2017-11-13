package gvs.business.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.access.Persistor;
import gvs.business.logic.graph.Session;
import gvs.business.logic.graph.SessionFactory;
import gvs.business.logic.tree.TreeSessionController;
import gvs.business.model.SessionHolder;
import gvs.business.model.graph.Graph;
import gvs.business.model.tree.Tree;
import gvs.interfaces.ISession;
import gvs.interfaces.ITreeSessionController;

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
   * @param pSessionController
   *          sessionController
   */
  public synchronized void changeCurrentSession(ISession pSessionController) {
    sessionHolder.setCurrentSession(pSessionController);
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
      sessionHolder
          .setCurrentSession(sessionHolder.getSessions().iterator().next());

    } else {

      // when the last session is deleted, create empty dummy controller
      // otherwise session-bindings for UI would have to be unbound etc.
      logger.debug("Set empty graph session");
      sessionHolder.setCurrentSession(sessionFactory.create(-1, ""));
    }
  }

  /**
   * Adds a new tree model, if an associated session exists, adds model to
   * session. Otherwise, creates a new tree session
   * 
   * @param pTreeModel
   *          TreeModel
   * @param pId
   *          Id
   * @param pSessionName
   *          SessionName
   */
  public synchronized void addTreeToSession(Tree pTreeModel, long pId,
      String pSessionName) {
    logger.info("New Tree arrived");

    // TODO merge with addGraphToSession
    Iterator<ISession> sessionIt = sessionHolder.getSessions().iterator();
    boolean isSessionExisting = false;
    while (sessionIt.hasNext()) {
      ISession sc = (ISession) (sessionIt.next());
      if (sc.getId() == pId) {
        logger.debug("Add tree to exsting session");
        ((ITreeSessionController) sc).addTreeModel(pTreeModel);
        isSessionExisting = true;
      }
    }
    if (!isSessionExisting) {
      logger.debug("Build new tree session");
      ITreeSessionController newSession = new TreeSessionController(pId,
          pSessionName, pTreeModel);
      sessionHolder.addSession(newSession);
      logger.debug("Set session as actual model");
      sessionHolder.setCurrentSession(newSession);
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
      String sessionName) {

    logger.info("Received new graph");

    boolean isSessionExisting = false;
    for (ISession session : sessionHolder.getSessions()) {
      if (session.getId() == sessionId) {

        logger.info("Add graph to exsting session");
        Session existingSession = (Session) session;
        existingSession.addGraph(graph);
        existingSession.layoutCurrentGraph(null);

        isSessionExisting = true;
      }
    }

    if (!isSessionExisting) {
      logger.info("Create new session");

      Session newSession = sessionFactory.create(sessionId, sessionName);
      newSession.addGraph(graph);
      newSession.layoutCurrentGraph(null);

      sessionHolder.addSession(newSession);
      sessionHolder.setCurrentSession(newSession);
    }
  }
}
