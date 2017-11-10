package gvs.business.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.access.Persistor;
import gvs.business.logic.graph.GraphSessionFactory;
import gvs.business.logic.graph.Session;
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

  private final GraphSessionFactory graphSessionFactory;
  private final Persistor persistor;
  private final SessionHolder currentSessionHolder;

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
  public ApplicationController(SessionHolder sessionHolder,
      Persistor persistor, GraphSessionFactory graphSessionFactory) {

    this.currentSessionHolder = sessionHolder;
    this.persistor = persistor;
    this.graphSessionFactory = graphSessionFactory;
  }

  /**
   * Sets session chosen from drop down, informs model and updates view.
   * 
   * @param pSessionController
   *          sessionController
   */
  public synchronized void changeCurrentSession(ISession pSessionController) {
    currentSessionHolder.setCurrentSession(pSessionController);
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

    currentSessionHolder.addSession(loadedSession);
    currentSessionHolder.setCurrentSession(loadedSession);
  }

  /**
   * Deletes a chosen session.
   * 
   * @param pSessionController
   *          SessionController
   */
  public synchronized void deleteSession(ISession pSessionController) {
    logger.info("Delete session");

    currentSessionHolder.removeSession(pSessionController);

    if (currentSessionHolder.getSessions().size() > 0) {
      logger.debug("Session controller deleted. Set former graph session");
      currentSessionHolder.setCurrentSession(
          currentSessionHolder.getSessions().iterator().next());

    } else {

      // when the last session is deleted, create empty dummy controller
      // otherwise session-bindings for UI would have to be unbound etc.
      logger.debug("Set empty graph session");
      currentSessionHolder
          .setCurrentSession(graphSessionFactory.create(-1, "", null));
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

    Iterator<ISession> sessionIt = currentSessionHolder.getSessions()
        .iterator();
    boolean isSessionExisting = false;
    while (sessionIt.hasNext()) {
      ISession sc = (ISession) (sessionIt.next());
      if (sc.getSessionId() == pId) {
        logger.debug("Add tree to exsting session");
        ((ITreeSessionController) sc).addTreeModel(pTreeModel);
        isSessionExisting = true;
      }
    }
    if (!isSessionExisting) {
      logger.debug("Build new tree session");
      ITreeSessionController newSession = new TreeSessionController(pId,
          pSessionName, pTreeModel);
      currentSessionHolder.addSession(newSession);
      logger.debug("Set session as actual model");
      currentSessionHolder.setCurrentSession(newSession);
    }
  }

  /**
   * Adds a new graph model, if an associated session exists, adds model to
   * session. Otherwise, creates a new graph session
   * 
   * @param graph
   *          graphModel
   * @param pId
   *          Id
   * @param pSessionName
   *          sessionName
   */
  public synchronized void addGraphToSession(Graph graph, long pId,
      String pSessionName) {
    logger.info("New graph arrived");
    Iterator<ISession> sessionIt = currentSessionHolder.getSessions()
        .iterator();
    boolean isSessionExisting = false;
    while (sessionIt.hasNext()) {
      ISession sc = (ISession) (sessionIt.next());
      if (sc.getSessionId() == pId) {
        logger.debug("Add graph to exsting session");

        Session graphSessionController = (Session) sc;
        graphSessionController.addGraph(graph);
        graphSessionController.layout();

        isSessionExisting = true;
      }
    }
    if (!isSessionExisting) {
      logger.debug("Build new graph session");

      List<Graph> singleGraph = new ArrayList<>();
      singleGraph.add(graph);
      Session newSession = graphSessionFactory.create(pId, pSessionName,
          singleGraph);
      newSession.layout();

      currentSessionHolder.addSession(newSession);
      logger.debug("Set session as actual model");
      currentSessionHolder.setCurrentSession(newSession);
    }
  }
}
