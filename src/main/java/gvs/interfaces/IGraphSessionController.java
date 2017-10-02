package gvs.interfaces;

import gvs.ui.graph.model.GraphModel;

import java.util.AbstractList;
import java.util.Observable;

public interface IGraphSessionController extends ISessionController {

  void addGraphModel(GraphModel pGraphModel);

  void update(Observable o, Object arg);

  GraphModel getActualGraphModel();

  void setActualGraphModel(GraphModel actualGraphModel);

  @SuppressWarnings("rawtypes")
  AbstractList getMyGraphModels();

}
