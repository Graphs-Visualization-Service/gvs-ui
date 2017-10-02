package gvs.ui.graph.controller;

import gvs.ui.graph.model.GraphModel;

import java.util.TimerTask;
import java.util.Vector;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author aegli
 *
 */
public class GraphSessionReplay extends TimerTask {
  private int sessionCounter = 0;
  private Vector sessionControllers = null;
  private GraphSessionController sessionController = null;
  private GraphModel graphModel = null;

  public GraphSessionReplay(Vector pSessionControllers,
      GraphSessionController pSessionController) {
    this.sessionControllers = pSessionControllers;
    this.sessionController = pSessionController;
  }

  public void run() {
    if (sessionCounter < sessionControllers.size()) {
      graphModel = (GraphModel) sessionControllers.get(sessionCounter);
      sessionController.setActualGraphModel(graphModel);
      sessionController.isDraggable();
      sessionController.setVisualModel();
      sessionCounter++;
    } else {
      sessionController.setReplayMode(false);
      sessionController.validateNavigation(graphModel.getModelId());
      this.cancel();
    }
  }
}
