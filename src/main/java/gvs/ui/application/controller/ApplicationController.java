package gvs.ui.application.controller;

import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.common.Persistor;
import gvs.interfaces.ISessionController;
import gvs.interfaces.ITreeSessionController;
import gvs.ui.application.model.ApplicationModel;
import gvs.ui.graph.controller.GraphSessionController;
import gvs.ui.graph.model.GraphModel;
import gvs.ui.tree.controller.TreeSessionController;
import gvs.ui.tree.model.TreeModel;

/**
 * The Application Controller reacts to events from the user or newly reveived
 * data. The requested operations will be executed.
 * 
 * @author aegli
 *
 */
public class ApplicationController {
  private Logger appContLogger = null;
  private Vector<ISessionController> sessionControllers = null;
  private static ApplicationController appController = null;
  private ApplicationModel applicationModel = null;
  private boolean layoutOption = false;

  private ISessionController duplicatedSessions = null;
  private ISessionController deletingSession = null;
  private ISessionController defaultGraphSession = null;

  /**
   * Constructor.
   * 
   * @param am
   *          ApplicationModel
   */
  private ApplicationController(ApplicationModel am) {
    this.applicationModel = am;
    this.sessionControllers = new Vector<ISessionController>();
    // TODO check logger replacement
    // this.appContLogger =
    // gvs.common.Logger.getInstance().getApplciationControllerLogger();
    this.appContLogger = LoggerFactory.getLogger(ApplicationController.class);
  }

  /**
   * Get Controller for model.
   * 
   * @param pApplicationModel
   *          model
   * @return ApplicationController singleton
   */
  public static synchronized ApplicationController getInstance(
      ApplicationModel pApplicationModel) {
    if (appController == null) {
      appController = new ApplicationController(pApplicationModel);
    }
    return appController;
  }

  /**
   * Returns an instance of the application controller.
   * 
   * @return applicationController
   */
  public static synchronized ApplicationController getInstance() {
    return appController;
  }

  /**
   * Sets layout option for layout engine: hard or soft layout.
   * 
   * @param pLayoutOption
   *          layoutOption
   */
  public void setLayoutOption(boolean pLayoutOption) {
    this.layoutOption = pLayoutOption;
  }

  /**
   * Returns layout option defined by user.
   * 
   * @return layoutOption
   */
  public boolean getLayoutOption() {
    return layoutOption;
  }

  /**
   * Sets session chosen from combobox, informs model and updates view.
   * 
   * @param pSessionController
   *          sessionController
   */
  public void setComboSession(ISessionController pSessionController) {
    applicationModel.setSession(pSessionController);
  }

  /**
   * Returns available session for displaying in combobox.
   * 
   * @return sessionController
   */
  @SuppressWarnings("rawtypes")
  public Vector getSessionContoller() {
    return sessionControllers;
  }

  /**
   * Loads a session from a specific file.
   * 
   * @param fileName
   *          fileName
   * @param persistor
   *          persistor
   */
  public void setRequestedFile(String fileName, Persistor persistor) {
    appContLogger.info("Load session from directory");
    ISessionController loadedSession = (ISessionController) persistor
        .loadFile(fileName);
    long loadedSessionId = loadedSession.getSessionId();
    ISessionController tempSession = null;

    Iterator<ISessionController> it = sessionControllers.iterator();
    while (it.hasNext()) {
      tempSession = ((ISessionController) it.next());

      if (tempSession.getSessionId() == loadedSessionId) {
        duplicatedSessions = tempSession;

      }
    }

    if (duplicatedSessions != null) {
      appContLogger.warn("Duplicated session detected while loading. "
          + "Delete duplicated session");
      sessionControllers.remove(duplicatedSessions);
      duplicatedSessions = null;
    }

    this.sessionControllers.add(loadedSession);
    applicationModel
        .setSession(((ISessionController) sessionControllers.lastElement()));
  }

  /**
   * Deletes a chosen session.
   * 
   * @param pSessionController
   *          SessionController
   */
  public void deleteSession(ISessionController pSessionController) {
    appContLogger.info("Deleting graph session");
    long deleteSessionId = pSessionController.getSessionId();
    ISessionController temp = null;

    Iterator<ISessionController> it = sessionControllers.iterator();
    while (it.hasNext()) {
      temp = ((ISessionController) it.next());

      if (temp.getSessionId() == deleteSessionId) {
        deletingSession = temp;
      }
    }

    sessionControllers.remove(deletingSession);
    if (sessionControllers.size() > 0) {
      appContLogger.debug("Set former graph session");
      applicationModel
          .setSession(((ISessionController) sessionControllers.firstElement()));

    } else {
      appContLogger.debug("Set empty graph session");
      defaultGraphSession = new GraphSessionController();
      applicationModel.setSession(defaultGraphSession);
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
  public synchronized void addTreeModel(TreeModel pTreeModel, long pId,
      String pSessionName) {
    appContLogger.info("New Tree arrived");
    try {
      Monitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Iterator<ISessionController> sessionIt = sessionControllers.iterator();
    boolean isSessionExisting = false;
    while (sessionIt.hasNext()) {
      ISessionController sc = (ISessionController) (sessionIt.next());
      if (sc.getSessionId() == pId) {
        appContLogger.debug("Add tree to exsting session");
        ((ITreeSessionController) sc).addTreeModel(pTreeModel);
        isSessionExisting = true;
      }
    }
    if (!isSessionExisting) {
      appContLogger.debug("Build new tree session");
      ITreeSessionController newSession = new TreeSessionController(pId,
          pSessionName, pTreeModel);
      sessionControllers.add(newSession);
      appContLogger.debug("Set session as actual model");
      applicationModel.setSession(newSession);
    }
    Monitor.getInstance().unlock();
  }

  /**
   * Adds a new graph model, if an associated session exists, adds model to
   * session. Otherwise, creates a new graph session
   * 
   * @param pGraphModel
   *          graphModel
   * @param pId
   *          Id
   * @param pSessionName
   *          sessionName
   */
  public synchronized void addModel(GraphModel pGraphModel, long pId,
      String pSessionName) {
    appContLogger.info("New graph arrived");
    Iterator<ISessionController> sessionIt = sessionControllers.iterator();
    boolean isSessionExisting = false;
    while (sessionIt.hasNext()) {
      ISessionController sc = (ISessionController) (sessionIt.next());
      if (sc.getSessionId() == pId) {
        appContLogger.debug("Add graph to exsting session");
        ((GraphSessionController) sc).addGraphModel(pGraphModel);

        isSessionExisting = true;
      }
    }
    if (!isSessionExisting) {
      appContLogger.debug("Build new graph session");
      GraphSessionController newSession = new GraphSessionController(pId,
          pSessionName, pGraphModel);
      sessionControllers.add(newSession);
      appContLogger.debug("Set session as actual model");
      applicationModel.setSession(newSession);
    }
  }
}
