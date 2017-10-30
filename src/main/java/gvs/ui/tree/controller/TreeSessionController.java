package gvs.ui.tree.controller;

import java.util.AbstractList;
import java.util.Observable;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gvs.common.Persistor;
import gvs.interfaces.IPersistor;
import gvs.interfaces.ITreeSessionController;
import gvs.ui.application.view.ControlPanel;
import gvs.ui.tree.layout.TreeLayoutController;
import gvs.ui.tree.model.TreeModel;
import gvs.ui.tree.model.VisualizationTreeModel;
import gvs.ui.tree.view.VisualizationTreePanel;

/**
 * The session contoller reacts on user input events and implements most of the
 * visualization logic
 * 
 * @author aegli
 *
 */
public class TreeSessionController extends Observable
    implements ITreeSessionController {

  private Logger treeContLogger = null;
  private VisualizationTreeModel visualModel = null;
  private VisualizationTreePanel visualPanel = null;
  private TreeLayoutController layoutController = null;
  private ControlPanel cp = null;
  private long clientSessionId = 0;
  private int serverSessionId = 1;
  private String sessionName = null;
  private Vector<TreeModel> treeModels = null;
  private TreeModel currentTreeModel = null;
  private TreeSessionReplay ta = null;
  private Timer replayTimer = null;

  private boolean replayMode = false;
  private int picsPersMinute = 1000;
  private int currentTreeId;

  // TODO: change to inject
  private IPersistor persistor = new Persistor();

  /**
   * Builds an instance of a tree session controller
   * 
   * @param pSessionId
   * @param pSessionName
   * @param pTreeModel
   */
  public TreeSessionController(long pSessionId, String pSessionName,
      TreeModel pTreeModel) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;

    initializeTreeSessionController();

    treeContLogger.info("Build new tree session controller");
    this.treeModels = new Vector<>();
    this.currentTreeModel = pTreeModel;
    currentTreeId = currentTreeModel.getModelId();

    cp.addVisualizationPanel(visualPanel);
    currentTreeModel.setModelId(serverSessionId++);
    treeModels.add(currentTreeModel);

    if ((currentTreeModel.getNodes()).size() != 0) {
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
      Vector<TreeModel> pTreeModels) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;
    this.treeModels = pTreeModels;

    initializeTreeSessionController();

    treeContLogger.info("Build new tree session controller from storage");
    cp.addVisualizationPanel(visualPanel);
    currentTreeModel = (TreeModel) treeModels.lastElement();
    currentTreeId = currentTreeModel.getModelId();
    setVisualModel();
  }

  private void initializeTreeSessionController() {
    // TODO check logger replacement
    // this.treeContLogger =
    // gvs.common.Logger.getInstance().getTreeControllerLogger();
    treeContLogger = LoggerFactory.getLogger(TreeSessionController.class);
    this.visualModel = new VisualizationTreeModel();
    this.visualPanel = new VisualizationTreePanel(visualModel);
    this.cp = new ControlPanel(this);
  }

  /**
   * Adds a new tree model to the current session
   */
  public void addTreeModel(TreeModel pTreeModel) {
    treeContLogger.info("New tree model arrived");
    currentTreeModel = pTreeModel;
    currentTreeModel.setModelId(serverSessionId++);
    currentTreeId = currentTreeModel.getModelId();
    treeModels.add(currentTreeModel);
    treeContLogger.debug("Check if current tree is empty");
    if ((currentTreeModel.getNodes()).size() != 0) {
      treeContLogger.debug("tree isn't empty");
      callLayouter();
    } else {
      treeContLogger.debug("tree is empty");
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
    treeContLogger.info("Show first tree of current session");
    int requestedModelId = ((TreeModel) treeModels.firstElement()).getModelId();
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays previous model of session
   */
  public void getPreviousModel() {
    treeContLogger.info("Show prevoius tree of current session");
    int requestedModelId = currentTreeModel.getModelId() - 1;
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays next model of session
   */
  public synchronized void getNextModel() {
    treeContLogger.info("Show next tree of current session");
    int requestedModelId = currentTreeModel.getModelId() + 1;
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays last model of session
   */
  public void getLastModel() {
    treeContLogger.info("Show last tree of current session");
    int requestedModelId = ((TreeModel) treeModels.lastElement()).getModelId();
    if (validateNavigation(requestedModelId)) {
      currentTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Shows a replay over all available models
   */
  public void replay() {
    treeContLogger.info("Show replay of tree session");
    if (!replayMode) {
      setEmptyButtonState();
      cp.setReplay(true);
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
  public TreeModel getCurrentTreeModel() {
    return currentTreeModel;
  }

  /**
   * Marks model as currently used
   */
  public void setCurrentTreeModel(TreeModel pACurrentTreeModel) {
    this.currentTreeModel = pACurrentTreeModel;
  }

  /**
   * Returns control panel
   */
  public ControlPanel getControlPanel() {
    return cp;
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
    treeContLogger.debug("Changing replay speed");
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
      cp.setReplayText("  Stop  ");
    } else {
      cp.setReplayText("Replay");
    }
  }

  /**
   * Returns a list of available models.
   * 
   * @return treeModels
   */
  public AbstractList<TreeModel> getMyGraphModels() {
    return treeModels;
  }

  // Call layouter for layouting current model
  private void callLayouter() {
    treeContLogger.info("Layouting elements of tree");
    layoutController = new TreeLayoutController();
    layoutController.setElements(currentTreeModel);
    treeContLogger.info("Finished layouting tree, all positions are set");
    setVisualModel();
  }

  // Set button state
  private void setButtonState(long pRequestedModelId) {
    cp.setLayoutState(false);
    cp.setLayout(false);
    if (!replayMode) {
      if (pRequestedModelId == treeModels.size()) {
        cp.setNext(false);
        cp.setLast(false);
        if (treeModels.size() == 1) {
          cp.setReplay(false);
          cp.setSlider(false);
          cp.setPrevious(false);
          cp.setFirst(false);
          cp.setNext(false);
          cp.setLast(false);
        } else {
          cp.setReplay(true);
          cp.setSlider(true);
          cp.setFirst(true);
          cp.setPrevious(true);
          cp.setNext(false);
          cp.setLast(false);
        }
      } else if (pRequestedModelId == 1) {
        cp.setPrevious(false);
        cp.setFirst(false);
        if (treeModels.size() == 1) {
          cp.setReplay(false);
          cp.setSlider(false);
          cp.setNext(false);
          cp.setLast(false);
        } else {
          cp.setReplay(true);
          cp.setSlider(true);
          cp.setNext(true);
          cp.setLast(true);
        }
      } else {
        cp.setReplay(true);
        cp.setSlider(true);
        cp.setFirst(true);
        cp.setPrevious(true);
        cp.setNext(true);
        cp.setLast(true);
      }
    }
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
  public void saveSession() {
    persistor.saveToDisk(this);
  }

  @Override
  public void changeCurrentGraphToNext() {
    int nextTreeId = currentTreeModel.getModelId() + 1;
    if (validateNavigation(nextTreeId)) {
      currentTreeModel = treeModels.get(nextTreeId - 1);
      currentTreeId = nextTreeId;
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToFirst() {
    currentTreeModel = treeModels.firstElement();
    currentTreeId = currentTreeModel.getModelId();
    notifyObservers();
  }

  @Override
  public void changeCurrentGraphToPrev() {
    int prevTreeId = currentTreeModel.getModelId() - 1;
    if (validateNavigation(prevTreeId)) {
      currentTreeModel = treeModels.get(prevTreeId - 1);
      currentTreeId = prevTreeId;
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToLast() {
    currentTreeModel = treeModels.lastElement();
    currentTreeId = currentTreeModel.getModelId();
    notifyObservers();
  }

  @Override
  public int getCurrentGraphId() {
    return currentTreeId;
  }

  @Override
  public int getTotalGraphCount() {
    return treeModels.size();
  }

}
