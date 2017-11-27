package gvs.business.model.tree;

/**
 * This class is only needed for layouting trees. For every non-existent child
 * of a binary tree vertex, a LeafVertex is created. It takes up space in
 * the tree creation during the layouting phase. The leaf vertex is not to be
 * added to the vertex collection of a graph, so that it will not be displayed
 * in the user interface.
 * 
 * @author mtrentini
 *
 */
public class LeafVertex extends TreeVertex {

  public LeafVertex(String parentLabel) {
    super(-1, parentLabel, null, false, null);
  }

  public boolean isLeaf() {
    return true;
  }

}
