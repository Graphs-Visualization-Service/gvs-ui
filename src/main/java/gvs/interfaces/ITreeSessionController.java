package gvs.interfaces;

import gvs.business.model.tree.Tree;

public interface ITreeSessionController extends ISessionController {

  void addTreeModel(Tree pTreeModel);

  Tree getCurrentTreeModel();

  void setCurrentTreeModel(Tree currentTreeModel);

}
