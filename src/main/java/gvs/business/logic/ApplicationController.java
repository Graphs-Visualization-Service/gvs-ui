package gvs.business.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.access.Persistor;
import gvs.business.logic.graph.GraphSessionController;
import gvs.business.logic.graph.GraphSessionControllerFactory;
import gvs.business.logic.tree.TreeSessionController;
import gvs.business.model.CurrentSessionHolder;
import gvs.business.model.graph.Graph;
import gvs.business.model.tree.Tree;
import gvs.interfaces.ISessionController;
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

  // TODO replace with enum? stable, random? check ui menu
  private boolean isSoftLayout;

  private final GraphSessionControllerFactory sessionControllerFactory;

  private final Collection<ISessionController> sessionControllers;
  private final Persistor persistor;
  private final CurrentSessionHolder currentSessionHolder;

  private static final Logger logger = LoggerFactory
      .getLogger(ApplicationController.class);

  /**
   * Constructor.
   * 
   * @param sessionHolder
   *          wrapper for the current session
   * @param persistor
   *          persistor
   */
  @Inject
  public ApplicationController(CurrentSessionHolder sessionHolder,
      Persistor persistor,
      GraphSessionControllerFactory sessionControllerFactory) {
    this.currentSessionHolder = sessionHolder;
    this.persistor = persistor;
    this.sessionControllerFactory = sessionControllerFactory;
    this.sessionControllers = new HashSet<>();
    this.isSoftLayout = false;
  }

  /**
   * Sets session chosen from drop down, informs model and updates view.
   * 
   * @param pSessionController
   *          sessionController
   */
  public void changeCurrentSession(ISessionController pSessionController) {
    currentSessionHolder.setCurrentSession(pSessionController);
  }

  /**
   * Returns available session for displaying in combobox.
   * 
   * @return sessionControllers
   */
  public Collection<ISessionController> getSessionContollers() {
    return sessionControllers;
  }

  /**
   * Loads a session from a specific file.
   * 
   * @param fileName
   *          fileName
   */
  public void loadStoredSession(String fileName) {
    logger.info("Load session from filesystem");
    ISessionController loadedSession = persistor.loadFile(fileName);

    currentSessionHolder.setCurrentSession(loadedSession);
    sessionControllers.add(loadedSession);
  }

  /**
   * Deletes a chosen session.
   * 
   * @param pSessionController
   *          SessionController
   */
  public void deleteSession(ISessionController pSessionController) {
    logger.info("Delete session");

    sessionControllers.remove(pSessionController);

    if (sessionControllers.size() > 0) {

      logger.debug("Session controller deleted. Set former graph session");
      currentSessionHolder
          .setCurrentSession(sessionControllers.iterator().next());
    } else {
      // when the last session is deleted, create empty dummy controller
      // otherwise session-bindings for UI would have to be unbound etc.
      logger.debug("Set empty graph session");
      currentSessionHolder
          .setCurrentSession(sessionControllerFactory.create(-1, "", null));
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
  public synchronized void addTreeModel(Tree pTreeModel, long pId,
      String pSessionName) {
    logger.info("New Tree arrived");
    try {
      LayoutMonitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Iterator<ISessionController> sessionIt = sessionControllers.iterator();
    boolean isSessionExisting = false;
    while (sessionIt.hasNext()) {
      ISessionController sc = (ISessionController) (sessionIt.next());
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
      sessionControllers.add(newSession);
      logger.debug("Set session as actual model");
      currentSessionHolder.setCurrentSession(newSession);
    }
    LayoutMonitor.getInstance().unlock();
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
  public synchronized void addModel(Graph graph, long pId,
      String pSessionName) {
    logger.info("New graph arrived");
    Iterator<ISessionController> sessionIt = sessionControllers.iterator();
    boolean isSessionExisting = false;
    while (sessionIt.hasNext()) {
      ISessionController sc = (ISessionController) (sessionIt.next());
      if (sc.getSessionId() == pId) {
        logger.debug("Add graph to exsting session");

        GraphSessionController graphSessionController = (GraphSessionController) sc;
        graphSessionController.addGraph(graph);
        graphSessionController.layout();

        isSessionExisting = true;
      }
    }
    if (!isSessionExisting) {
      logger.debug("Build new graph session");

      List<Graph> singleGraph = new ArrayList<>();
      singleGraph.add(graph);
      GraphSessionController newSession = sessionControllerFactory.create(pId,
          pSessionName, singleGraph);
      newSession.layout();

      sessionControllers.add(newSession);
      logger.debug("Set session as actual model");
      currentSessionHolder.setCurrentSession(newSession);
    }
  }

  /**
   * Sets layout option for layout engine. <br>
   * hard = false soft = true
   * 
   * @param layoutOption
   *          layoutOption
   */
  public void setIsSoftLayoutOption(boolean layoutOption) {
    this.isSoftLayout = layoutOption;
  }

  /**
   * Returns layout option defined by user.
   * 
   * @return layoutOption
   */
  public boolean isSoftLayout() {
    return isSoftLayout;
  }
}
