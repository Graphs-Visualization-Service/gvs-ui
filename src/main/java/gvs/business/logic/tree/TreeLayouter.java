package gvs.business.logic.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.ILayouter;
import gvs.business.model.graph.Graph;
import gvs.business.model.tree.TreeVertex;
import gvs.interfaces.Action;

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

  private static final int NODESIZE = 50;
  private static final double SIBLING_DISTANCE = 200;
  private static final double TREE_DISTANCE = 500;
  private static final Logger logger = LoggerFactory
      .getLogger(TreeLayouter.class);

  @Override
  public void layoutGraph(Graph currentGraph, boolean b, Action callback) {
    logger.info("Layouting tree...");

    List<TreeVertex> roots = currentGraph.getVertices().stream()
        .map(v -> (TreeVertex) v).filter(v -> v.isRoot())
        .collect(Collectors.toList());

    roots.forEach(root -> initializeVertices(root, 0));
    roots.forEach(root -> calculateInitialXPosition(root));
    roots.forEach(root -> checkAllChildrenOnScreen(root));
    roots.forEach(root -> calculateFinalPositions(root, 0));
    logger.debug(currentGraph.toString());
    logger.debug("\n");
  }

  private void checkAllChildrenOnScreen(TreeVertex vertex) {
    Map<Double, Double> nodeContour = new HashMap<>();

    getLeftContour(vertex, 0, nodeContour);

    double shiftAmount = 0;
    
    for (Double key : nodeContour.keySet()) {
      if (nodeContour.get(key) + shiftAmount < 0) {
        shiftAmount = (nodeContour.get(key) * -1);
      }
    }

    if (shiftAmount > 0) {
      vertex.setXPosition(vertex.getXPosition() + shiftAmount);
      vertex.setMod(vertex.getMod() + shiftAmount);
    }
  }

  private void getLeftContour(TreeVertex vertex, double modSum,
      Map<Double, Double> values) {
    if (!values.containsKey(vertex.getYPosition())) {
      values.put(vertex.getYPosition(), vertex.getXPosition() + modSum);
    } else {
      values.put(vertex.getYPosition(), Math.min(
          values.get(vertex.getYPosition()), vertex.getXPosition() + modSum));
    }
    modSum += vertex.getMod();
    double finalModSum = modSum;
    vertex.getChildren()
        .forEach(child -> getLeftContour(child, finalModSum, values));
  }

  private void getRightContour(TreeVertex vertex, double modSum,
      Map<Double, Double> values) {
    if (!values.containsKey(vertex.getYPosition())) {
      values.put(vertex.getYPosition(), vertex.getXPosition() + modSum);
    } else {
      values.put(vertex.getYPosition(), Math.max(
          values.get(vertex.getYPosition()), vertex.getXPosition() + modSum));
    }
    modSum += vertex.getMod();
    double finalModSum = modSum;
    vertex.getChildren()
        .forEach(child -> getRightContour(child, finalModSum, values));
  }

  private void calculateFinalPositions(TreeVertex vertex, double modSum) {
    vertex.setMod(vertex.getMod() + modSum);
    modSum += vertex.getMod();
    double finalModSum = modSum;
    vertex.getChildren()
        .forEach(child -> calculateFinalPositions(child, finalModSum));

    if (!vertex.getChildren().isEmpty()) {
      double biggestX = vertex.getChildren().stream()
          .sorted(
              (v1, v2) -> Double.compare(v2.getXPosition(), v1.getXPosition()))
          .collect(Collectors.toList()).get(0).getXPosition();
      double biggestY = vertex.getChildren().stream()
          .sorted(
              (v1, v2) -> Double.compare(v2.getYPosition(), v1.getYPosition()))
          .collect(Collectors.toList()).get(0).getYPosition();
      vertex.setXPosition(biggestX);
      vertex.setYPosition(biggestY);
    }
  }

  private void initializeVertices(TreeVertex vertex, int depth) {
    vertex.setXPosition(-1); // TODO: probably superfluous
    vertex.setYPosition(depth);
    vertex.setMod(0);// TODO: probably superfluous

    vertex.getChildren().forEach(child -> initializeVertices(child, depth + 1));
  }

  private void calculateInitialXPosition(TreeVertex vertex) {
    vertex.getChildren().forEach(child -> calculateInitialXPosition(child));

    // if no children
    if (vertex.getChildren().isEmpty()) {
      // if there is a previous sibling in this set, set X to prevous sibling +
      // designated distance
      if (!vertex.isLeftMostChild() && !vertex.isRoot()) {
        vertex.setXPosition(vertex.getPreviousSibling().getXPosition()
            + NODESIZE + SIBLING_DISTANCE);
      } else {
        // if this is the first node in a set, set X to 0
        vertex.setXPosition(0);
      }
    }
    // if there is only one child
    else if (vertex.getChildren().size() == 1) {
      // if this is the first node in a set, set it's X value equal to it's
      // child's X value
      if (vertex.isLeftMostChild()) {
        vertex.setXPosition(vertex.getChildren().get(0).getXPosition());
      } else {
        vertex.setXPosition(vertex.getPreviousSibling().getXPosition()
            + NODESIZE + SIBLING_DISTANCE);
        vertex.setMod(
            vertex.getXPosition() - vertex.getChildren().get(0).getXPosition());
      }
    } else {
      TreeVertex leftMostChild = vertex.getChildren().get(0);
      TreeVertex rightMostChild = vertex.getChildren()
          .get(vertex.getChildren().size() - 1);
      double middle = (leftMostChild.getXPosition()
          + rightMostChild.getXPosition()) / 2;

      if (vertex.isLeftMostChild()) {
        vertex.setXPosition(middle);
      } else {
        vertex.setXPosition(vertex.getPreviousSibling().getXPosition()
            + NODESIZE + SIBLING_DISTANCE);
        vertex.setMod(vertex.getXPosition() - middle);
      }
    }
    if (vertex.getChildren().size() > 0 && !vertex.isLeftMostChild()) {
      // Since subtrees can overlap, check for conflicts and shift tree right if
      // needed
      checkForConflicts(vertex);
    }
  }

  private void checkForConflicts(TreeVertex vertex) {
    double minDistance = TREE_DISTANCE + NODESIZE;
    double shiftValue = 0.0;

    Map<Double, Double> nodeContour = new HashMap<>();
    getLeftContour(vertex, 0, nodeContour);

    TreeVertex sibling = vertex.getLeftMostSibling();
    while (sibling != null && sibling != vertex) {
      Map<Double, Double> siblingContour = new HashMap<>();
      getRightContour(sibling, 0, siblingContour);

      for (double level = vertex.getYPosition() + 1; level <= Math.min(
          Collections.max(siblingContour.keySet()),
          Collections.max(nodeContour.keySet())); level++) {
        double distance = nodeContour.get(level) - siblingContour.get(level);
        if (distance + shiftValue < minDistance) {
          shiftValue = Math.max(minDistance - distance, shiftValue);
        }
      }

      if (shiftValue > 0) {
        vertex.setXPosition(vertex.getXPosition() + shiftValue);
        vertex.setMod(vertex.getMod() + shiftValue);

        centerNodesBetween(vertex, sibling);

        shiftValue = 0;
      }

      sibling = sibling.getNextSibling();
    }
  }

  private void centerNodesBetween(TreeVertex leftNode, TreeVertex rightNode) {
    int leftIndex = leftNode.getParent().getChildren().indexOf(leftNode);
    int rightIndex = leftNode.getParent().getChildren().indexOf(rightNode);

    double numNodesBetween = (rightIndex - leftIndex) - 1;

    if (numNodesBetween > 0) {
      double distanceBetweenNodes = (leftNode.getXPosition()
          - rightNode.getXPosition()) / (numNodesBetween + 1);

      int count = 1;
      for (int i = leftIndex + 1; i < rightIndex; i++) {
        TreeVertex middleNode = leftNode.getParent().getChildren().get(i);

        double desiredX = rightNode.getXPosition()
            + (distanceBetweenNodes * count);
        double offset = desiredX - middleNode.getXPosition();
        middleNode.setXPosition(middleNode.getXPosition() + offset);
        middleNode.setMod(middleNode.getMod() + offset);
        count++;
      }
      checkForConflicts(leftNode);
    }
  }

}
