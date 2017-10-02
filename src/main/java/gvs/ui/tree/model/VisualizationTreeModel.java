package gvs.ui.tree.model;

import java.util.Observable;

import gvs.ui.tree.layout.TreeLayoutController;

/**
 * Model which gives updates to the visualization panel
 * 
 * @author aegli
 *
 */

public class VisualizationTreeModel extends Observable {

  private TreeModel treeModel = null;

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
  public void setTreeModel(TreeModel pTreeModel,
      TreeLayoutController pLayoutController) {
    this.treeModel = pTreeModel;
    setChanged();
    notifyObservers(pLayoutController);
  }

  /**
   * Returns currently held tree model
   * 
   */
  public TreeModel getTreeModel() {
    return treeModel;
  }
}
