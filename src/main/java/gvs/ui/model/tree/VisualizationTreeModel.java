package gvs.ui.model.tree;

import java.util.Observable;

import gvs.business.logic.tree.TreeLayoutController;
import gvs.business.model.tree.Tree;

/**
 * Model which gives updates to the visualization panel
 * 
 * @author aegli
 *
 */

public class VisualizationTreeModel extends Observable {

  private Tree treeModel = null;

  /**
   * Builds an instance of a VisualTreeModel
   *
   */
  public VisualizationTreeModel() {
  }

  /**
   * Sets a tree model for displaying
   * 
   * @param pTreeModel
   */
  public void setTreeModel(Tree pTreeModel,
      TreeLayoutController pLayoutController) {
    this.treeModel = pTreeModel;
    setChanged();
    notifyObservers(pLayoutController);
  }

  /**
   * Returns currently held tree model
   * 
   */
  public Tree getTreeModel() {
    return treeModel;
  }
}
