package gvs.business.logic.graph;

import java.util.List;
import java.util.TimerTask;

import gvs.business.model.graph.Graph;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author aegli
 *
 */
public class GraphSessionReplay extends TimerTask {
  private int sessionCounter = 0;

  private List<Graph> graphs = null;
  private GraphSessionController sessionController = null;
  private Graph graph = null;

  /**
   * GraphSessionReplay.
   * 
   * @param graphs
   *          sessionController
   * @param pSessionController
   *          sessionControlelr
   */
  public GraphSessionReplay(List<Graph> graphs,
      GraphSessionController pSessionController) {
    this.graphs = graphs;
    this.sessionController = pSessionController;
  }

  public void run() {
    if (sessionCounter < graphs.size()) {
      graph = graphs.get(sessionCounter);
      sessionController.setCurrentGraph(graph);
      sessionCounter++;
    } else {
      sessionController.setReplayMode(false);
      this.cancel();
    }
  }
}
