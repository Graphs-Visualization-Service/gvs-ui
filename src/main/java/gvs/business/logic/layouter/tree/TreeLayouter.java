package gvs.business.logic.layouter.tree;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.Session;
import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.IVertex;
import gvs.business.model.tree.TreeVertex;
import gvs.business.model.tree.TreeVertexLayoutValues;
import gvs.util.Action;

/**
 * Calculates the position of each TreeVertex as specified by an extension of
 * the Reingold-Tilford Algorithm. See:
 * https://dl.acm.org/citation.cfm?id=1133017
 * 
 * @author mtrentini
 *
 */
@Singleton
public class TreeLayouter implements ILayouter {

  private static final int VERTEX_X_RADIUS = 50; // TODO: account for nodeSize
  private static final double DEFAULT_DISPLAY_WIDTH = 800;
  private static final double DEFAULT_DISPLAY_HEIGHT = 450;
  private static final double VERTEX_X_DISTANCE = 20;
  private static double LEVEL_DISTANCE;
  private static final Logger logger = LoggerFactory
      .getLogger(TreeLayouter.class);
  private static final double MARGIN = 10;

  @Override
  public void layout(Session session, boolean useRandomLayout,
      Action callback) {
    session.getGraphs().forEach(g -> layout(g, useRandomLayout, callback));
  }

  @Override
  public void layout(Graph graph, boolean useRandomLayout, Action callback) {
    logger.info("Layouting tree...");

    List<TreeVertex> roots = graph.getVertices().stream()
        .map(v -> (TreeVertex) v).filter(v -> v.isRoot())
        .collect(Collectors.toList());

    // TODO: support multiple roots
    roots.forEach(root -> {
      int height = computeHeight(root);
      LEVEL_DISTANCE = (DEFAULT_DISPLAY_HEIGHT - 2 * MARGIN) / height;
      firstWalk(root);
      secondWalk(root, -root.getLayoutValues().getPreliminary());
      centerGraph(root, graph);
    });

    logger.info("Finished layouting tree.");
  }

  private int computeHeight(TreeVertex vertex) {
    int height = 0;
    for (TreeVertex child : vertex.getChildren()) {
      height = Math.max(height, 1 + computeHeight(child));
    }
    return height;
  }

  /**
   * Bottom-up traversal of the tree. The position of each node is preliminary.
   * 
   * @param vertex
   */
  private void firstWalk(TreeVertex vertex) {
    TreeVertexLayoutValues layoutValues = vertex.getLayoutValues();
    if (vertex.isLeaf()) {
      layoutValues.setPreliminary(DEFAULT_DISPLAY_WIDTH / 2);
      TreeVertex leftSibling = getLeftSibling(vertex);
      if (leftSibling != null) {
        layoutValues
            .setPreliminary(leftSibling.getLayoutValues().getPreliminary()
                + VERTEX_X_RADIUS + VERTEX_X_DISTANCE);
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
      double midPoint = 0.5 * (leftMostChild.getLayoutValues().getPreliminary()
          + rightMostChild.getLayoutValues().getPreliminary());
      TreeVertex leftSibling = getLeftSibling(vertex);
      if (leftSibling != null) {
        layoutValues.setPreliminary(
            leftSibling.getLayoutValues().getPreliminary() + VERTEX_X_DISTANCE);
        layoutValues
            .setMod(leftSibling.getLayoutValues().getPreliminary() - midPoint);
      } else {
        layoutValues.setPreliminary(midPoint);
      }
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
    vertex.setXPosition(vertex.getLayoutValues().getPreliminary() + modSum);
    vertex.setYPosition(getLevel(vertex) * LEVEL_DISTANCE + MARGIN);
    vertex.getChildren().forEach(
        child -> secondWalk(child, modSum + vertex.getLayoutValues().getMod()));
  }

  private double getLevel(TreeVertex vertex) {
    if (vertex.getParent() == null) {
      return 1;
    } else {
      return 1 + getLevel(vertex.getParent());
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
      TreeVertexLayoutValues childHelper = child.getLayoutValues();
      childHelper.setPreliminary(childHelper.getPreliminary() + shift);
      childHelper.setMod(childHelper.getMod() + shift);
      change += childHelper.getChange();
      shift = shift + childHelper.getShift() + change;
    }
  }

  /**
   * This method satisfies the 6th aesthetic property required to display trees:
   * 6) The children of a node should be equally spaced.
   * <p>
   * Mechanism: each child of the current root is placed as close to the right
   * of its left sibling as possible. Traversing the contours of the subtree
   * then finds conflicting neighbours. Such conflicts are resolved by shifting
   * affected subtrees to the right.
   * <p>
   * Variables:<br>
   * insideRightVertex: vertex used for traversal along the inside contour of
   * the right subtree <br>
   * insideLeftVertex: vertex used for traversal along the inside contour of the
   * left subtree<br>
   * outsideRightVertex: vertex used for traversal along the outside contour of
   * the right subtree<br>
   * outsideLeftVertex: vertex used for traversal along the outside contour of
   * the left subtree<br>
   * associated modSum variables are used to summing up the modifiers along the
   * corresponding contour.
   * 
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

      double insideRightModSum = insideRightVertex.getLayoutValues().getMod();
      double insideLeftModSum = insideLeftVertex.getLayoutValues().getMod();
      double outsideRightModSum = outsideRightVertex.getLayoutValues().getMod();
      double outsideLeftModSum = outsideLeftVertex.getLayoutValues().getMod();

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
        insideLeftModSum += insideLeftVertex.getLayoutValues().getMod()
            + VERTEX_X_RADIUS;
        insideRightModSum += insideRightVertex.getLayoutValues().getMod()
            + VERTEX_X_RADIUS;
        outsideLeftModSum += outsideLeftVertex.getLayoutValues().getMod()
            + VERTEX_X_RADIUS;
        outsideRightModSum += outsideRightVertex.getLayoutValues().getMod()
            + VERTEX_X_RADIUS;
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
    TreeVertexLayoutValues helper = source.getLayoutValues();
    helper.setThread(nextRight(target));
    helper.setMod(helper.getMod() + insideMod + outsideMod);
  }

  private double calculateShift(TreeVertex insideRightVertex,
      TreeVertex insideLeftVertex, double insideRightModSum,
      double insideLeftModSum) {
    return (insideLeftVertex.getLayoutValues().getPreliminary()
        + insideLeftModSum)
        - (insideRightVertex.getLayoutValues().getPreliminary()
            + insideRightModSum)
        + VERTEX_X_DISTANCE;
  }

  private void moveSubTree(TreeVertex vertexLeft, TreeVertex vertexRight,
      double shift) {
    TreeVertexLayoutValues rightHelper = vertexRight.getLayoutValues();
    TreeVertexLayoutValues leftHelper = vertexLeft.getLayoutValues();
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
      return vertex.getLayoutValues().getThread();
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
      return vertex.getLayoutValues().getThread();
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

  @Override
  public void takeOverVertexPositions(Graph source, Graph target) {
    // do nothing
  }

}
