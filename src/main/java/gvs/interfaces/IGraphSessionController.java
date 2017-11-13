package gvs.interfaces;

import java.util.List;

import gvs.business.model.graph.Graph;

public interface IGraphSessionController extends ISession {

  void addGraph(Graph graph);

  List<Graph> getGraphs();

}
