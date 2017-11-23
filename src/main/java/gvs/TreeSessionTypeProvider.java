package gvs;

import com.google.inject.Inject;
import com.google.inject.Provider;

import gvs.business.logic.GraphSessionType;
import gvs.business.logic.layouter.graph.GraphLayouter;

class TreeSessionTypeProvider implements Provider<GraphSessionType> {
  @Inject
  private final GraphLayouter graphLayouter;

  @Inject
  public TreeSessionTypeProvider(GraphLayouter layouter) {
    this.graphLayouter = layouter;
  }

  @Override
  public GraphSessionType get() {
    return new GraphSessionType(graphLayouter);
  }
}
