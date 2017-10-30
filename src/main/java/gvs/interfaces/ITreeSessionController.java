package gvs.interfaces;

import gvs.business.model.tree.TreeModel;

public interface ITreeSessionController extends ISessionController {

  void addTreeModel(TreeModel pTreeModel);

  TreeModel getCurrentTreeModel();

  void setCurrentTreeModel(TreeModel currentTreeModel);

}
