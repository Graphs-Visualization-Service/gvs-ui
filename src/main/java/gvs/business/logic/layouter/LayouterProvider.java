package gvs.business.logic.layouter;

import com.google.inject.Inject;

import gvs.business.logic.layouter.graph.GraphLayouter;
import gvs.business.logic.layouter.tree.TreeLayouter;

/**
 * Factory interface for Guice
 * 
 * @author mtrentini
 *
 */
public class LayouterProvider {

  private final TreeLayouter treeLayouter;
  private final GraphLayouter graphLayouter;

  @Inject
  public LayouterProvider(TreeLayouter treeLayouter, GraphLayouter graphLayouter) {
    this.treeLayouter = treeLayouter;
    this.graphLayouter = graphLayouter;
  }

  public TreeLayouter createTreeLayouter() {
    return treeLayouter;
  }

  public GraphLayouter createGraphLayouter() {
    return graphLayouter;
  }
}
