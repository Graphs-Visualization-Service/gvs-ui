package gvs.interfaces;

import java.util.List;

import gvs.business.model.graph.Graph;

public interface IGraphSessionController extends ISession {

  void addGraph(Graph graph);

  Graph getCurrentGraph();

  void setCurrentGraph(Graph currentGraph);

  List<Graph> getGraphs();

}
