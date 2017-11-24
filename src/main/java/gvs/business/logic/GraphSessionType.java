package gvs.business.logic;

import com.google.inject.Inject;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.logic.layouter.graph.GraphLayouter;

public class GraphSessionType implements ISessionType {

  private final ILayouter graphLayouter;

  @Inject
  public GraphSessionType(GraphLayouter layouter) {
    this.graphLayouter = layouter;
  }

  @Override
  public ILayouter getLayouter() {
    return graphLayouter;
  }

}
