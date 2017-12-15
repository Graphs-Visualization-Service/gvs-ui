package gvs.business.logic.layouter;

import gvs.model.Graph;
import gvs.model.Session;
import gvs.util.Action;

public interface ILayouter {

  void layout(Session session);

  void layout(Graph graph, Action callback);

  void takeOverVertexPositions(Graph source, Graph target);

}
