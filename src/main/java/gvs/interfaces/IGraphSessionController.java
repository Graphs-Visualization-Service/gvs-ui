package gvs.interfaces;

import java.util.AbstractList;
import java.util.Observable;

import gvs.ui.graph.model.GraphModel;

public interface IGraphSessionController extends ISessionController {

  void addGraphModel(GraphModel pGraphModel);

  void update(Observable o, Object arg);

  GraphModel getCurrentGraphModel();

  void setCurrentGraphModel(GraphModel currentGraphModel);

  @SuppressWarnings("rawtypes")
  AbstractList getMyGraphModels();

}
