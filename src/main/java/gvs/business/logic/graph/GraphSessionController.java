package gvs.business.logic.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import gvs.interfaces.Action;
import gvs.interfaces.IGraphSessionController;
import gvs.interfaces.IVertex;

/**
 * The session contoller reacts on user input events and implements most of the
 * visualization logic. It observes the LayoutController.
 * 
 * @author aegli
 *
 */
public class GraphSessionController implements IGraphSessionController {

  private boolean callLayoutEngine = false;
  private boolean replayMode = false;
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
  public void replay(long timeout, Action finishedCallback) {
    logger.info("Replay current session");
    Timer timer = new Timer();
    setReplayMode(true);
    GraphSessionReplay sessionReplay = sessionReplayFactory.create(this, graphs,
        finishedCallback);
    timer.schedule(sessionReplay, timeout, timeout);
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
      logger.debug("Graph is last element in Queue, call Layouter");
      currentGraph.getVertices().forEach(v -> {
        v.setFixedPosition(false);
      });
      
      layout();
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
    int newIndex = nextGraphId - 1;
    if (validIndex(newIndex)) {
      graphHolder.setCurrentGraph(graphs.get(newIndex));
    }
  }

  private boolean validIndex(int i) {
    return i >= 0 && i < graphs.size();
  }

  @Override
  public void changeCurrentGraphToFirst() {
    if (!graphs.isEmpty()) {
      graphHolder.setCurrentGraph(graphs.get(0));
    }
  }

  @Override
  public void changeCurrentGraphToPrev() {
    int prevGraphId = graphHolder.getCurrentGraph().getId() - 1;
    int newIndex = prevGraphId - 1;
    if (validIndex(newIndex)) {
      graphHolder.setCurrentGraph(graphs.get(newIndex));
    }

  }

  @Override
  public void changeCurrentGraphToLast() {
    int newIndex = graphs.size() - 1;
    if (validIndex(newIndex)) {
      graphHolder.setCurrentGraph(graphs.get(newIndex));
    }
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
      logger.info("Got layout monitor");

      Graph currentGraph = graphHolder.getCurrentGraph();
      layoutController.setGraphToLayout(currentGraph,
          applicationController.isSoftLayout());

    } catch (InterruptedException e) {
      logger.warn("Unable to get layout monitor", e);
    } finally {
      LayoutMonitor.getInstance().unlock();
    }
  }

}
