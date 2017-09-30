package gvs.interfaces;

import gvs.ui.graph.model.GraphModel;

import java.util.AbstractList;
import java.util.Observable;

public interface IGraphSessionController extends ISessionController{
	
	public abstract void addGraphModel(GraphModel pGraphModel);

	public abstract void update(Observable o, Object arg);

	public abstract GraphModel getActualGraphModel();
	
	public abstract void setActualGraphModel(GraphModel actualGraphModel);

	public abstract AbstractList getMyGraphModels();

}


