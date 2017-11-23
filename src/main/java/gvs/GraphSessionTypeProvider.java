package gvs;

import com.google.inject.Inject;
import com.google.inject.Provider;

import gvs.business.logic.TreeSessionType;
import gvs.business.logic.layouter.tree.TreeLayouter;

class GraphSessionTypeProvider implements Provider<TreeSessionType> {
  
  @Inject
  private final TreeLayouter treeLayouter;

  @Inject
  public GraphSessionTypeProvider(TreeLayouter layouter) {
    this.treeLayouter = layouter;
  }

  @Override
  public TreeSessionType get() {
    return new TreeSessionType(treeLayouter);
  }
}
