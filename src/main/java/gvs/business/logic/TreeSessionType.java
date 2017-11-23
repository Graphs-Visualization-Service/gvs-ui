package gvs.business.logic;

import com.google.inject.Inject;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.logic.layouter.graph.GraphLayouter;
import gvs.business.logic.layouter.tree.TreeLayouter;

public class TreeSessionType implements ISessionType {

  @Inject
  private TreeLayouter treeLayouter;

  @Override
  public ILayouter getLayouter() {
    return treeLayouter;
  }

}
