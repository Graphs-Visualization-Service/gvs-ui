package gvs.business.logic.graph;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gvs.access.Persistor;
import gvs.business.logic.ApplicationController;
import gvs.business.logic.LayoutController;
import gvs.business.logic.Monitor;
import gvs.business.model.graph.Graph;
import gvs.interfaces.IGraphSessionController;
import gvs.interfaces.IVertex;
import gvs.ui.model.graph.VisualizationGraphModel;
import gvs.ui.model.graph.VisualizationGraphPanel;

/**
 * The session contoller reacts on user input events and implements most of the
 * visualization logic.
 * 
 * @author aegli
 *
 */
public class GraphSessionController extends Observable
    implements Observer, IGraphSessionController {

  private VisualizationGraphModel visualModel = null;
  private VisualizationGraphPanel visualPanel = null;
  private LayoutController layoutController = null;
  private Graph currentGraph = null;

  // TODO: choose better structure to save models: e.g. map, doubly linked list
  private List<Graph> graphs = null;
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

  private ApplicationController applicationController;
  private Persistor persistor;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphSessionController.class);

  /**
   * Builds default session controller.
   *
   * @param appController
   *          injected app controller
   * @param persistor
   *          injected persistor
   */
  @Inject
  public GraphSessionController(ApplicationController appController,
      Persistor persistor) {
    this.applicationController = appController;
    this.persistor = persistor;
    graphs = new Vector<>();

    initializeGraphSessionController();

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
      Vector<Graph> pGraphModels) {
    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;
    this.graphs = pGraphModels;

    initializeGraphSessionController();

    logger.info("Build new graph session controller from storage");
    currentGraph = graphs.get(graphs.size() - 1);
    currentGraphId = currentGraph.getId();

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
   * @param graph
   *          graphModel
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public GraphSessionController(long pSessionId, String pSessionName,
      Graph graph) {
    try {
      Monitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    this.clientSessionId = pSessionId;
    this.sessionName = pSessionName;

    initializeGraphSessionController();

    graphs = new Vector();
    logger.info("Build new graph session controller");
    currentGraph = graph;
    currentGraph.setId(serverSessionId++);
    currentGraphId = currentGraph.getId();
    graphs.add(currentGraph);

    logger.debug("Check if graph model is empty");
    if (currentGraph.getVertices().size() > 0) {

      // TODO check if first is relative (might be wrong. mwieland)
      if (currentGraph.getVertices().iterator().next().isRelative()) {
        logger.debug("Graph is relative");
        isRelativeSession = true;
        validateNavigation(currentGraph.getId());
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
      validateNavigation(currentGraph.getId());
      setVisualModel();

      // TODO replace with view model pendant
      // controlPanel.setLayout(false);
      Monitor.getInstance().unlock();
    }
  }

  /**
   * initialize graph session controller.
   */
  private void initializeGraphSessionController() {
    visualModel = new VisualizationGraphModel();
    visualPanel = new VisualizationGraphPanel(visualModel);
  }

  /**
   * Adds a new graph session to an existing session.
   * 
   * @param pGraphModel
   *          graphModel
   */
  @Override
  public void addGraph(Graph pGraphModel) {
    logger.info("New graph arrived");
    try {
      Monitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    currentGraph = pGraphModel;
    currentGraph.setId(serverSessionId++);
    graphs.add(currentGraph);

    logger.debug("Check if graph is empty");
    if ((currentGraph.getVertices()).size() != 0) {

      // check if first element. might be wrong (mwieand)
      if (currentGraph.getVertices().iterator().next().isRelative()) {
        logger.debug("Graph is relative");
        isRelativeSession = true;
        validateNavigation(currentGraph.getId());
        isDraggable();
        setVisualModel();
        Monitor.getInstance().unlock();
      } else {
        setFormerVertexCoordinate();
      }
    } else {
      validateNavigation(currentGraph.getId());
      setVisualModel();
      // TODO replace with view model pendant
      // controlPanel.setLayout(false);
      // ApplicationView.setButton(true);
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
      // TODO replace with view model pendant
      // controlPanel.setLayoutState(false);
      autoLayoutingMode = false;

      visualModel.setLayouting(false);
      validateNavigation(currentGraph.getId());
      isDraggable();
      // deactivate GVS 1.0 GUI
      // setVisualModel();
      // setButtonState(actualGraphModel.getId());
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
   * Sets calculated model to visualization panel.
   */
  public void setVisualModel() {
    // TODO: currently deactivates all GVS 1.0 GUI functionality
    // graphContLogger.debug("Display graph");
    // controlPanel.setText(actualGraphModel.getId(), graphModels.size());
    // visualModel.setGraphModel(actualGraphModel);
    // setButtonState(actualGraphModel.getId());
  }

  /**
   * Validates if user interactions are allowed.
   * 
   * @param pRequestedModelId
   *          requestModelId
   * @return boolean
   */
  public boolean validateNavigation(long pRequestedModelId) {
    if ((pRequestedModelId >= 1) && (pRequestedModelId <= (graphs.size()))) {
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
    if (currentGraph.getId() == graphs.size()) {
      visualModel.setDragging(true);
      logger.debug("Last Graph in session queue, enable dragging");

      // check if first element. might be wrong (mwieland)
      if (currentGraph.getVertices().iterator().next().isRelative()) {
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
    int graphId = graphs.get(0).getId();

    if (validateNavigation(graphId)) {
      currentGraph = graphs.get(graphId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getPreviousModel() {
    logger.info("Show previous graph of current session");
    int requestedModelId = currentGraph.getId() - 1;

    if (validateNavigation(requestedModelId)) {
      currentGraph = (Graph) graphs.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getNextModel() {
    logger.info("Show next graph of current session");
    int requestedModelId = currentGraph.getId() + 1;

    if (validateNavigation(requestedModelId)) {
      currentGraph = (Graph) graphs.get(requestedModelId - 1);
      isDraggable();
      setVisualModel();
    }
  }

  /**
   * Displays requested model.
   */
  public void getLastModel() {
    logger.info("Show last graph of current session");
    int requestedModelId = graphs.get(graphs.size() - 1).getId();

    if (validateNavigation(requestedModelId)) {
      currentGraph = (Graph) graphs.get(requestedModelId - 1);
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
      // TODO replace with view model pendant
      // controlPanel.setReplay(true);
      replayTimer = new Timer();
      sessionReplay = new GraphSessionReplay(graphs, this);
      replayTimer.schedule(sessionReplay, picsPersMinute, picsPersMinute);
    } else {
      this.setReplayMode(false);
      this.validateNavigation(currentGraph.getId());
      replayTimer.cancel();
    }
  }

  /**
   * Returns current graph model.
   * 
   * @return current graph model
   */
  @Override
  public Graph getCurrentGraph() {
    return currentGraph;
  }

  /**
   * Sets current graph model.
   * 
   * @param currentGraph
   *          new graph model
   */
  public void setCurrentGraph(Graph currentGraph) {
    this.currentGraph = currentGraph;
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
      if (currentGraph.getId() == graphs.size()) {
        logger.debug("Graph is last element in Queue, call Layouter");
        currentGraph.getVertices().forEach(v -> {
          v.setFixedPosition(false);
        });
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
  public List<Graph> getGraphs() {
    return graphs;
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
      // TODO replace with view model pendant
      // controlPanel.setReplayText(" Stop ");
    } else {
      // TODO replace with view model pendant
      // controlPanel.setReplayText("Replay");
    }
  }

  /**
   * Copy positions of former vertizes to current model in order of no changes.
   */
  private void setFormerVertexCoordinate() {
    Graph formerGraph = graphs.get(currentGraph.getId() - 2);
    Collection<IVertex> formerVertizes = formerGraph.getVertices();
    Collection<IVertex> currentVertizes = currentGraph.getVertices();

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
      validateNavigation(currentGraph.getId());
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
    // TODO currently deactivates all GVS 1.0 GUI functionality
    // controlPanel.setLayoutState(true);
    setEmptyButtonState();
    layoutController = new LayoutController();
    layoutController.addObserver(this);
    layoutController.setElements(currentGraph.getVertices(),
        currentGraph.getEdges(), applicationController.isSoftLayout());
  }

  @Override
  public void saveSession(File file) {
    persistor.saveToDisk(this, file);
  }

  @Override
  public void changeCurrentGraphToNext() {
    int nextGraphId = currentGraph.getId() + 1;
    if (validateNavigation(nextGraphId)) {
      currentGraph = graphs.get(nextGraphId - 1);
      currentGraphId = nextGraphId;
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToFirst() {
    currentGraph = graphs.get(0);
    currentGraphId = currentGraph.getId();
    notifyObservers();
  }

  @Override
  public void changeCurrentGraphToPrev() {
    int prevGraphId = currentGraph.getId() - 1;
    if (validateNavigation(prevGraphId)) {
      currentGraph = (Graph) graphs.get(prevGraphId - 1);
      currentGraphId = prevGraphId;
      notifyObservers();
    }
  }

  @Override
  public void changeCurrentGraphToLast() {
    currentGraph = graphs.get(graphs.size() - 1);
    currentGraphId = currentGraph.getId();
    notifyObservers();
  }

  @Override
  public int getCurrentGraphId() {
    return currentGraphId;
  }

  @Override
  public int getTotalGraphCount() {
    return graphs.size();
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
    GraphSessionController other = (GraphSessionController) obj;
    if (clientSessionId != other.clientSessionId) {
      return false;
    }
    return true;
  }

}
