package gvs.business.logic;

import com.google.inject.Inject;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.logic.layouter.graph.GraphLayouter;

public class GraphSessionType implements ISessionType {

  @Inject
  private GraphLayouter graphLayouter;

  @Override
  public ILayouter getLayouter() {
    return graphLayouter;
  }

}
