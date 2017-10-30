package gvs.ui.graph.controller;

import java.util.TimerTask;
import java.util.Vector;

import gvs.ui.graph.model.GraphModel;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author aegli
 *
 */
public class GraphSessionReplay extends TimerTask {
  private int sessionCounter = 0;
  
  @SuppressWarnings("rawtypes")
  private Vector sessionControllers = null;
  private GraphSessionController sessionController = null;
  private GraphModel graphModel = null;

  /**
   * GraphSessionReplay.
   * @param pSessionControllers sessionController
   * @param pSessionController sessionControlelr
   */
  @SuppressWarnings("rawtypes")
  public GraphSessionReplay(Vector pSessionControllers,
      GraphSessionController pSessionController) {
    this.sessionControllers = pSessionControllers;
    this.sessionController = pSessionController;
  }

  public void run() {
    if (sessionCounter < sessionControllers.size()) {
      graphModel = (GraphModel) sessionControllers.get(sessionCounter);
      sessionController.setCurrentGraphModel(graphModel);
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
