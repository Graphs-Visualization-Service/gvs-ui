package gvs.business.logic.layouter;

import com.google.inject.Inject;

import gvs.business.logic.layouter.graph.GraphLayouter;
import gvs.business.logic.layouter.tree.BinaryTreeLayouter;
import gvs.business.logic.layouter.tree.TreeLayouter;

/**
 * Factory interface for Guice
 * 
 * @author mtrentini
 *
 */
public class LayouterProvider {

  private final TreeLayouter treeLayouter;
  private final BinaryTreeLayouter binaryTreeLayouter;
  private final GraphLayouter graphLayouter;

  @Inject
  public LayouterProvider(TreeLayouter treeLayouter, BinaryTreeLayouter binaryTreeLayouter, GraphLayouter graphLayouter) {
    this.treeLayouter = treeLayouter;
    this.binaryTreeLayouter = binaryTreeLayouter;
    this.graphLayouter = graphLayouter;
  }

  public TreeLayouter getTreeLayouter() {
    return treeLayouter;
  }
  
  public BinaryTreeLayouter getBinaryTreeLayouter() {
    return binaryTreeLayouter;
  }

  public GraphLayouter getGraphLayouter() {
    return graphLayouter;
  }
}
