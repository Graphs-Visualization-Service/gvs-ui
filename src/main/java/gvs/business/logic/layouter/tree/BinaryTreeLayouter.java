package gvs.business.logic.layouter.tree;

import com.google.inject.Singleton;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.util.Action;

/**
 * Calculates the position of each TreeVertex as specified by the
 * Reingold-Tilford Algorithm. See: https://llimllib.github.io/pymag-trees/
 * 
 * @author mtrentini
 *
 */
@Singleton
public class BinaryTreeLayouter implements ILayouter {

  @Override
  public void layoutGraph(Graph currentGraph, boolean useRandomLayout,
      Action callback) {
    // TODO Auto-generated method stub

  }

}
