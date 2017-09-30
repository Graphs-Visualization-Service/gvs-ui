package gvs.interfaces;

import gvs.ui.tree.model.TreeModel;

public interface ITreeSessionController extends ISessionController{

	public abstract void addTreeModel(TreeModel pTreeModel);
	
	public abstract TreeModel getActualTreeModel();
	
	public abstract void setActualTreeModel(TreeModel actualTreeModel);
	
}

