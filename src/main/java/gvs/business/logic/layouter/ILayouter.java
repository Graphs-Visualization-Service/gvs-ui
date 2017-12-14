package gvs.business.logic.layouter;

import gvs.business.logic.Session;
import gvs.business.model.Graph;
import gvs.util.Action;

public interface ILayouter {

  void layout(Session session, Action callback);

  void layout(Graph graph, Action callback);

  void takeOverVertexPositions(Graph source, Graph target);

}
