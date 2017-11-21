package gvs.business.logic.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.access.Persistor;
import gvs.business.logic.GraphLayoutMonitor;
import gvs.business.logic.ILayouter;
import gvs.business.logic.LayouterProvider;
import gvs.business.model.graph.Graph;
import gvs.business.model.graph.GraphHolder;
import gvs.interfaces.Action;
import gvs.interfaces.IVertex;

/**
 * The session controller reacts on user input events and implements most of the
 * visualization logic. It observes the LayoutController.
 * 
 * @author aegli
 *
 */
public class Session {

  private SessionReplay currentReplayThread;

  private final long id;
  private final String sessionName;
  private final GraphHolder graphHolder;
  private final List<Graph> graphs;

  private final SessionReplayFactory sessionReplayFactory;
  private final ILayouter layouter;
  private final Persistor persistor;
  private final GraphLayoutMonitor layoutMonitor;

  private final boolean isTreeSession;

  private static final Logger logger = LoggerFactory.getLogger(Session.class);

  @Inject
  public Session(GraphHolder graphHolder, Persistor persistor,
      GraphLayoutMonitor layoutMonitor, SessionReplayFactory replayFactory,
      LayouterProvider layouterProvider, @Assisted long sessionId,
      @Assisted String sessionName, @Assisted boolean isTreeSession) {

    logger.info("Instantiating new graph session.");
    this.sessionReplayFactory = replayFactory;
    this.persistor = persistor;
    this.isTreeSession = isTreeSession;
    if (isTreeSession) {
      this.layouter = layouterProvider.createTreeLayouter();
    } else {
      this.layouter = layouterProvider.createGraphLayouter();
    }
    this.layoutMonitor = layoutMonitor;

    this.id = sessionId;
    this.sessionName = sessionName;
    this.graphHolder = graphHolder;
    this.graphs = new ArrayList<>();
  }

  public boolean isTreeSession() {
    return isTreeSession;
  }

  /**
   * Adds a new graph session to an existing session.
   * 
   * @param graph
   *          new graph
   */
  public void addGraph(Graph graph) {

    int numberOfGraphs = getGraphs().size();
    if (numberOfGraphs == 0) {
      graph.setId(1);
    } else {
      Graph previousGraph = getGraphs().get(numberOfGraphs - 1);
      graph.setId(previousGraph.getId() + 1);
    }

    logger.info("Add new graph with id {} to session {}", graph.getId(),
        getId());
    graphs.add(graph);
  }

  public void layoutWholeSession(Action callback) {
    if (isTreeSession) {
      graphs.forEach(t -> layouter.layoutGraph(t, false, callback));
    }
  }

  public void layoutCurrentGraph(boolean useRandomLayout, Action callback) {
    Graph currentGraph = graphHolder.getCurrentGraph();

    if (currentGraph.isLayoutable()) {
      if (isTreeSession) {
        layouter.layoutGraph(currentGraph, false, callback);
      } else {
        try {
          layoutMonitor.lock();
          logger.info("Got layout monitor");

          layouter.layoutGraph(currentGraph, useRandomLayout, callback);

        } catch (InterruptedException e) {
          logger.warn("Unable to get layout monitor", e);
        } finally {
          layoutMonitor.unlock();
        }
      }
    } else if (callback != null) {
      callback.execute();
    }
  }

  /**
   * Displays requested model.
   */
  public void replay(long timeout, Action finishedCallback) {
    logger.info("Replaying current session");
    if (currentReplayThread == null || currentReplayThread.isCanceled()) {
      int startId = graphHolder.getCurrentGraph().getId();
      int lastId = graphs.get(graphs.size() - 1).getId();
      if (startId == lastId) {
        startId = 1;
        changeCurrentGraphToFirst();
      }

      this.currentReplayThread = sessionReplayFactory.create(this,
          finishedCallback, startId);
      Timer timer = new Timer();
      timer.schedule(currentReplayThread, timeout, timeout);
    } else {
      pauseReplay();
    }
  }

  public void pauseReplay() {
    if (this.currentReplayThread != null) {
      currentReplayThread.cancel();
      currentReplayThread = null;
    }
  }

  public void cancelReplay() {
    pauseReplay();
    changeCurrentGraphToFirst();
  }

  public GraphHolder getGraphHolder() {
    return graphHolder;
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
   * Returns session id.
   * 
   * @return session id
   */
  public long getId() {
    return this.id;
  }

  /**
   * Returns list, used for saving option.
   * 
   * @return graphModels
   */
  public List<Graph> getGraphs() {
    return graphs;
  }

  public void saveSession(File file) {
    persistor.saveToDisk(this, file);
  }

  public void changeCurrentGraphToNext() {
    Graph currentGraph = graphHolder.getCurrentGraph();
    int nextGraphId = currentGraph.getId() + 1;
    if (validIndex(nextGraphId)) {
      Graph nextGraph = getGraphs().get(nextGraphId - 1);
      if (!isTreeSession) {
        takeOverPreviousVertexPositions(graphHolder.getCurrentGraph(),
            nextGraph);
      } else {
        graphHolder.setCurrentGraph(nextGraph);
      }
    }
  }

  public void changeCurrentGraphToPrev() {
    int prevGraphId = graphHolder.getCurrentGraph().getId() - 1;
    if (validIndex(prevGraphId)) {
      Graph previousGraph = getGraphs().get(prevGraphId - 1);

      if (!isTreeSession) {
        takeOverPreviousVertexPositions(graphHolder.getCurrentGraph(),
            previousGraph);
      } else {
        graphHolder.setCurrentGraph(previousGraph);
      }
    }
  }

  public void changeCurrentGraphToFirst() {
    if (!getGraphs().isEmpty()) {
      Graph firstGraph = getGraphs().get(0);
      graphHolder.setCurrentGraph(firstGraph);
    }
  }

  public void changeCurrentGraphToLast() {
    int newIndex = getGraphs().size() - 1;
    if (validIndex(newIndex)) {
      Graph lastGraph = getGraphs().get(newIndex);

      if (!isTreeSession) {
        takeOverPreviousVertexPositions(graphHolder.getCurrentGraph(),
            lastGraph);
      } else {
        graphHolder.setCurrentGraph(lastGraph);
      }
    }
  }

  /**
   * Check if index is within range
   * 
   * @param i
   *          index
   * @return true if index is valid
   */
  private boolean validIndex(int i) {
    return i > 0 && i <= getGraphs().size();
  }

  /**
   * Reuse vertex coordinates of former graph.
   * 
   * @param sourceGraph
   *          source graph
   * @param targetGraph
   *          target graph
   */
  private void takeOverPreviousVertexPositions(Graph sourceGraph,
      Graph targetGraph) {

    Map<Long, IVertex> formerVertices = sourceGraph.getVertices().stream()
        .collect(Collectors.toMap(IVertex::getId, Function.identity()));

    boolean verticesToLayout = false;

    for (IVertex currentVertex : targetGraph.getVertices()) {
      IVertex formerVertex = formerVertices.get(currentVertex.getId());
      if (formerVertex != null) {
        currentVertex.setXPosition(formerVertex.getXPosition());
        currentVertex.setYPosition(formerVertex.getYPosition());
        currentVertex.setUserPositioned(formerVertex.isUserPositioned());
      } else {
        verticesToLayout = true;
      }
    }

    graphHolder.setCurrentGraph(targetGraph);

    if (verticesToLayout) {
      layoutCurrentGraph(true, null);
    }
  }

  public int getTotalGraphCount() {
    return getGraphs().size();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
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
    Session other = (Session) obj;
    if (id != other.id) {
      return false;
    }
    return true;
  }
}
