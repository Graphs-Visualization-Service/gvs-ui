package gvs.interfaces;

import gvs.ui.tree.model.TreeModel;

public interface ITreeSessionController extends ISessionController {

  void addTreeModel(TreeModel pTreeModel);

  TreeModel getActualTreeModel();

  void setActualTreeModel(TreeModel actualTreeModel);

}
