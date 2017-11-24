package gvs.business.logic.layouter.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import gvs.business.logic.Session;
import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.tree.BinaryTreeVertex;
import gvs.business.model.tree.TreeVertex;
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
  private static final int d = 30; // TODO: rename, account for nodeSize
  private static final double SIBLING_DISTANCE = 20;
  private static final double LEVEL_DISTANCE = 20;
  private static final int CANVAS_WIDTH = 800;
  private static final int CANVAS_HEIGHT = 450;
  private static int hc; // TODO: rename
  private static int wc;// TODO: rename

  @Override
  public void layout(Session session, boolean useRandomLayout,
      Action callback) {
    // TODO Auto-generated method stub

  }

  @Override
  public void layout(Graph graph, boolean useRandomLayout, Action callback) {
    List<BinaryTreeVertex> roots = graph.getVertices().stream()
        .map(v -> (BinaryTreeVertex) v).filter(v -> v.isRoot())
        .collect(Collectors.toList());

    // TODO: support multiple roots
    roots.forEach(root -> {
      int treeHeight = calculateHeight(root);
      hc = CANVAS_HEIGHT / treeHeight;
      wc = (int) (CANVAS_WIDTH / (Math.pow(2, treeHeight)));
      int oX = 0; // offset x
      int oY = (hc - d) / 2; // offset Y
      int w = 0; // treeWidth
      int rX = 0; // root x
      Integer[] helperDim = new Integer[] { w, rX };
      compute(root, oX, oY, helperDim); // returns treeWidth and root X
      // setup(root, 0, null, null);
      // addMods(root);
    });
  }

  private int calculateHeight(TreeVertex vertex) {
    int h = 0;
    for (TreeVertex child : vertex.getChildren()) {
      h = Math.max(h, 1 + calculateHeight(child));
    }
    return h;
  }

  private void compute(BinaryTreeVertex vertex, int oX, int oY,
      Integer[] helperDim) {
    Integer w = helperDim[0]; // treeWidth
    Integer rX = helperDim[1]; // root x
    w = 0;
    int stW = 0; // The width of a subtree
    int stRX = 0; // X-coordinate of a subtree’s root
    if (vertex.isLeaf()) {
      w = d;
      rX = oX;
    } else {
      // draw left subtree
      BinaryTreeVertex leftChild = (BinaryTreeVertex) vertex.getLeftChild();
      BinaryTreeVertex rightChild = (BinaryTreeVertex) vertex.getRightChild();
      if (leftChild != null) {
        Integer[] helperDimChild = new Integer[] { stRX, stW };
        compute(leftChild, oX, oY + hc, helperDimChild);
      } else {
        w = d / 2;
        rX = oX;
      }
      // draw right subtree
      if (rightChild != null) {
        Integer[] helperDimChild = new Integer[] { stRX, stW };
        compute(rightChild, oX + w, oY + hc, helperDimChild);
        w += stW;
      } else {
        w += d / 2;
      }
      vertex.setXPosition(rX);
      vertex.setYPosition(oY);
    }

  }

  @Override
  public void takeOverVertexPositions(Graph source, Graph target) {
    // do nothing
  }

  private void setup(BinaryTreeVertex vertex, int depth,
      Map<Integer, Integer> nexts, Map<Integer, Double> offset) {
    if (nexts == null) {
      nexts = new HashMap<>();
    }
    if (offset == null) {
      offset = new HashMap<>();
    }
    List<BinaryTreeVertex> children = vertex.getChildren().stream()
        .map(c -> (BinaryTreeVertex) c).collect(Collectors.toList());
    for (BinaryTreeVertex child : children) {
      setup(child, depth + 1, nexts, offset);
    }
    vertex.setYPosition(depth * LEVEL_DISTANCE + LEVEL_DISTANCE);

    int place = 0;
    if (children.isEmpty()) {
      Integer next = nexts.get(depth);
      place = next == null ? 0 : next;
      vertex.setXPosition(place);
    } else if (children.size() == 1) {
      BinaryTreeVertex leftChild = (BinaryTreeVertex) vertex.getLeftChild();
      BinaryTreeVertex rightChild = (BinaryTreeVertex) vertex.getRightChild();
    }

  }

  private void addMods(TreeVertex root) {
    // TODO Auto-generated method stub

  }

}
