package gvs.business.logic.tree;

import java.util.TimerTask;
import java.util.Vector;

import gvs.business.model.tree.Tree;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author aegli
 *
 */
public class TreeSessionReplay extends TimerTask {
  private int replayCounter = 0;
  private Vector<Tree> treeSessions = null;
  private TreeSessionController treeSessionController = null;
  private Tree treeModel = null;

  /**
   * Creates an instance of TimerReplaySession
   * 
   * @param pTreeSessions
   * @param pTreeSessionController
   */
  public TreeSessionReplay(Vector<Tree> pTreeSessions,
      TreeSessionController pTreeSessionController) {
    this.treeSessions = pTreeSessions;
    this.treeSessionController = pTreeSessionController;
  }

  /**
   * Animates the model with the chosen speed
   */
  public void run() {
    if (replayCounter < treeSessions.size()) {
      treeModel = (Tree) treeSessions.get(replayCounter);
      treeSessionController.setCurrentTreeModel(treeModel);
      treeSessionController.setVisualModel();
      replayCounter++;
    } else {
      treeSessionController.setReplayMode(false);
      treeSessionController.validateNavigation(treeModel.getModelId());
      this.cancel();
    }
  }

}
