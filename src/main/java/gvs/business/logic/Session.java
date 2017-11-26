package gvs.business.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.access.Persistor;
import gvs.business.model.Graph;
import gvs.business.model.GraphHolder;
import gvs.util.Action;

/**
 * A session represents a collection of graphs. It contains logic to navigate
 * through its graphs, which included the replay functionality.
 * 
 * @author mtrentini
 *
 */
public class Session {

  private SessionReplay currentReplayThread;

  private final ISessionType sessionType;
  private final long id;
  private final String sessionName;
  private final GraphHolder graphHolder;
  private final List<Graph> graphs;
  private final SessionReplayFactory sessionReplayFactory;

  private static final Logger logger = LoggerFactory.getLogger(Session.class);

  @Inject
  public Session(GraphHolder graphHolder,
      SessionReplayFactory replayFactory, @Assisted ISessionType sessionType,
      @Assisted long sessionId, @Assisted String sessionName) {

    logger.info("Instantiating new graph session.");
    this.sessionReplayFactory = replayFactory;
    this.sessionType = sessionType;

    this.id = sessionId;
    this.sessionName = sessionName;
    this.graphHolder = graphHolder;
    this.graphs = new ArrayList<>();
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

  public void changeCurrentGraphToNext() {
    Graph currentGraph = graphHolder.getCurrentGraph();
    int nextGraphId = currentGraph.getId() + 1;
    if (validIndex(nextGraphId)) {
      Graph nextGraph = getGraphs().get(nextGraphId - 1);
      getSessionType().getLayouter().takeOverVertexPositions(currentGraph,
          nextGraph);
      graphHolder.setCurrentGraph(nextGraph);
    }
  }

  public void changeCurrentGraphToPrev() {
    Graph currentGraph = graphHolder.getCurrentGraph();
    int prevGraphId = currentGraph.getId() - 1;
    if (validIndex(prevGraphId)) {
      Graph previousGraph = getGraphs().get(prevGraphId - 1);
      getSessionType().getLayouter().takeOverVertexPositions(currentGraph,
          previousGraph);
      graphHolder.setCurrentGraph(previousGraph);
    }
  }

  public void changeCurrentGraphToFirst() {
    Graph currentGraph = graphHolder.getCurrentGraph();
    Graph firstGraph = getGraphs().get(0);
    getSessionType().getLayouter().takeOverVertexPositions(currentGraph,
        firstGraph);
    graphHolder.setCurrentGraph(firstGraph);
  }

  public void changeCurrentGraphToLast() {
    Graph currentGraph = graphHolder.getCurrentGraph();
    int newIndex = getGraphs().size() - 1;
    if (validIndex(newIndex)) {
      Graph lastGraph = getGraphs().get(newIndex);
      getSessionType().getLayouter().takeOverVertexPositions(currentGraph,
          lastGraph);
      graphHolder.setCurrentGraph(lastGraph);
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

  public int getTotalGraphCount() {
    return getGraphs().size();
  }

  public GraphHolder getGraphHolder() {
    return graphHolder;
  }

  public String getSessionName() {
    return sessionName;
  }

  public ISessionType getSessionType() {
    return sessionType;
  }

  public long getId() {
    return this.id;
  }

  public List<Graph> getGraphs() {
    return graphs;
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
