package gvs.business.logic.tree;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.ILayouter;
import gvs.business.model.graph.Graph;
import gvs.business.model.tree.TreeVertex;
import gvs.interfaces.Action;
import gvs.interfaces.IVertex;

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

  private static final int NODESIZE = 50; //TODO: account for nodeSize
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
      secondWalk(root, -root.getPrelim());
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
      verticesSorted
          .forEach(v -> v.setXPosition(v.getXPosition() + Math.abs(minX)+ MARGIN));
    }
  }

  private void secondWalk(TreeVertex vertex, double m) {
    vertex.setXPosition(vertex.getPrelim() + m);
    vertex.setYPosition(getLevel(vertex) * LEVEL_DISTANCE + MARGIN);
    vertex.getChildren()
        .forEach(child -> secondWalk(child, m + vertex.getMod()));
  }

  private double getLevel(TreeVertex vertex) {
    if (vertex.getParent() == null) {
      return 1;
    } else {
      return 1 + getLevel(vertex.getParent());
    }
  }

  private void firstWalk(TreeVertex vertex) {
    // if vertex is a leaf
    if (vertex.getChildren().isEmpty()) {
      vertex.setPrelim(0);
      TreeVertex leftSibling = getLeftSibling(vertex);
      if (leftSibling != null) {
        vertex.setPrelim(leftSibling.getPrelim() + SIBLING_DISTANCE);
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
      double midPoint = 0.5
          * (leftMostChild.getPrelim() + rightMostChild.getPrelim());
      TreeVertex leftSibling = getLeftSibling(vertex);
      if (leftSibling != null) {
        vertex.setPrelim(leftSibling.getPrelim() + SIBLING_DISTANCE);
        vertex.setMod(leftSibling.getPrelim() - midPoint);
      } else {
        vertex.setPrelim(midPoint);
      }
    }
  }

  private void executeShifts(TreeVertex vertex) {
    double shift = 0;
    double change = 0;
    List<TreeVertex> children = vertex.getChildren();
    for (int i = children.size() - 1; i == 0; i--) {
      TreeVertex child = children.get(i);
      child.setPrelim(child.getPrelim() + shift);
      child.setMod(child.getMod() + shift);
      change += child.getChange();
      shift = shift + child.getShift() + change;
    }
  }

  private void apportion(TreeVertex vertex, TreeVertex defaultAncestor) {
    TreeVertex leftSibling = getLeftSibling(vertex);
    if (leftSibling != null) {
      TreeVertex leftMostSibling = getLeftMostSibling(vertex);
      TreeVertex insideRightVertex = vertex;
      TreeVertex insideLeftVertex = leftSibling;
      TreeVertex outsideRightVertex = vertex;
      TreeVertex outsideLeftVertex = leftMostSibling;

      double insideRightModSum = insideRightVertex.getMod();
      double insideLeftModSum = insideLeftVertex.getMod();
      double outsideRightModSum = outsideRightVertex.getMod();
      double outsideLeftModSum = outsideLeftVertex.getMod();

      while (nextRight(insideLeftVertex) != null
          && nextLeft(insideRightVertex) != null) {
        insideLeftVertex = nextRight(insideLeftVertex);
        insideRightVertex = nextLeft(insideRightVertex);
        outsideLeftVertex = nextLeft(outsideLeftVertex);
        outsideRightVertex = nextRight(outsideRightVertex);
        double shift = (insideLeftVertex.getPrelim() + insideLeftModSum)
            - (insideRightVertex.getPrelim() + insideRightModSum)
            + SIBLING_DISTANCE;
        if (shift > 0) {
          moveSubTree(ancestor(insideLeftVertex, vertex, defaultAncestor),
              vertex, shift);
          insideRightModSum += shift;
          outsideRightModSum += shift;
        }
        insideLeftModSum += insideLeftVertex.getMod();
        insideRightModSum += insideRightVertex.getMod();
        outsideLeftModSum += outsideLeftVertex.getMod();
        outsideRightModSum += outsideRightVertex.getMod();
      }
      if (nextRight(insideLeftVertex) != null
          && nextRight(outsideRightVertex) == null) {
        outsideRightVertex.setThread(nextRight(insideLeftVertex));
        outsideRightVertex.setMod(outsideRightVertex.getMod() + insideLeftModSum
            + outsideRightModSum);
      }
      if (nextLeft(insideRightVertex) != null
          && nextLeft(outsideLeftVertex) == null) {
        outsideLeftVertex.setThread(nextLeft(insideRightVertex));
        outsideLeftVertex.setMod(
            outsideLeftVertex.getMod() + insideRightModSum + outsideLeftModSum);
        defaultAncestor = vertex;
      }
    }
  }

  private void moveSubTree(TreeVertex vertexLeft, TreeVertex vertexRight,
      double shift) {
    int subtrees = number(vertexRight) - number(vertexLeft);
    vertexRight.setChange(vertexRight.getChange() - shift / subtrees);
    vertexRight.setShift(vertexRight.getShift() + shift);
    vertexLeft.setChange(vertexLeft.getChange() - shift / subtrees);
    vertexRight.setPrelim(vertexRight.getPrelim() + shift);
    vertexRight.setMod(vertexRight.getMod() + shift);
  }

  private int number(TreeVertex vertexRight) {
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
    if (!vertex.getChildren().isEmpty()) {
      return vertex.getChildren().get(0);
    } else {
      return vertex.getThread();
    }
  }

  /**
   * Used to traverse the right contour of a subtree.
   * 
   * @param vertex
   * @return the successor of vertex on this contour
   */
  private TreeVertex nextRight(TreeVertex vertex) {
    if (!vertex.getChildren().isEmpty()) {
      return vertex.getChildren().get(vertex.getChildren().size() - 1);
    } else {
      return vertex.getThread();
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
