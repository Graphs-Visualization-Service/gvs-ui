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
  private Logger graphContLogger = null;
  private VisualizationGraphModel visualModel = null;
  private VisualizationGraphPanel visualPanel = null;
  private LayoutController layoutController = null;
  private GraphModel actualGraphModel = null;
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

    graphContLogger.info("Build empty graph session");
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

    graphContLogger.info("Build new graph session controller from storage");
    actualGraphModel = (GraphModel) graphModels.lastElement();
    currentGraphId = actualGraphModel.getModelId();

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
    graphContLogger.info("Build new graph session controller");
    actualGraphModel = pGraphModel;
    actualGraphModel.setModelId(serverSessionId++);
    currentGraphId = actualGraphModel.getModelId();
    graphModels.add(actualGraphModel);

    graphContLogger.debug("Check if graph model is empty");
    if ((actualGraphModel.getVertizes()).size() != 0) {
      if (((IVertex) actualGraphModel.getVertizes().firstElement())
          .isRelative()) {
        graphContLogger.debug("Graph is relative");
        isRelativeSession = true;
        validateNavigation(actualGraphModel.getModelId());
        isDraggable();
        setVisualModel();
        Monitor.getInstance().unlock();
      } else {
        graphContLogger.debug("Graph isn't relative");
        setVisualModel();
        callLayouter();
      }
    } else {

      graphContLogger.debug("Empty graph");
      validateNavigation(actualGraphModel.getModelId());
      setVisualModel();
      controlPanel.setLayout(false);
      Monitor.getInstance().unlock();
    }
  }

  /**
   * initialize graph session controller.
   */
  private void initializeGraphSessionController() {
    // TODO check Logger replacement
    this.graphContLogger = LoggerFactory
        .getLogger(GraphSessionController.class);
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
    graphContLogger.info("New graph arrived");
    try {
      Monitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    actualGraphModel = pGraphModel;
    actualGraphModel.setModelId(serverSessionId++);
    graphModels.add(actualGraphModel);

    graphContLogger.debug("Check if graph is empty");
    if ((actualGraphModel.getVertizes()).size() != 0) {

      if (((IVertex) actualGraphModel.getVertizes().firstElement())
          .isRelative()) {
        graphContLogger.debug("Graph is relative");
        isRelativeSession = true;
        validateNavigation(actualGraphModel.getModelId());
        isDraggable();
        setVisualModel();
        Monitor.getInstance().unlock();
      } else {
        setFormerVertexCoordinate();
      }
    } else {
      validateNavigation(actualGraphModel.getModelId());
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
      graphContLogger.info("Finish layouting graph, all positions are set");
      controlPanel.setLayoutState(false);
      autoLayoutingMode = false;

      visualModel.setLayouting(false);
      validateNavigation(actualGraphModel.getModelId());
      isDraggable();
      setVisualModel();

      setButtonState(actualGraphModel.getModelId());
      Monitor.getInstance().unlock();

    } else {
      graphContLogger.debug(
          "Continue layouting graph, vertizes without positions detected");
      visualModel.setLayouting(true);
      setVisualModel();
      controlPanel.setLayoutState(true);
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
      setButtonState(pRequestedModelId);
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
    if (actualGraphModel.getModelId() == graphModels.size()) {
      visualModel.setDragging(true);
      graphContLogger.debug("Last Graph in session queue, enable dragging");
      if (((IVertex) actualGraphModel.getVertizes().firstElement())
          .isRelative()) {
        visualModel.setDragging(false);
        graphContLogger.debug("Graph is relative, disable dragging");
      }
    } else {
      graphContLogger
          .debug("Graph isn't last element in queue, disable dragging");
      visualModel.setDragging(false);

    }
  }

  /**
   * Displays requested model.
   */
  public synchronized void getFirstModel() {
    graphContLogger.info("Show first graph of actual session");
    int requestedModelId = ((GraphModel) graphModels.firstElement())
        .getModelId();

    if (validateNavigation(requestedModelId)) {
      actualGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getPreviousModel() {
    graphContLogger.info("Show previous graph of actual session");
    int requestedModelId = actualGraphModel.getModelId() - 1;

    if (validateNavigation(requestedModelId)) {
      actualGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getNextModel() {
    graphContLogger.info("Show next graph of actual session");
    int requestedModelId = actualGraphModel.getModelId() + 1;

    if (validateNavigation(requestedModelId)) {
      actualGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getLastModel() {
    graphContLogger.info("Show last graph of actual session");
    int requestedModelId = ((GraphModel) graphModels.lastElement())
        .getModelId();

    if (validateNavigation(requestedModelId)) {
      actualGraphModel = (GraphModel) graphModels.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void replay() {
    graphContLogger.info("Show a replay of the session");
    if (!replayMode) {
      setEmptyButtonState();
      setReplayMode(true);
      controlPanel.setReplay(true);
      replayTimer = new Timer();
      sessionReplay = new GraphSessionReplay(graphModels, this);
      replayTimer.schedule(sessionReplay, picsPersMinute, picsPersMinute);
    } else {
      this.setReplayMode(false);
      this.validateNavigation(actualGraphModel.getModelId());
      replayTimer.cancel();
    }
  }

  /**
   * Returns current graph model.
   * 
   * @return actual graph model
   */
  public GraphModel getActualGraphModel() {
    return actualGraphModel;
  }

  /**
   * Sets current graph model.
   * 
   * @param actualGraphModel
   *          new graph model
   */
  public void setActualGraphModel(GraphModel actualGraphModel) {
    this.actualGraphModel = actualGraphModel;
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
    graphContLogger.debug("Changing replay speed");
    picsPersMinute = picsPerSecond;
  }

  /**
   * Layout current displayed graph.
   */
  public void autoLayout() {
    graphContLogger.debug("Check if graph can be layouted");
    if (!isRelativeSession) {
      if (actualGraphModel.getModelId() == graphModels.size()) {
        graphContLogger.debug("Graph is last element in Queue, call Layouter");
        Iterator<IVertex> it = actualGraphModel.getVertizes().iterator();
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
   * Copy positions of former vertizes to actual model in order of no changes.
   */
  private void setFormerVertexCoordinate() {
    GraphModel formerModel = (GraphModel) graphModels
        .elementAt(actualGraphModel.getModelId() - 2);
    Vector<IVertex> formerVertizes = formerModel.getVertizes();
    Vector<IVertex> actualVertizes = actualGraphModel.getVertizes();

    Iterator<IVertex> it1 = actualVertizes.iterator();
    boolean isFormerVertexPosAvailable = false;
    graphContLogger.debug("Setting former graph position to actual graph");
    while (it1.hasNext()) {
      IVertex actualVertex = (IVertex) it1.next();
      isFormerVertexPosAvailable = false;

      Iterator<IVertex> it = formerVertizes.iterator();
      while (it.hasNext()) {
        IVertex formerVertex = ((IVertex) it.next());
        if (actualVertex.getId() == formerVertex.getId()) {
          setActualVertexCoordinate(actualVertex, formerVertex);
          isFormerVertexPosAvailable = true;
        }
      }

      if (!isFormerVertexPosAvailable) {
        callLayoutEngine = true;
      }
    }

    if (callLayoutEngine) {
      graphContLogger
          .debug("Vertizes without positions detected, call layouter");
      callLayouter();
      callLayoutEngine = false;
    } else {
      graphContLogger.debug("All graph positions are set");
      validateNavigation(actualGraphModel.getModelId());
      isDraggable();
      setVisualModel();
      Monitor.getInstance().unlock();
    }
  }

  /**
   * Set calculated vertex coordinates.
   * 
   * @param pActualVertex
   *          actualVertex
   * @param pFormerVertex
   *          formerVetex
   */
  private void setActualVertexCoordinate(IVertex pActualVertex,
      IVertex pFormerVertex) {
    pActualVertex.setXPosition(pFormerVertex.getXPosition());
    pActualVertex.setYPosition(pFormerVertex.getYPosition());
    pActualVertex.setFixedPosition(pFormerVertex.isFixedPosition());
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
    graphContLogger.info("Layouting elements of graph");
    controlPanel.setLayoutState(true);
    setEmptyButtonState();
    layoutController = new LayoutController();
    layoutController.addObserver(this);
    layoutController.setElements(actualGraphModel.getVertizes(),
        actualGraphModel.getEdges(),
        ApplicationController.getInstance().getLayoutOption());

  }

  @Override
  public void saveSession() {
    persistor.saveToDisk(this);
  }

  @Override
  public void changeCurrentGraphToNext() {
    currentGraphId = actualGraphModel.getModelId() + 1;
    if (validateNavigation(currentGraphId)) {
      actualGraphModel = graphModels.get(currentGraphId - 1);
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToFirst() {
    actualGraphModel = graphModels.firstElement();
    currentGraphId = actualGraphModel.getModelId();
    notifyObservers();
  }

  @Override
  public void changeCurrentGraphToPrev() {
    currentGraphId = actualGraphModel.getModelId() - 1;
    if (validateNavigation(currentGraphId)) {
      actualGraphModel = (GraphModel) graphModels.get(currentGraphId - 1);
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToLast() {
    actualGraphModel = graphModels.lastElement();
    currentGraphId = actualGraphModel.getModelId();
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
