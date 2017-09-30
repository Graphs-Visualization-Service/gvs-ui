package gvs.ui.tree.controller;

import gvs.ui.tree.model.TreeModel;

import java.util.TimerTask;
import java.util.Vector;

/**
 * TimerTask, responsible for showing replays with a defined timeout
 * 
 * @author aegli
 *
 */
public class TreeSessionReplay extends TimerTask {
	private int replayCounter = 0;
	private Vector treeSessions = null;
	private TreeSessionController treeSessionController = null;
	private TreeModel treeModel = null;

	/**
	 * Creates an instance of TimerReplaySession
	 * 
	 * @param pTreeSessions
	 * @param pTreeSessionController
	 */
	public TreeSessionReplay(Vector pTreeSessions, TreeSessionController pTreeSessionController) {
		this.treeSessions = pTreeSessions;
		this.treeSessionController = pTreeSessionController;
	}

	/**
	 * Animates the model with the chosen speed
	 */
	public void run() {
		if (replayCounter < treeSessions.size()) {
			treeModel = (TreeModel) treeSessions.get(replayCounter);
			treeSessionController.setActualTreeModel(treeModel);
			treeSessionController.setVisualModel();
			replayCounter++;
		} else {
			treeSessionController.setReplayMode(false);
			treeSessionController.validateNavigation(treeModel.getModelId());
			this.cancel();
		}
	}

}