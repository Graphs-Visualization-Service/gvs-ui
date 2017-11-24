package gvs.business.logic;

import com.google.inject.Inject;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.logic.layouter.tree.BinaryTreeLayouter;

public class BinaryTreeSessionType implements ISessionType {

  
  private final ILayouter binaryTreeLayouter;

  @Inject
  public BinaryTreeSessionType(BinaryTreeLayouter layouter) {
    this.binaryTreeLayouter = layouter;
  }

  @Override
  public ILayouter getLayouter() {
    return binaryTreeLayouter;
  }
  
}
