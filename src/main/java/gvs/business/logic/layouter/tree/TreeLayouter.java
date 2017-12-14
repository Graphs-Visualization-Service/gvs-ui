package gvs.business.logic.layouter.tree;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import gvs.Configuration;
import gvs.business.logic.Session;
import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.tree.TreeVertex;
import gvs.util.Action;

/**
 * Layouts a tree according to the Reingold-Tilford Algorithm which was improved
 * by Walker et al. to accomodate for n-ary trees and by Buchheim et al. to
 * still run in linear time.
 * 
 * For the original algorithm see:
 * {@link https://dl.acm.org/citation.cfm?id=1133017}<br>
 * Our implementation is inspired by:
 * {@link https://github.com/abego/treelayout/blob/master/org.abego.treelayout/src/main/java/org/abego/treelayout/TreeLayout.java}
 * 
 * @author mtrentini
 *
 */
public class TreeLayouter implements ILayouter {

  private static final double GAP_BETWEEN_VERTICES = 40;
  private static final double GAP_BETWEEN_FORESTS = 80;
  private static final double GAP_BETWEEN_LEVEL = 40;
  private static final double VERTEX_HEIGHT = 60;
  private static final int VERTEX_LABEL_MARGIN = 12;
  private static final double UPPER_MARGIN = 20;
  private static final int MIN_VERTEX_WIDTH = Configuration.getAvgPixelPerLetter();

  private TreeLayouterValues values;
  private Bounds bounds;
  private Bounds prevBounds;

  @Override
  public void layout(Session session, boolean useRandomLayout,
      Action callback) {
    session.getGraphs().forEach(g -> layout(g, useRandomLayout, callback));
  }

  @Override
  public void layout(Graph graph, boolean useRandomLayout, Action callback) {
    List<TreeVertex> roots = graph.getVertices().stream()
        .map(v -> (TreeVertex) v).filter(v -> v.isRoot())
        .collect(Collectors.toList());

    if (!graph.getVertices().isEmpty() && roots.isEmpty()) {
      throw new IllegalArgumentException(
          "No Root found. Check ModelBuilder and Persistor!");
    }

    roots.forEach(r -> {
      this.bounds = new Bounds();
      this.values = new TreeLayouterValues();
      firstWalk(r, null);
      secondWalk(r, -values.getPreliminary(r), 0, 0);
      setFinalPosition();
      this.prevBounds = bounds;
      updateBounds();
    });

    prevBounds = new Bounds(); // reset bounds for next tree
  }

  /**
   * Bottom-up traversal of the tree. The position of each node is preliminary.
   * 
   * @param vertex
   */
  private void firstWalk(TreeVertex v, TreeVertex leftSibling) {
    if (v.isLeaf()) {
      if (leftSibling != null) {
        values.setPreliminary(v,
            values.getPreliminary(leftSibling) + getDistance(v, leftSibling));
      }
    } else {
      TreeVertex defaultAncestor = v.getChildren().get(0);
      TreeVertex previousChild = null;
      for (TreeVertex w : v.getChildren()) {
        firstWalk(w, previousChild);
        defaultAncestor = apportion(w, defaultAncestor, previousChild, v);
        previousChild = w;
      }
      executeShifts(v);
      double midpoint = (values.getPreliminary(v.getChildren().get(0)) + values
          .getPreliminary(v.getChildren().get(v.getChildren().size() - 1)))
          / 2.0;
      if (leftSibling != null) {
        values.setPreliminary(v,
            values.getPreliminary(leftSibling) + getDistance(v, leftSibling));
        values.setMod(v, values.getPreliminary(v) - midpoint);

      } else {
        values.setPreliminary(v, midpoint);
      }
    }
  }

  /**
   * Top-down traversal of the tree. Compute all real positions in linear time.
   * The real position of a vertex is its preliminary position plus the
   * aggregated modifier given by the sum of all modifiers on the path from the
   * parent of the vertex to the root.
   * 
   * @param v
   * @param modSum
   * @param depth
   * @param startDepth
   */
  private void secondWalk(TreeVertex v, double modSum, int depth,
      double startDepth) {
    double x = values.getPreliminary(v) + modSum;
    double y = startDepth + (VERTEX_HEIGHT / 2) + UPPER_MARGIN;
    values.getPositions().put(v, new NormalizedPosition(x, y, bounds));

    bounds.updateBounds(v, x, y);

    if (!v.isLeaf()) {
      double nextDepthStart = startDepth + VERTEX_HEIGHT + GAP_BETWEEN_LEVEL;
      for (TreeVertex w : v.getChildren()) {
        secondWalk(w, modSum + values.getMod(v), depth + 1, nextDepthStart);
      }
    }
  }

  private void updateBounds() {
    for (Entry<TreeVertex, NormalizedPosition> entry : values.getPositions()
        .entrySet()) {
      TreeVertex vertex = entry.getKey();
      prevBounds.updateBounds(vertex, vertex.getXPosition(),
          vertex.getYPosition());
    }
  }

  /**
   * Calculates the final position of the vertices. This is needed, because the
   * algorithm sets the root at 0,0. Therefore many vertices in the left subtree
   * have negative x coordinates. This method shift the tree so the left most
   * vertex is at x=0.
   */
  private void setFinalPosition() {
    for (Entry<TreeVertex, NormalizedPosition> entry : values.getPositions()
        .entrySet()) {
      TreeVertex vertex = entry.getKey();
      NormalizedPosition pos = entry.getValue();
      double width = vertex.getLabel().length();
      double x = pos.getX() - width / 2;
      if (prevBounds != null) {
        x += prevBounds.getBoundsRight() + GAP_BETWEEN_FORESTS;
      }
      double y = pos.getY() - VERTEX_HEIGHT / 2;
      vertex.setXPosition(x);
      vertex.setYPosition(y);
    }
  }

  /**
   * Used to traverse the left contour of a subtree.
   * 
   * @param v
   * @return the successor of the vertex on this contour
   */
  private TreeVertex nextLeft(TreeVertex v) {
    if (v.isLeaf()) {
      return values.getThread(v);
    }
    return v.getChildren().get(0);
  }

  /**
   * Used to traverse the right contour of a subtree.
   * 
   * @param v
   * @return the successor of the vertex on this contour
   */
  private TreeVertex nextRight(TreeVertex v) {
    if (v.isLeaf()) {
      return values.getThread(v);
    }
    return v.getChildren().get(v.getChildren().size() - 1);
  }

  private TreeVertex ancestor(TreeVertex insideLeftVertex, TreeVertex parent,
      TreeVertex defaultAncestor) {
    TreeVertex ancestorVertex = values.getAncestor(insideLeftVertex);
    if (isChildOfParent(ancestorVertex, parent)) {
      return ancestorVertex;
    } else {
      return defaultAncestor;
    }
  }

  private boolean isChildOfParent(TreeVertex child, TreeVertex parent) {
    return child.getParent().equals(parent);
  }

  private void moveSubtree(TreeVertex v, TreeVertex w, TreeVertex parent,
      double currentShift) {
    int subtrees = values.getChildNumber(w, parent)
        - values.getChildNumber(v, parent);
    values.setChange(w, values.getChange(w) - currentShift / subtrees);
    values.setShift(w, values.getShift(w) + currentShift);
    values.setChange(v, values.getChange(v) + currentShift / subtrees);
    values.setPreliminary(w, values.getPreliminary(w) + currentShift);
    values.setMod(w, values.getMod(w) + currentShift);
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
   * @param v
   * @param defaultAncestor
   * @param leftSibling
   * @param parentOfV
   * @return the (possibly changed) default ancestor
   */
  private TreeVertex apportion(TreeVertex v, TreeVertex defaultAncestor,
      TreeVertex leftSibling, TreeVertex parentOfV) {
    if (leftSibling == null) {
      return defaultAncestor;
    }
    TreeVertex outsideRightVertex = v;
    TreeVertex insideRightVertex = v;
    TreeVertex insideLeftVertex = leftSibling;
    TreeVertex outsideLeftVertex = parentOfV.getChildren().get(0);

    Double insideRightModSum = values.getMod(insideRightVertex);
    Double outsideRightModSum = values.getMod(outsideRightVertex);
    Double insideLeftModSum = values.getMod(insideLeftVertex);
    Double outsideLeftModSum = values.getMod(outsideLeftVertex);

    TreeVertex nextRight = nextRight(insideLeftVertex);
    TreeVertex nextLeft = nextLeft(insideRightVertex);

    while (nextRight != null && nextLeft != null) {
      insideLeftVertex = nextRight;
      insideRightVertex = nextLeft;
      outsideLeftVertex = nextLeft(outsideLeftVertex);
      outsideRightVertex = nextRight(outsideRightVertex);
      values.setAncestor(outsideRightVertex, v);
      double currentShift = (values.getPreliminary(insideLeftVertex)
          + insideLeftModSum)
          - (values.getPreliminary(insideRightVertex) + insideRightModSum)
          + getDistance(insideLeftVertex, insideRightVertex);

      if (currentShift > 0) {
        moveSubtree(ancestor(insideLeftVertex, parentOfV, defaultAncestor), v,
            parentOfV, currentShift);
        insideRightModSum = insideRightModSum + currentShift;
        outsideRightModSum = outsideRightModSum + currentShift;
      }
      insideLeftModSum = insideLeftModSum + values.getMod(insideLeftVertex);
      insideRightModSum = insideRightModSum + values.getMod(insideRightVertex);
      outsideLeftModSum = outsideLeftModSum + values.getMod(outsideLeftVertex);
      outsideRightModSum = outsideRightModSum
          + values.getMod(outsideRightVertex);

      nextRight = nextRight(insideLeftVertex);
      nextLeft = nextLeft(insideRightVertex);
    }

    if (nextRight != null && nextRight(outsideRightVertex) == null) {
      values.setThread(outsideRightVertex, nextRight);
      values.setMod(outsideRightVertex, values.getMod(outsideRightVertex)
          + insideLeftModSum - outsideRightModSum);
    }

    if (nextLeft != null && nextLeft(outsideLeftVertex) == null) {
      values.setThread(outsideLeftVertex, nextLeft);
      values.setMod(outsideLeftVertex, values.getMod(outsideLeftVertex)
          + insideRightModSum - outsideLeftModSum);
      defaultAncestor = v;
    }
    return defaultAncestor;
  }

  /**
   * Shifts the current subtree of input vertex. When moving a subtree rooted at
   * vertex, only its mod and preliminary x-coordinate are adjusted by the
   * amount of shifting.
   * 
   * @param v
   */
  private void executeShifts(TreeVertex v) {
    double currentShift = 0;
    double currentChange = 0;
    for (TreeVertex w : getChildrenReverse(v)) {
      currentChange += values.getChange(w);
      values.setPreliminary(w, values.getPreliminary(w) + currentShift);
      values.setMod(w, values.getMod(w) + currentShift);
      currentShift += values.getShift(w) + currentChange;
    }
  }

  /**
   * The distance between two vertices includes the gap between the vertices and
   * half of the sizes of the vertices.
   * 
   * @param v
   * @param w
   * @return the distance between vertex v and w
   */
  private double getDistance(TreeVertex v, TreeVertex w) {
    double vertexSize = getVertexWidth(v) + getVertexWidth(w);
    return vertexSize / 2 + GAP_BETWEEN_VERTICES;
  }

  private int getVertexWidth(TreeVertex v) {
    int acctualWidth = v.getLabel().length()
        * Configuration.getAvgPixelPerLetter() + 2 * VERTEX_LABEL_MARGIN;
    int maxWidth = Configuration.getMaxLabelLengthForTree();
    int width = Math.min(acctualWidth, maxWidth);
    return Math.max(width, MIN_VERTEX_WIDTH);
  }

  private List<TreeVertex> getChildrenReverse(TreeVertex v) {
    return Lists.reverse(v.getChildren());
  }

  @Override
  public void takeOverVertexPositions(Graph source, Graph target) {
    // Do nothing. Only relevant for graphs
  }
}
