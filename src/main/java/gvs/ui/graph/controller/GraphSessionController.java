package gvs.ui.graph.controller;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gvs.common.Persistor;
import gvs.interfaces.IGraphSessionController;
import gvs.interfaces.IPersistor;
import gvs.interfaces.IVertex;
import gvs.server.ConnectionMonitor;
import gvs.ui.application.controller.ApplicationController;
import gvs.ui.application.controller.Monitor;
import gvs.ui.application.view.ApplicationView;
import gvs.ui.application.view.ControlPanel;
import gvs.ui.graph.layout.LayoutController;
import gvs.ui.graph.model.GraphModel;
import gvs.ui.graph.model.VisualizationGraphModel;
import gvs.ui.graph.view.VisualizationGraphPanel;

/**
 * The session contoller reacts on user input events and implements most of the
 * visualization logic.
 * 
 * @author aegli
 *
 */
public class GraphSessionController extends Observable
    implements Observer, IGraphSessionController {
  private static final Logger logger = LoggerFactory
      .getLogger(GraphSessionController.class);
  private VisualizationGraphModel visualModel = null;
  private VisualizationGraphPanel visualPanel = null;
  private LayoutController layoutController = null;
  private GraphModel currentGraphModel = null;
  private ControlPanel controlPanel = null;
  // TODO: choose better structure to save models: e.g. map, doubly linked list
  private Vector<GraphModel> graphModels = null;
  private Timer replayTimer = null;
  private GraphSessionReplay sessionReplay = null;

  private long clientSessionId = 0;
  private int serverSessionId = 1;
  private String sessionName = null;

  private static final int DEFAULT_PICS_PER_MINUTE = 1000;
  private int picsPersMinute = DEFAULT_PICS_PER_MINUTE;
  private boolean callLayoutEngine = false;
  private boolean replayMode = false;
  private boolean autoLayoutingMode = false;
  private boolean isRelativeSession = false;
  private int currentGraphId;

  // TODO: add inject
  private IPersistor persistor = new Persistor();

  /**
   * Builds default session controller.
   *
   */
  public GraphSessionController() {
    initializeGraphSessionController();
    graphModels = new Vector<>();

    logger.info("Build empty graph session");
    setEmptyButtonState();
  }

  /**
   * Builds session controller for loaded session (File).
   * 
   * @param pSessionId
   *          sessionId
   * @param pSessionName
   *          sessionName
   * @param pGraphModels
   *          graphModel
   */
  public GraphSessionController(long pSessionId, String pSessionName,
      Vector<GraphModel> pGraphModels) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;
    this.graphModels = pGraphModels;

    initializeGraphSessionController();

    logger.info("Build new graph session controller from storage");
    currentGraphModel = (GraphModel) graphModels.lastElement();
    currentGraphId = currentGraphModel.getModelId();

    isDraggable();
    setVisualModel();
  }

  /**
   * Builds session contoller and associated instances.
   * 
   * @param pSessionId
   *          sessionId
   * @param pSessionName
   *          sessionName
   * @param pGraphModel
   *          graphModel
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public GraphSessionController(long pSessionId, String pSessionName,
      GraphModel pGraphModel) {
    try {
      Monitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;

    initializeGraphSessionController();

    graphModels = new Vector();
    logger.info("Build new graph session controller");
    currentGraphModel = pGraphModel;
    currentGraphModel.setModelId(serverSessionId++);
    currentGraphId = currentGraphModel.getModelId();
    graphModels.add(currentGraphModel);

    logger.debug("Check if graph model is empty");
    if ((currentGraphModel.getVertizes()).size() != 0) {
      if (((IVertex) currentGraphModel.getVertizes().firstElement())
          .isRelative()) {
        logger.debug("Graph is relative");
        isRelativeSession = true;
        validateNavigation(currentGraphModel.getModelId());
        isDraggable();
        setVisualModel();
        Monitor.getInstance().unlock();
      } else {
        logger.debug("Graph isn't relative");
        setVisualModel();
        callLayouter();
      }
    } else {

      logger.debug("Empty graph");
      validateNavigation(currentGraphModel.getModelId());
      setVisualModel();
      controlPanel.setLayout(false);
      Monitor.getInstance().unlock();
    }
  }

  /**
   * initialize graph session controller.
   */
  private void initializeGraphSessionController() {
    visualModel = new VisualizationGraphModel();
    visualPanel = new VisualizationGraphPanel(visualModel);
    controlPanel = new ControlPanel(this);
    controlPanel.addVisualizationPanel(visualPanel);
  }

  /**
   * Adds a new graph session to an existing session.
   * 
   * @param pGraphModel
   *          graphModel
   */
  public void addGraphModel(GraphModel pGraphModel) {
    logger.info("New graph arrived");
    try {
      Monitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    currentGraphModel = pGraphModel;
    currentGraphModel.setModelId(serverSessionId++);
    graphModels.add(currentGraphModel);

    logger.debug("Check if graph is empty");
    if ((currentGraphModel.getVertizes()).size() != 0) {

      if (((IVertex) currentGraphModel.getVertizes().firstElement())
          .isRelative()) {
        logger.debug("Graph is relative");
        isRelativeSession = true;
        validateNavigation(currentGraphModel.getModelId());
        isDraggable();
        setVisualModel();
        Monitor.getInstance().unlock();
      } else {
        setFormerVertexCoordinate();
      }
    } else {
      validateNavigation(currentGraphModel.getModelId());
      setVisualModel();
      controlPanel.setLayout(false);
      ApplicationView.setButton(true);
      Monitor.getInstance().unlock();
    }
  }

  /**
   * Updates session contoller, used by layout engine to display calculated
   * models.
   * 
   * @param o
   *          Observable
   * @param arg
   *          Object
   */
  public void update(Observable o, Object arg) {
    String parameter = (String) arg;
    if (parameter.equals("TRUE")) {
      logger.info("Finish layouting graph, all positions are set");
      controlPanel.setLayoutState(false);
      autoLayoutingMode = false;

      visualModel.setLayouting(false);
      validateNavigation(currentGraphModel.getModelId());
      isDraggable();
      // deactivate GVS 1.0 GUI
      // setVisualModel();
      // setButtonState(actualGraphModel.getModelId());
      Monitor.getInstance().unlock();

    } else {
      logger.debug(
          "Continue layouting graph, vertizes without positions detected");
      visualModel.setLayouting(true);
      // deactivate GVS 1.0 GUI
      // setVisualModel();
      // controlPanel.setLayoutState(true);
    }

  }

  /**
   * Returns contol panel to view.
   * 
   * @return controlPanel
   */
  public synchronized ControlPanel getControlPanel() {
    return controlPanel;
  }

  /**
   * Sets calculated model to visualization panel.
   */
  public void setVisualModel() {
    // TODO: currently deactivates all GVS 1.0 GUI functionality
    // graphContLogger.debug("Display graph");
    // controlPanel.setText(actualGraphModel.getModelId(), graphModels.size());
    // visualModel.setGraphModel(actualGraphModel);
    // setButtonState(actualGraphModel.getModelId());
  }

  /**
   * Validates if user interactions are allowed.
   * 
   * @param pRequestedModelId
   *          requestModelId
   * @return boolean
   */
  public boolean validateNavigation(long pRequestedModelId) {
    if ((pRequestedModelId >= 1)
        && (pRequestedModelId <= (graphModels.size()))) {
      // deactivate GVS 1.0 GUI
      // setButtonState(pRequestedModelId);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Validats if dragging is allowed.
   *
   */
  public void isDraggable() {
    if (currentGraphModel.getModelId() == graphModels.size()) {
      visualModel.setDragging(true);
      logger.debug("Last Graph in session queue, enable dragging");
      if (((IVertex) currentGraphModel.getVertizes().firstElement())
          .isRelative()) {
        visualModel.setDragging(false);
        logger.debug("Graph is relative, disable dragging");
      }
    } else {
      logger.debug("Graph isn't last element in queue, disable dragging");
      visualModel.setDragging(false);

    }
  }

  /**
   * Displays requested model.
   */
  public synchronized void getFirstModel() {
    logger.info("Show first graph of current session");
    int requestedModelId = ((GraphModel) graphModels.firstElement())
        .getModelId();

    if (validateNavigation(requestedModelId)) {
      currentGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getPreviousModel() {
    logger.info("Show previous graph of current session");
    int requestedModelId = currentGraphModel.getModelId() - 1;

    if (validateNavigation(requestedModelId)) {
      currentGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getNextModel() {
    logger.info("Show next graph of current session");
    int requestedModelId = currentGraphModel.getModelId() + 1;

    if (validateNavigation(requestedModelId)) {
      currentGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getLastModel() {
    logger.info("Show last graph of current session");
    int requestedModelId = ((GraphModel) graphModels.lastElement())
        .getModelId();

    if (validateNavigation(requestedModelId)) {
      currentGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void replay() {
    logger.info("Show a replay of the session");
    if (!replayMode) {
      setEmptyButtonState();
      setReplayMode(true);
      controlPanel.setReplay(true);
      replayTimer = new Timer();
      sessionReplay = new GraphSessionReplay(graphModels, this);
      replayTimer.schedule(sessionReplay, picsPersMinute, picsPersMinute);
    } else {
      this.setReplayMode(false);
      this.validateNavigation(currentGraphModel.getModelId());
      replayTimer.cancel();
    }
  }

  /**
   * Returns current graph model.
   * 
   * @return current graph model
   */
  public GraphModel getCurrentGraphModel() {
    return currentGraphModel;
  }

  /**
   * Sets current graph model.
   * 
   * @param currentGraphModel
   *          new graph model
   */
  public void setCurrentGraphModel(GraphModel currentGraphModel) {
    this.currentGraphModel = currentGraphModel;
  }

  /**
   * Returns session name.
   * 
   * @return sessionName
   */
  public String getSessionName() {
    return sessionName;
  }

  /**
   * Sets replay speed.
   * 
   * @param picsPerSecond
   *          picsPerSecond
   */
  public void speed(int picsPerSecond) {
    logger.debug("Changing replay speed");
    picsPersMinute = picsPerSecond;
  }

  /**
   * Layout current displayed graph.
   */
  public void autoLayout() {
    logger.debug("Check if graph can be layouted");
    if (!isRelativeSession) {
      if (currentGraphModel.getModelId() == graphModels.size()) {
        logger.debug("Graph is last element in Queue, call Layouter");
        Iterator<IVertex> it = currentGraphModel.getVertizes().iterator();
        while (it.hasNext()) {
          ((IVertex) it.next()).setFixedPosition(false);
        }

        visualModel.setDragging(false);
        setEmptyButtonState();
        autoLayoutingMode = true;
        setVisualModel();
        callLayouter();
      }
    }
  }

  /**
   * Returns session id.
   * 
   * @return clientSessionId
   */
  public long getSessionId() {
    return clientSessionId;
  }

  /**
   * Returns list, used for saving option.
   * 
   * @return graphModels
   */
  public AbstractList<GraphModel> getMyGraphModels() {
    return graphModels;
  }

  /**
   * Sets whether replay is active.
   * 
   * @param pFinishReplay
   *          finishReplay
   */
  public void setReplayMode(boolean pFinishReplay) {
    replayMode = pFinishReplay;
    if (replayMode) {
      controlPanel.setReplayText("  Stop  ");
    } else {
      controlPanel.setReplayText("Replay");
    }
  }

  /**
   * Copy positions of former vertizes to current model in order of no changes.
   */
  private void setFormerVertexCoordinate() {
    GraphModel formerModel = (GraphModel) graphModels
        .elementAt(currentGraphModel.getModelId() - 2);
    Vector<IVertex> formerVertizes = formerModel.getVertizes();
    Vector<IVertex> currentVertizes = currentGraphModel.getVertizes();

    Iterator<IVertex> it1 = currentVertizes.iterator();
    boolean isFormerVertexPosAvailable = false;
    logger.debug("Setting former graph position to current graph");
    while (it1.hasNext()) {
      IVertex currentVertex = (IVertex) it1.next();
      isFormerVertexPosAvailable = false;

      Iterator<IVertex> it = formerVertizes.iterator();
      while (it.hasNext()) {
        IVertex formerVertex = ((IVertex) it.next());
        if (currentVertex.getId() == formerVertex.getId()) {
          setCurrentVertexCoordinate(currentVertex, formerVertex);
          isFormerVertexPosAvailable = true;
        }
      }

      if (!isFormerVertexPosAvailable) {
        callLayoutEngine = true;
      }
    }

    if (callLayoutEngine) {
      logger.debug("Vertizes without positions detected, call layouter");
      callLayouter();
      callLayoutEngine = false;
    } else {
      logger.debug("All graph positions are set");
      validateNavigation(currentGraphModel.getModelId());
      isDraggable();
      setVisualModel();
      Monitor.getInstance().unlock();
    }
  }

  /**
   * Set calculated vertex coordinates.
   * 
   * @param pCurrentVertex
   *          currentVertex
   * @param pFormerVertex
   *          formerVetex
   */
  private void setCurrentVertexCoordinate(IVertex pCurrentVertex,
      IVertex pFormerVertex) {
    pCurrentVertex.setXPosition(pFormerVertex.getXPosition());
    pCurrentVertex.setYPosition(pFormerVertex.getYPosition());
    pCurrentVertex.setFixedPosition(pFormerVertex.isFixedPosition());
  }

  /**
   * Set Button state in order of displayed model in queue.
   * 
   * @param pRequestedModelId
   *          requestModelId
   */
  private void setButtonState(long pRequestedModelId) {
    if (!replayMode && !(autoLayoutingMode)) {
      if (pRequestedModelId == graphModels.size()) {
        controlPanel.setNext(false);
        controlPanel.setLast(false);
        controlPanel.setLayout(true);
        if (graphModels.size() == 1) {

          controlPanel.setReplay(false);
          controlPanel.setSlider(false);
          controlPanel.setPrevious(false);
          controlPanel.setFirst(false);
          controlPanel.setNext(false);
          controlPanel.setLast(false);
        } else {

          controlPanel.setReplay(true);
          controlPanel.setSlider(true);
          controlPanel.setFirst(true);
          controlPanel.setPrevious(true);
          controlPanel.setNext(false);
          controlPanel.setLast(false);
        }
      } else if (pRequestedModelId == 1) {

        controlPanel.setPrevious(false);
        controlPanel.setFirst(false);
        if (graphModels.size() == 1) {
          controlPanel.setReplay(false);
          controlPanel.setSlider(false);
          controlPanel.setNext(false);
          controlPanel.setLast(false);
        } else {
          controlPanel.setLayout(false);
          controlPanel.setReplay(true);
          controlPanel.setSlider(true);
          controlPanel.setNext(true);
          controlPanel.setLast(true);
        }
      } else {
        controlPanel.setLayout(false);
        controlPanel.setReplay(true);
        controlPanel.setSlider(true);
        controlPanel.setFirst(true);
        controlPanel.setPrevious(true);
        controlPanel.setNext(true);
        controlPanel.setLast(true);
      }
      if (isRelativeSession) {
        controlPanel.setLayout(false);
        ApplicationView.setButton(true);
      }
    }
  }

  /**
   * Disable all buttons.
   */
  private void setEmptyButtonState() {
    // TODO: currently deactivates all GVS 1.0 GUI functionality
    // graphContLogger.debug("Disable all User-Interface components");
    // controlPanel.setLayout(false);
    // controlPanel.setPrevious(false);
    // controlPanel.setFirst(false);
    // controlPanel.setNext(false);
    // controlPanel.setLast(false);
    // controlPanel.setReplay(false);
    // controlPanel.setLayout(false);
    // controlPanel.setSlider(false);
  }

  /**
   * Call layout engine.
   */
  private void callLayouter() {
    logger.info("Layouting elements of graph");
    controlPanel.setLayoutState(true);
    setEmptyButtonState();
    layoutController = new LayoutController();
    layoutController.addObserver(this);
    layoutController.setElements(currentGraphModel.getVertizes(),
        currentGraphModel.getEdges(),
        ApplicationController.getInstance().getLayoutOption());

  }

  @Override
  public void saveSession() {
    persistor.saveToDisk(this);
  }

  @Override
  public void changeCurrentGraphToNext() {
    int nextGraphId = currentGraphModel.getModelId() + 1;
    if (validateNavigation(nextGraphId)) {
      currentGraphModel = graphModels.get(nextGraphId - 1);
      currentGraphId = nextGraphId;
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToFirst() {
    currentGraphModel = graphModels.firstElement();
    currentGraphId = currentGraphModel.getModelId();
    notifyObservers();
  }

  @Override
  public void changeCurrentGraphToPrev() {
    int prevGraphId = currentGraphModel.getModelId() - 1;
    if (validateNavigation(prevGraphId)) {
      currentGraphModel = (GraphModel) graphModels.get(prevGraphId - 1);
      currentGraphId = prevGraphId;
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToLast() {
    currentGraphModel = graphModels.lastElement();
    currentGraphId = currentGraphModel.getModelId();
    notifyObservers();
  }

  @Override
  public int getCurrentGraphId() {
    return currentGraphId;
  }

  @Override
  public int getTotalGraphCount() {
    return graphModels.size();
  }

}
