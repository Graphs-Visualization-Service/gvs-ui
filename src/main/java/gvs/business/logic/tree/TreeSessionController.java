package gvs.business.logic.tree;

import java.io.File;
import java.util.AbstractList;
import java.util.Observable;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gvs.access.Persistor;
import gvs.business.model.graph.Graph;
import gvs.business.model.tree.Tree;
import gvs.interfaces.ITreeSessionController;
import gvs.interfaces.VoidOperation;
import gvs.ui.model.tree.VisualizationTreeModel;
import gvs.ui.view.tree.VisualizationTreePanel;

/**
 * The session contoller reacts on user input events and implements most of the
 * visualization logic
 * 
 * @author aegli
 *
 */
public class TreeSessionController extends Observable
    implements ITreeSessionController {

  private VisualizationTreeModel visualModel = null;
  private VisualizationTreePanel visualPanel = null;
  private TreeLayoutController layoutController = null;
  private long clientSessionId = 0;
  private int serverSessionId = 1;
  private String sessionName = null;
  private Vector<Tree> treeModels = null;
  private Tree currentTreeModel = null;
  private TreeSessionReplay ta = null;
  private Timer replayTimer = null;

  private boolean replayMode = false;
  private int picsPersMinute = 1000;

  private Persistor persistor;

  private static final Logger logger = LoggerFactory
      .getLogger(TreeSessionController.class);

  /**
   * Builds default session controller.
   *
   * @param appController
   *          injected app controller
   * @param persistor
   *          injected persistor
   */
  @Inject
  public TreeSessionController(Persistor persistor) {
    this.persistor = persistor;
  }

  /**
   * Builds an instance of a tree session controller
   * 
   * @param pSessionId
   * @param pSessionName
   * @param pTreeModel
   */
  public TreeSessionController(long pSessionId, String pSessionName,
      Tree pTreeModel) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;

    initializeTreeSessionController();

    logger.info("Build new tree session controller");
    this.treeModels = new Vector<>();
    this.currentTreeModel = pTreeModel;

    currentTreeModel.setModelId(serverSessionId++);
    treeModels.add(currentTreeModel);

    if (currentTreeModel.getNodes().size() > 0
        && currentTreeModel.getNodes().size() != 0) {
      callLayouter();
    } else {
      setVisualModel();
    }
  }

  /**
   * Builds an instance of a loaded tree session from persistor
   * 
   * @param pSessionId
   * @param pSessionName
   * @param pTreeModels
   */
  public TreeSessionController(long pSessionId, String pSessionName,
      Vector<Tree> pTreeModels) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;
    this.treeModels = pTreeModels;

    initializeTreeSessionController();

    logger.info("Build new tree session controller from storage");
    currentTreeModel = (Tree) treeModels.lastElement();
    setVisualModel();
  }

  private void initializeTreeSessionController() {
    // TODO check logger replacement
    // this.treeContLogger =
    // gvs.common.Logger.getInstance().getTreeControllerLogger();
    this.visualModel = new VisualizationTreeModel();
    this.visualPanel = new VisualizationTreePanel(visualModel);
  }

  /**
   * Adds a new tree model to the current session
   */
  public void addTreeModel(Tree pTreeModel) {
    logger.info("New tree model arrived");
    currentTreeModel = pTreeModel;
    currentTreeModel.setModelId(serverSessionId++);
    treeModels.add(currentTreeModel);
    logger.debug("Check if current tree is empty");
    if ((currentTreeModel.getNodes()).size() != 0) {
      logger.debug("tree isn't empty");
      callLayouter();
    } else {
      logger.debug("tree is empty");
      setVisualModel();
    }
  }

  /**
   * Sets calculated model for drawing
   */
  public void setVisualModel() {
    // TODO: currently deactivates all GVS 1.0 GUI functionality
    // treeContLogger.debug("Display tree");
    // cp.setText(actualTreeModel.getModelId(), treeModels.size());
    // visualModel.setTreeModel(actualTreeModel, layoutController);
    // setButtonState(actualTreeModel.getModelId());
  }

  /**
   * Displays first model of session
   */
  public void getFirstModel() {
    logger.info("Show first tree of current session");
    int requestedModelId = ((Tree) treeModels.firstElement()).getModelId();
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (Tree) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays previous model of session
   */
  public void getPreviousModel() {
    logger.info("Show prevoius tree of current session");
    int requestedModelId = currentTreeModel.getModelId() - 1;
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (Tree) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays next model of session
   */
  public synchronized void getNextModel() {
    logger.info("Show next tree of current session");
    int requestedModelId = currentTreeModel.getModelId() + 1;
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (Tree) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays last model of session
   */
  public void getLastModel() {
    logger.info("Show last tree of current session");
    int requestedModelId = ((Tree) treeModels.lastElement()).getModelId();
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (Tree) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Shows a replay over all available models
   */
  public void replay(long speed, VoidOperation c) {
    logger.info("Show replay of tree session");
    if (!replayMode) {
      setEmptyButtonState();
      // TODO replace with GVS 2.0 pendant
      // cp.setReplay(true);
      this.setReplayMode(true);
      replayTimer = new Timer();
      ta = new TreeSessionReplay(treeModels, this);
      replayTimer.schedule(ta, picsPersMinute, picsPersMinute);
    } else {
      this.setReplayMode(false);
      this.validateNavigation(currentTreeModel.getModelId());
      replayTimer.cancel();
    }

  }

  /**
   * Validate if user intention is valid
   */
  public boolean validateNavigation(long pRequestedModelId) {
    if ((pRequestedModelId >= 1)
        && (pRequestedModelId <= (treeModels.size()))) {
      // deactivate GVS 1.0 GUI
      // setButtonState(pRequestedModelId);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns current tree model
   */
  public Tree getCurrentTreeModel() {
    return currentTreeModel;
  }

  /**
   * Marks model as currently used
   */
  public void setCurrentTreeModel(Tree pACurrentTreeModel) {
    this.currentTreeModel = pACurrentTreeModel;
  }

  /**
   * Returns current session name
   */
  public String getSessionName() {
    return sessionName;
  }

  /**
   * Returns actaul session id
   */
  public long getSessionId() {
    return clientSessionId;
  }

  /**
   * Set replay speed
   */
  public void speed(int pPicsPerSecond) {
    logger.debug("Changing replay speed");
    picsPersMinute = pPicsPerSecond;
  }

  /**
   * NOT USED IN TREE SESSION
   */
  public void autoLayout() {
  }

  /**
   * Set if replay is acive
   * 
   * @param pFinishReplay
   */
  public void setReplayMode(boolean pFinishReplay) {
    replayMode = pFinishReplay;
    if (replayMode) {
      // TODO replace with GVS 2.0
      // cp.setReplayText(" Stop ");
    } else {
      // TODO replace with GVS 2.0
      // cp.setReplayText("Replay");
    }
  }

  /**
   * Returns a list of available models.
   * 
   * @return treeModels
   */
  public AbstractList<Tree> getMyGraphModels() {
    return treeModels;
  }

  // Call layouter for layouting current model
  private void callLayouter() {
    logger.info("Layouting elements of tree");
    layoutController = new TreeLayoutController();
    layoutController.setElements(currentTreeModel);
    logger.info("Finished layouting tree, all positions are set");
    setVisualModel();
  }

  // Disable all button. Occurs when replay or layouting is active
  private void setEmptyButtonState() {
    // TODO: currently deactivates all GVS 1.0 GUI functionality
    // treeContLogger.debug("Disable all User-Interface components");
    // cp.setPrevious(false);
    // cp.setFirst(false);
    // cp.setNext(false);
    // cp.setLast(false);
    // cp.setReplay(false);
    // cp.setLayout(false);
    // cp.setSlider(false);
  }

  @Override
  public void saveSession(File file) {
    persistor.saveToDisk(this, file);
  }

  @Override
  public void changeCurrentGraphToNext() {
    int nextTreeId = currentTreeModel.getModelId() + 1;
    if (validateNavigation(nextTreeId)) {
      currentTreeModel = treeModels.get(nextTreeId - 1);
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToFirst() {
    currentTreeModel = treeModels.firstElement();
    notifyObservers();
  }

  @Override
  public void changeCurrentGraphToPrev() {
    int prevTreeId = currentTreeModel.getModelId() - 1;
    if (validateNavigation(prevTreeId)) {
      currentTreeModel = treeModels.get(prevTreeId - 1);
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToLast() {
    currentTreeModel = treeModels.lastElement();
    notifyObservers();
  }

  @Override
  public int getTotalGraphCount() {
    return treeModels.size();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + (int) (clientSessionId ^ (clientSessionId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TreeSessionController other = (TreeSessionController) obj;
    if (clientSessionId != other.clientSessionId) {
      return false;
    }
    return true;
  }

  @Override
  public Graph getCurrentGraph() {
    return null;
  }

}
