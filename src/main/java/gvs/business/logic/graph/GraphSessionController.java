package gvs.business.logic.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.access.Persistor;
import gvs.business.logic.ApplicationController;
import gvs.business.logic.LayoutController;
import gvs.business.logic.LayoutMonitor;
import gvs.business.model.graph.CurrentGraphHolder;
import gvs.business.model.graph.Graph;
import gvs.interfaces.IGraphSessionController;
import gvs.interfaces.IVertex;

/**
 * The session contoller reacts on user input events and implements most of the
 * visualization logic. It observes the LayoutController.
 * 
 * @author aegli
 *
 */
public class GraphSessionController
    implements Observer, IGraphSessionController {

  private boolean callLayoutEngine = false;
  private boolean replayMode = false;
  private boolean autoLayoutingMode = false;
  private boolean isRelativeSession = false;

  private long sessionId;
  private String sessionName;
  private List<Graph> graphs;

  private final GraphSessionReplayFactory sessionReplayFactory;
  private final CurrentGraphHolder graphHolder;
  private final LayoutController layoutController;
  private final ApplicationController applicationController;
  private final Persistor persistor;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphSessionController.class);

  @Inject
  public GraphSessionController(ApplicationController appController,
      CurrentGraphHolder graphHolder, Persistor persistor,
      LayoutController layoutController,
      GraphSessionReplayFactory replayFactory, @Assisted long pSessionId,
      @Assisted String pSessionName, @Assisted @Nullable List<Graph> graphs) {

    this.sessionReplayFactory = replayFactory;
    this.graphHolder = graphHolder;
    this.applicationController = appController;
    this.persistor = persistor;
    this.layoutController = layoutController;

    this.graphs = graphs;
    this.sessionId = pSessionId;
    this.sessionName = pSessionName;

    // TODO is this really required?
    int graphId = 1;
    if (graphs != null) {
      if (graphs.size() >= 1) {
        for (Graph g : graphs) {
          if (g.getId() == 0) {
            g.setId(++graphId);
          }
        }
        graphHolder.setCurrentGraph(graphs.get(graphs.size() - 1));
      }
    } else {
      logger.info("Build empty graph session");
      this.graphs = new ArrayList<>();
    }
  }

  /**
   * Adds a new graph session to an existing session.
   * 
   * @param pGraphModel
   *          graphModel
   */
  @Override
  public void addGraph(Graph graph) {
    logger.info("New graph arrived");

    if (graphs.size() == 0) {
      graph.setId(1);
    } else {
      Graph previousGraph = graphs.get(graphs.size() - 1);
      graph.setId(previousGraph.getId() + 1);
    }

    graphHolder.setCurrentGraph(graph);
    graphs.add(graph);
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

      // visualModel.setLayouting(false);
      // deactivate GVS 1.0 GUI
      // setVisualModel();
      // setButtonState(actualGraphModel.getId());
      LayoutMonitor.getInstance().unlock();

    } else {
      logger.debug(
          "Continue layouting graph, vertizes without positions detected");
      // visualModel.setLayouting(true);
      // deactivate GVS 1.0 GUI
      // setVisualModel();
      // controlPanel.setLayoutState(true);
    }

  }

  /**
   * Displays requested model.
   */
  public synchronized void getFirstModel() {
    logger.info("Show first graph of current session");
    int graphId = graphs.get(0).getId();

    graphHolder.setCurrentGraph(graphs.get(graphId - 1));
  }

  /**
   * Displays requested model.
   */
  public void getPreviousModel() {
    logger.info("Show previous graph of current session");
    int requestedModelId = graphHolder.getCurrentGraph().getId() - 1;

    graphHolder.setCurrentGraph(graphs.get(requestedModelId - 1));
  }

  /**
   * Displays requested model.
   */
  public void getNextModel() {
    logger.info("Show next graph of current session");
    int requestedModelId = graphHolder.getCurrentGraph().getId() + 1;
    graphHolder.setCurrentGraph(graphs.get(requestedModelId - 1));
  }

  /**
   * Displays requested model.
   */
  public void getLastModel() {
    logger.info("Show last graph of current session");
    int requestedModelId = graphs.get(graphs.size() - 1).getId();
    graphHolder.setCurrentGraph(graphs.get(requestedModelId - 1));
  }

  /**
   * Displays requested model.
   */
  public void replay(long timeout) {
    logger.info("Replay current session");
    Timer timer = new Timer();
    if (!replayMode) {
      setReplayMode(true);
      // TODO replace with view model pendant
      // controlPanel.setReplay(true);
      GraphSessionReplay sessionReplay = sessionReplayFactory.create(this,
          graphs);
      timer.schedule(sessionReplay, timeout, timeout);
    } else {
      this.setReplayMode(false);
      timer.cancel();
    }
  }

  /**
   * Returns current graph model.
   * 
   * @return current graph model
   */
  @Override
  public Graph getCurrentGraph() {
    return graphHolder.getCurrentGraph();
  }

  /**
   * Sets current graph model.
   * 
   * @param currentGraph
   *          new graph model
   */
  public void setCurrentGraph(Graph currentGraph) {
    this.graphHolder.setCurrentGraph(currentGraph);
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
   * Layout current displayed graph.
   */
  public void autoLayout() {
    logger.debug("Check if graph can be layouted");
    if (!isRelativeSession) {
      Graph currentGraph = graphHolder.getCurrentGraph();
      if (currentGraph.getId() == graphs.size()) {
        logger.debug("Graph is last element in Queue, call Layouter");
        currentGraph.getVertices().forEach(v -> {
          v.setFixedPosition(false);
        });
        // visualModel.setDragging(false);
        autoLayoutingMode = true;
        layout();
      }
    }
  }

  /**
   * Returns session id.
   * 
   * @return clientSessionId
   */
  public long getSessionId() {
    return this.sessionId;
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
    Graph currentGraph = graphHolder.getCurrentGraph();
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
      layout();
      callLayoutEngine = false;
    } else {
      logger.debug("All graph positions are set");
      LayoutMonitor.getInstance().unlock();
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

  @Override
  public void saveSession(File file) {
    persistor.saveToDisk(this, file);
  }

  @Override
  public void changeCurrentGraphToNext() {
    int nextGraphId = graphHolder.getCurrentGraph().getId() + 1;
    graphHolder.setCurrentGraph(graphs.get(nextGraphId - 1));
  }

  @Override
  public void changeCurrentGraphToFirst() {
    graphHolder.setCurrentGraph(graphs.get(0));
  }

  @Override
  public void changeCurrentGraphToPrev() {
    int prevGraphId = graphHolder.getCurrentGraph().getId() - 1;
    graphHolder.setCurrentGraph(graphs.get(prevGraphId - 1));
  }

  @Override
  public void changeCurrentGraphToLast() {
    graphHolder.setCurrentGraph(graphs.get(graphs.size() - 1));
  }

  @Override
  public int getTotalGraphCount() {
    return graphs.size();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (sessionId ^ (sessionId >>> 32));
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
    if (sessionId != other.sessionId) {
      return false;
    }
    return true;
  }

  public void layout() {

    try {
      LayoutMonitor.getInstance().lock();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    Graph currentGraph = graphHolder.getCurrentGraph();

    logger.debug("Check if graph model is empty");
    if (currentGraph.getVertices().size() > 0) {

      // TODO check if first is relative (might be wrong. mwieland)
      if (currentGraph.getVertices().iterator().next().isRelative()) {
        logger.debug("Graph is relative");
        isRelativeSession = true;
      } else {
        logger.debug("Graph isn't relative. Layout graph");
        logger.info("Layouting elements of graph");
        layoutController.addObserver(this);
        layoutController.setElements(currentGraph.getVertices(),
            currentGraph.getEdges(), applicationController.isSoftLayout());
      }
    }

    LayoutMonitor.getInstance().unlock();
  }

}
