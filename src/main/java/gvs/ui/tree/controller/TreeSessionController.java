package gvs.ui.tree.controller;

import java.util.AbstractList;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class TreeSessionController implements ITreeSessionController {

  private Logger treeContLogger = null;
  private VisualizationTreeModel visualModel = null;
  private VisualizationTreePanel visualPanel = null;
  private TreeLayoutController layoutController = null;
  private ControlPanel cp = null;
  private long clientSessionId = 0;
  private int serverSessionId = 1;
  private String sessionName = null;
  private Vector treeModels = null;
  private TreeModel actualTreeModel = null;
  private TreeSessionReplay ta = null;
  private Timer replayTimer = null;

  private boolean replayMode = false;
  private int picsPersMinute = 1000;

  /**
   * Builds an instance of a tree session controller
   * 
   * @param pSessionId
   * @param pSessionName
   * @param pTreeModel
   */
  @SuppressWarnings("unchecked")
  public TreeSessionController(long pSessionId, String pSessionName,
      TreeModel pTreeModel) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;

    initializeTreeSessionController();

    treeContLogger.info("Build new tree session controller");
    this.treeModels = new Vector();
    this.actualTreeModel = pTreeModel;

    cp.addVisualizationPanel(visualPanel);
    actualTreeModel.setModelId(serverSessionId++);
    treeModels.add(actualTreeModel);

    if ((actualTreeModel.getNodes()).size() != 0) {
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
      Vector pTreeModels) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;
    this.treeModels = pTreeModels;

    initializeTreeSessionController();

    treeContLogger.info("Build new tree session controller from storage");
    cp.addVisualizationPanel(visualPanel);
    actualTreeModel = (TreeModel) treeModels.lastElement();

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
   * Adds a new tree model to the actual session
   */
  @SuppressWarnings("unchecked")
  public void addTreeModel(TreeModel pTreeModel) {
    treeContLogger.info("New tree model arrived");
    actualTreeModel = pTreeModel;
    actualTreeModel.setModelId(serverSessionId++);
    treeModels.add(actualTreeModel);
    treeContLogger.debug("Check if actual tree is empty");
    if ((actualTreeModel.getNodes()).size() != 0) {
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
    treeContLogger.debug("Display tree");
    cp.setText(actualTreeModel.getModelId(), treeModels.size());
    visualModel.setTreeModel(actualTreeModel, layoutController);
    setButtonState(actualTreeModel.getModelId());
  }

  /**
   * Displays first model of session
   */
  public void getFirstModel() {
    treeContLogger.info("Show first tree of actual session");
    int requestedModelId = ((TreeModel) treeModels.firstElement()).getModelId();
    if (validateNavigation(requestedModelId)) {
      actualTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays previous model of session
   */
  public void getPreviousModel() {
    treeContLogger.info("Show prevoius tree of actual session");
    int requestedModelId = actualTreeModel.getModelId() - 1;
    if (validateNavigation(requestedModelId)) {
      actualTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays next model of session
   */
  public synchronized void getNextModel() {
    treeContLogger.info("Show next tree of actual session");
    int requestedModelId = actualTreeModel.getModelId() + 1;
    if (validateNavigation(requestedModelId)) {
      actualTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
      setVisualModel();
    }
  }

  /**
   * Displays last model of session
   */
  public void getLastModel() {
    treeContLogger.info("Show last tree of actual session");
    int requestedModelId = ((TreeModel) treeModels.lastElement()).getModelId();
    if (validateNavigation(requestedModelId)) {
      actualTreeModel = (TreeModel) treeModels.get(requestedModelId - 1);
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
      this.validateNavigation(actualTreeModel.getModelId());
      replayTimer.cancel();
    }

  }

  /**
   * Validate if user intention is valid
   */
  public boolean validateNavigation(long pRequestedModelId) {
    if ((pRequestedModelId >= 1)
        && (pRequestedModelId <= (treeModels.size()))) {
      setButtonState(pRequestedModelId);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns actual tree model
   */
  public TreeModel getActualTreeModel() {
    return actualTreeModel;
  }

  /**
   * Marks model as actually used
   */
  public void setActualTreeModel(TreeModel pActualTreeModel) {
    this.actualTreeModel = pActualTreeModel;
  }

  /**
   * Returns control panel
   */
  public ControlPanel getControlPanel() {
    return cp;
  }

  /**
   * Returns actual session name
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
  @SuppressWarnings("rawtypes")
  public AbstractList getMyGraphModels() {
    return treeModels;
  }

  // Call layouter for layouting actual model
  private void callLayouter() {
    treeContLogger.info("Layouting elements of tree");
    layoutController = new TreeLayoutController();
    layoutController.setElements(actualTreeModel);
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
    treeContLogger.debug("Disable all User-Interface components");
    cp.setPrevious(false);
    cp.setFirst(false);
    cp.setNext(false);
    cp.setLast(false);
    cp.setReplay(false);
    cp.setLayout(false);
    cp.setSlider(false);
  }

}
