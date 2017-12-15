package gvs.model;

import com.google.inject.Inject;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.logic.layouter.graph.GraphLayouter;

/**
 * Marks a session as a graph session. Holds a reference to the appropriate
 * layouter.
 * 
 * @author mwieland
 *
 */
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
