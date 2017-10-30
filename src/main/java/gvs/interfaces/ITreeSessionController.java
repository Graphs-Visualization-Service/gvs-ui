package gvs.interfaces;

import gvs.ui.tree.model.TreeModel;

public interface ITreeSessionController extends ISessionController {

  void addTreeModel(TreeModel pTreeModel);

  TreeModel getCurrentTreeModel();

  void setCurrentTreeModel(TreeModel currentTreeModel);

}
