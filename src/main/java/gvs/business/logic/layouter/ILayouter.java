package gvs.business.logic.layouter;

import gvs.business.model.Graph;
import gvs.util.Action;

public interface ILayouter {

  void layoutGraph(Graph currentGraph, boolean useRandomLayout,
      Action callback);

}
