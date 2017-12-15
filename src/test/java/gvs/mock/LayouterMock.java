package gvs.mock;

import gvs.business.logic.Session;
import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.util.Action;

public class LayouterMock implements ILayouter {

  @Override
  public void layout(Session session) {
    // TODO Auto-generated method stub
  }

  @Override
  public void layout(Graph graph, Action callback) {
    // TODO Auto-generated method stub

  }

  @Override
  public void takeOverVertexPositions(Graph source, Graph target) {
    // TODO Auto-generated method stub

  }

  @Override
  public String toString() {
    return "GVS Mock of Layouter";
  }

}
