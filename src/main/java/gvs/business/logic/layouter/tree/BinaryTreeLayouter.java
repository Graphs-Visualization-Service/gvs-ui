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
  private static final int NODESIZE = 50; // TODO: account for nodeSize
  private static final double SIBLING_DISTANCE = 20;
  private static final double LEVEL_DISTANCE = 20;

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
      setup(root, 0, null, null);
      addMods(root);
    });
  }

  @Override
  public void takeOverVertexPositions(Graph source, Graph target) {
    // TODO Auto-generated method stub

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
