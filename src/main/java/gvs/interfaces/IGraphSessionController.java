package gvs.interfaces;

import java.util.List;
import java.util.Observable;

import gvs.business.model.graph.Graph;

public interface IGraphSessionController extends ISessionController {

  void addGraph(Graph graph);

  void update(Observable o, Object arg);

  Graph getCurrentGraph();

  void setCurrentGraph(Graph currentGraph);

  List<Graph> getGraphs();

}
