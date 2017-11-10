package gvs.business.logic.graph;

import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import gvs.business.model.graph.Graph;
import gvs.interfaces.Action;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author mwieland
 *
 */
public class GraphSessionReplay extends TimerTask {

  private int sessionCounter = 0;

  private final List<Graph> graphs;
  private final Session sessionController;
  private final Action finishedCallback;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphSessionReplay.class);

  /**
   * GraphSessionReplay.
   * 
   * @param graphs
   *          sessionController
   * @param sessionController
   *          sessionControlelr
   */
  @Inject
  public GraphSessionReplay(@Assisted Session sessionController,
      @Assisted List<Graph> graphs, @Assisted Action callback) {
    this.graphs = graphs;
    this.sessionController = sessionController;
    this.finishedCallback = callback;
  }

  /**
   * Executes the timer task.
   */
  public void run() {
    logger.info("Session replay task started...");
    if (sessionCounter < graphs.size()) {
      logger.info("Replay graph {}", sessionCounter);
      Graph visualizedGraph = graphs.get(sessionCounter);
      sessionController.setCurrentGraph(visualizedGraph);
      sessionCounter++;
    } else {
      logger.info("Replay finished");
      finishedCallback.execute();
      this.cancel();
    }
  }
}
