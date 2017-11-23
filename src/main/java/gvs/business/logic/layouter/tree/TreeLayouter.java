package gvs.business.logic.layouter.tree;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.IVertex;
import gvs.business.model.tree.TreeVertex;
import gvs.business.model.tree.TreeVertexLayoutHelper;
import gvs.util.Action;

/**
 * Calculates the position of each TreeVertex as specified by the
 * Reingold-Tilford Algorithm. See:
 * https://rachel53461.wordpress.com/2014/04/20/algorithm-for-drawing-trees/
 * 
 * @author mtrentini
 *
 */
@Singleton
public class TreeLayouter implements ILayouter {

  private static final int NODESIZE = 50; // TODO: account for nodeSize
  private static final double SIBLING_DISTANCE = 20;
  private static final double LEVEL_DISTANCE = 20;
  private static final Logger logger = LoggerFactory
      .getLogger(TreeLayouter.class);
  private static final double MARGIN = 10;

  @Override
  public void layoutGraph(Graph currentGraph, boolean b, Action callback) {
    logger.info("Layouting tree...");

    List<TreeVertex> roots = currentGraph.getVertices().stream()
        .map(v -> (TreeVertex) v).filter(v -> v.isRoot())
        .collect(Collectors.toList());

    // TODO: support multiple roots
    roots.forEach(root -> {
      firstWalk(root);
      secondWalk(root, -root.getHelper().getPreliminary());
      centerGraph(root, currentGraph);
    });

    logger.info("Finished layouting tree.");
  }

  private void centerGraph(TreeVertex root, Graph currentGraph) {
    List<IVertex> verticesSorted = currentGraph.getVertices().stream()
        .sorted(
            (v1, v2) -> Double.compare(v1.getXPosition(), v2.getXPosition()))
        .collect(Collectors.toList());
    double minX = verticesSorted.get(0).getXPosition();
    if (minX < 0) {
      verticesSorted.forEach(
          v -> v.setXPosition(v.getXPosition() + Math.abs(minX) + MARGIN));
    }
  }

  /**
   * Top-down traversal of the tree. Compute all real positions in linear time.
   * The real position of a vertex is its preliminary position plus the
   * aggregated modifier given by the sum of all modifiers on the path from the
   * parent of the vertex to the root.
   * 
   * @param vertex
   * @param modSum
   */
  private void secondWalk(TreeVertex vertex, double modSum) {
    vertex.setXPosition(vertex.getHelper().getPreliminary() + modSum);
    vertex.setYPosition(getLevel(vertex) * LEVEL_DISTANCE + MARGIN);
    vertex.getChildren().forEach(
        child -> secondWalk(child, modSum + vertex.getHelper().getMod()));
  }

  private double getLevel(TreeVertex vertex) {
    if (vertex.getParent() == null) {
      return 1;
    } else {
      return 1 + getLevel(vertex.getParent());
    }
  }

  /**
   * Bottom-up traversal of the tree. The position of each node is preliminary.
   * 
   * @param vertex
   */
  private void firstWalk(TreeVertex vertex) {
    TreeVertexLayoutHelper helper = vertex.getHelper();
    if (vertex.isLeaf()) {
      helper.setPreliminary(0);
      TreeVertex leftSibling = getLeftSibling(vertex);
      if (leftSibling != null) {
        helper.setPreliminary(
            leftSibling.getHelper().getPreliminary() + SIBLING_DISTANCE);
      }
    } else {
      TreeVertex defaultAncestor = vertex.getChildren().get(0);
      vertex.getChildren().forEach(child -> {
        firstWalk(child);
        apportion(child, defaultAncestor);
      });
      executeShifts(vertex);
      TreeVertex leftMostChild = vertex.getChildren().get(0);
      TreeVertex rightMostChild = vertex.getChildren()
          .get(vertex.getChildren().size() - 1);
      double midPoint = 0.5 * (leftMostChild.getHelper().getPreliminary()
          + rightMostChild.getHelper().getPreliminary());
      TreeVertex leftSibling = getLeftSibling(vertex);
      if (leftSibling != null) {
        helper.setPreliminary(
            leftSibling.getHelper().getPreliminary() + SIBLING_DISTANCE);
        helper.setMod(leftSibling.getHelper().getPreliminary() - midPoint);
      } else {
        helper.setPreliminary(midPoint);
      }
    }
  }

  /**
   * Shifts the current subtree of input vertex. When moving a subtree rooted at
   * vertex, only its mod and preliminary x-coordinate are adjusted by the
   * amount of shifting.
   * 
   * @param vertex
   */
  private void executeShifts(TreeVertex vertex) {
    double shift = 0;
    double change = 0;
    List<TreeVertex> children = vertex.getChildren();
    for (int i = children.size() - 1; i == 0; i--) {
      TreeVertex child = children.get(i);
      TreeVertexLayoutHelper childHelper = child.getHelper();
      childHelper.setPreliminary(childHelper.getPreliminary() + shift);
      childHelper.setMod(childHelper.getMod() + shift);
      change += childHelper.getChange();
      shift = shift + childHelper.getShift() + change;
    }
  }

  /**
   * This method satisfies the 6th aesthetic property required to display trees:
   * 6) The children of a node should be equally spaced.
   * 
   * Mechanism: each child of the current root is placed as close to the right
   * of its left sibling as possible. Traversing the contours of the subtree
   * then finds conflicting neighbours. Such conflicts are resolved by shifting
   * affected subtrees to the right.
   * 
   * @param vertex
   * @param defaultAncestor
   */
  private void apportion(TreeVertex vertex, TreeVertex defaultAncestor) {
    TreeVertex leftSibling = getLeftSibling(vertex);
    if (leftSibling != null) {
      TreeVertex leftMostSibling = getLeftMostSibling(vertex);
      TreeVertex insideRightVertex = vertex;
      TreeVertex insideLeftVertex = leftSibling;
      TreeVertex outsideRightVertex = vertex;
      TreeVertex outsideLeftVertex = leftMostSibling;

      double insideRightModSum = insideRightVertex.getHelper().getMod();
      double insideLeftModSum = insideLeftVertex.getHelper().getMod();
      double outsideRightModSum = outsideRightVertex.getHelper().getMod();
      double outsideLeftModSum = outsideLeftVertex.getHelper().getMod();

      while (nextRight(insideLeftVertex) != null
          && nextLeft(insideRightVertex) != null) {
        insideLeftVertex = nextRight(insideLeftVertex);
        insideRightVertex = nextLeft(insideRightVertex);
        outsideLeftVertex = nextLeft(outsideLeftVertex);
        outsideRightVertex = nextRight(outsideRightVertex);
        double shift = calculateShift(insideRightVertex, insideLeftVertex,
            insideRightModSum, insideLeftModSum);
        if (shift > 0) {
          moveSubTree(ancestor(insideLeftVertex, vertex, defaultAncestor),
              vertex, shift);
          insideRightModSum += shift;
          outsideRightModSum += shift;
        }
        insideLeftModSum += insideLeftVertex.getHelper().getMod();
        insideRightModSum += insideRightVertex.getHelper().getMod();
        outsideLeftModSum += outsideLeftVertex.getHelper().getMod();
        outsideRightModSum += outsideRightVertex.getHelper().getMod();
      }
      if (nextRight(insideLeftVertex) != null
          && nextRight(outsideRightVertex) == null) {
        updateThreadAndMod(outsideRightVertex, insideLeftVertex,
            insideLeftModSum, outsideRightModSum);
      }
      if (nextLeft(insideRightVertex) != null
          && nextLeft(outsideLeftVertex) == null) {
        updateThreadAndMod(outsideLeftVertex, insideRightVertex,
            insideRightModSum, outsideLeftModSum);
        defaultAncestor = vertex;
      }
    }
  }

  private void updateThreadAndMod(TreeVertex source, TreeVertex target,
      double insideMod, double outsideMod) {
    TreeVertexLayoutHelper helper = source.getHelper();
    helper.setThread(nextRight(target));
    helper.setMod(helper.getMod() + insideMod + outsideMod);
  }

  private double calculateShift(TreeVertex insideRightVertex,
      TreeVertex insideLeftVertex, double insideRightModSum,
      double insideLeftModSum) {
    return (insideLeftVertex.getHelper().getPreliminary() + insideLeftModSum)
        - (insideRightVertex.getHelper().getPreliminary() + insideRightModSum)
        + SIBLING_DISTANCE;
  }

  private void moveSubTree(TreeVertex vertexLeft, TreeVertex vertexRight,
      double shift) {
    TreeVertexLayoutHelper rightHelper = vertexRight.getHelper();
    TreeVertexLayoutHelper leftHelper = vertexLeft.getHelper();
    int subtrees = childIndex(vertexRight) - childIndex(vertexLeft);
    rightHelper.setChange(rightHelper.getChange() - shift / subtrees);
    rightHelper.setShift(rightHelper.getShift() + shift);
    leftHelper.setChange(leftHelper.getChange() - shift / subtrees);
    rightHelper.setPreliminary(rightHelper.getPreliminary() + shift);
    rightHelper.setMod(rightHelper.getMod() + shift);
  }

  private int childIndex(TreeVertex vertexRight) {
    return vertexRight.getParent().getChildren().indexOf(vertexRight);
  }

  private TreeVertex ancestor(TreeVertex insideLeftVertex, TreeVertex vertex,
      TreeVertex defaultAncestor) {
    if (insideLeftVertex.getParent().getParent().equals(vertex.getParent())) {
      return insideLeftVertex.getParent();
    } else {
      return defaultAncestor;
    }
  }

  /**
   * Used to traverse the left contour of a subtree.
   * 
   * @param vertex
   * @return the successor of vertex on this contour
   */
  private TreeVertex nextLeft(TreeVertex vertex) {
    if (!vertex.isLeaf()) {
      return vertex.getChildren().get(0);
    } else {
      return vertex.getHelper().getThread();
    }
  }

  /**
   * Used to traverse the right contour of a subtree.
   * 
   * @param vertex
   * @return the successor of vertex on this contour
   */
  private TreeVertex nextRight(TreeVertex vertex) {
    if (!vertex.isLeaf()) {
      return vertex.getChildren().get(vertex.getChildren().size() - 1);
    } else {
      return vertex.getHelper().getThread();
    }
  }

  private TreeVertex getLeftMostSibling(TreeVertex vertex) {
    TreeVertex parent = vertex.getParent();
    if (parent != null) {
      return parent.getChildren().get(0);
    }
    return null;
  }

  private TreeVertex getLeftSibling(TreeVertex vertex) {
    TreeVertex parent = vertex.getParent();
    if (parent != null) {
      int index = parent.getChildren().indexOf(vertex);
      if (index == 0) {
        return null;
      } else {
        return parent.getChildren().get(index - 1);
      }
    }
    return null;
  }
}
