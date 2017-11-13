package gvs.business.logic.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.access.Persistor;
import gvs.business.logic.LayoutMonitor;
import gvs.business.logic.Layouter;
import gvs.business.model.graph.Graph;
import gvs.business.model.graph.GraphHolder;
import gvs.interfaces.Action;
import gvs.interfaces.IGraphSessionController;

/**
 * The session controller reacts on user input events and implements most of the
 * visualization logic. It observes the LayoutController.
 * 
 * @author aegli
 *
 */
public class Session implements IGraphSessionController {

  private final long id;
  private final String sessionName;
  private final GraphHolder graphHolder;
  private final List<Graph> graphs;

  private final GraphSessionReplayFactory sessionReplayFactory;
  private final Layouter layouter;
  private final Persistor persistor;
  private final LayoutMonitor layoutMonitor;

  private static final Logger logger = LoggerFactory.getLogger(Session.class);

  @Inject
  public Session(GraphHolder graphHolder, Persistor persistor,
      Layouter layouter, LayoutMonitor layoutMonitor,
      GraphSessionReplayFactory replayFactory, @Assisted long sessionId,
      @Assisted String sessionName) {

    logger.info("Instantiating new graph session.");
    this.sessionReplayFactory = replayFactory;
    this.persistor = persistor;
    this.layouter = layouter;
    this.layoutMonitor = layoutMonitor;

    this.id = sessionId;
    this.sessionName = sessionName;
    this.graphHolder = graphHolder;
    this.graphs = new ArrayList<>();
  }

  /**
   * Adds a new graph session to an existing session.
   * 
   * @param pGraphModel
   *          graphModel
   */
  @Override
  public void addGraph(Graph graph) {

    int numberOfGraphs = getGraphs().size();
    if (numberOfGraphs == 0) {
      graph.setId(1);
    } else {
      Graph previousGraph = getGraphs().get(numberOfGraphs - 1);
      graph.setId(previousGraph.getId() + 1);
    }

    graphHolder.setCurrentGraph(graph);
    graphs.add(graph);
    logger.info("Added new graph with id {} to session {}", graph.getId(),
        getId());
  }

  @Override
  public void layoutCurrentGraph(Action callback) {
    try {
      layoutMonitor.lock();
      logger.info("Got layout monitor");

      Graph currentGraph = graphHolder.getCurrentGraph();
      currentGraph.getVertices().forEach(v -> {
        v.setIsLayouted(false);
      });

      // TODO isSoftLayout is always false -> check usage
      layouter.layoutGraph(currentGraph, false, callback);

    } catch (InterruptedException e) {
      logger.warn("Unable to get layout monitor", e);
    } finally {
      layoutMonitor.unlock();
    }
  }

  /**
   * Displays requested model.
   */
  @Override
  public void replay(long timeout, Action finishedCallback) {
    logger.info("Replaying current session");
    SessionReplay sessionReplay = sessionReplayFactory.create(this,
        finishedCallback);

    Timer timer = new Timer();
    timer.schedule(sessionReplay, timeout, timeout);
  }

  @Override
  public GraphHolder getGraphHolder() {
    return graphHolder;
  }

  /**
   * Returns session name.
   * 
   * @return sessionName
   */
  @Override
  public String getSessionName() {
    return sessionName;
  }

  /**
   * Returns session id.
   * 
   * @return session id
   */
  @Override
  public long getId() {
    return this.id;
  }

  /**
   * Returns list, used for saving option.
   * 
   * @return graphModels
   */
  @Override
  public List<Graph> getGraphs() {
    return graphs;
  }

  @Override
  public void saveSession(File file) {
    persistor.saveToDisk(this, file);
  }

  @Override
  public void changeCurrentGraphToNext() {
    int nextGraphId = graphHolder.getCurrentGraph().getId() + 1;
    if (validIndex(nextGraphId)) {
      graphHolder.setCurrentGraph(getGraphs().get(nextGraphId - 1));
    }
  }

  @Override
  public void changeCurrentGraphToPrev() {
    int prevGraphId = graphHolder.getCurrentGraph().getId() - 1;
    if (validIndex(prevGraphId)) {
      graphHolder.setCurrentGraph(getGraphs().get(prevGraphId - 1));
    }
  }

  private boolean validIndex(int i) {
    return i > 0 && i <= getGraphs().size();
  }

  @Override
  public void changeCurrentGraphToFirst() {
    if (!getGraphs().isEmpty()) {
      graphHolder.setCurrentGraph(getGraphs().get(0));
    }
  }

  @Override
  public void changeCurrentGraphToLast() {
    int newIndex = getGraphs().size() - 1;
    if (validIndex(newIndex)) {
      graphHolder.setCurrentGraph(getGraphs().get(newIndex));
    }
  }

  @Override
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
