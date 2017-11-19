package gvs.business.logic;

import gvs.business.model.graph.Graph;
import gvs.interfaces.Action;

public interface ILayouter {

  void layoutGraph(Graph currentGraph, boolean useRandomLayout,
      Action callback);

}
