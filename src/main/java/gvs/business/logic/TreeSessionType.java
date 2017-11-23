package gvs.business.logic;

import com.google.inject.Inject;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.logic.layouter.tree.TreeLayouter;

public class TreeSessionType implements ISessionType {

  private final ILayouter treeLayouter;

  @Inject
  public TreeSessionType(TreeLayouter layouter) {
    this.treeLayouter = layouter;
  }

  @Override
  public ILayouter getLayouter() {
    return treeLayouter;
  }

}
