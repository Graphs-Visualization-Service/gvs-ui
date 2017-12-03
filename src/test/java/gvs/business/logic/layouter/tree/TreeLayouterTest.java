package gvs.business.logic.layouter.tree;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import gvs.business.model.Graph;
import gvs.business.model.IEdge;
import gvs.business.model.IVertex;
import gvs.business.model.tree.LeafVertex;
import gvs.business.model.tree.TreeVertex;

class TreeLayouterTest {
  private Graph tree;
  private TreeLayouter layouter;

  @BeforeEach
  void setUp() throws Exception {
    layouter = new TreeLayouter();
    List<IVertex> vertices = new ArrayList<>();
    List<IEdge> edges = new ArrayList<>();
    tree = new Graph("test graph", vertices, edges);
  }

  @Test
  void testLayoutGraph() {
    TreeVertex root = buildTree();
    root.setRoot(true);
    layouter.layout(tree, false, null);
    Map<Long, TreeVertex> layoutedVertices = new HashMap<>();
    tree.getVertices()
        .forEach(v -> layoutedVertices.put(v.getId(), (TreeVertex) v));
    // Vertex 2 is right child of Vertex 1 i.e. should be positions to the right
    assertTrue(layoutedVertices.get(2L).getXPosition()
        - layoutedVertices.get(1L).getXPosition() > 2);

    // Vertex 5 is left child of Vertex 4 i.e. should be positions to the left
    assertTrue(layoutedVertices.get(4L).getXPosition()
        - layoutedVertices.get(5L).getXPosition() > 2);
  }

  @Test
  void testLayoutWithNoRoots() {
    buildTree();
    Executable layoutTreeWithoutRoot = () -> layouter.layout(tree, false, null);
    assertThrows(IllegalArgumentException.class, layoutTreeWithoutRoot);
    assertTrue(tree.getVertices().stream()
        .allMatch(v -> v.getXPosition() == 0 && v.getYPosition() == 0));
  }

  private TreeVertex buildTree() {
    TreeVertex one = new TreeVertex(1, "1", null, false, null);
    TreeVertex two = new TreeVertex(2, "2", null, false, null);
    TreeVertex three = new TreeVertex(3, "3", null, false, null);
    TreeVertex four = new TreeVertex(4, "4", null, false, null);
    TreeVertex five = new TreeVertex(5, "5", null, false, null);
    LeafVertex l1 = new LeafVertex("l1");
    LeafVertex l2 = new LeafVertex("l2");
    LeafVertex l3 = new LeafVertex("l3");
    LeafVertex l4 = new LeafVertex("l4");
    LeafVertex l5 = new LeafVertex("l5");
    LeafVertex l6 = new LeafVertex("l6");
    tree.getVertices().add(one);
    tree.getVertices().add(two);
    tree.getVertices().add(three);
    tree.getVertices().add(four);
    tree.getVertices().add(five);
    setParentChildRelations(one, l1);
    setParentChildRelations(one, two);
    setParentChildRelations(two, three);
    setParentChildRelations(two, four);
    setParentChildRelations(three, l2);
    setParentChildRelations(three, l3);
    setParentChildRelations(four, five);
    setParentChildRelations(four, l4);
    setParentChildRelations(five, l5);
    setParentChildRelations(five, l6);
    return one;
  }

  private void setParentChildRelations(TreeVertex parent, TreeVertex child) {
    parent.addChild(child);
    child.setParent(parent);
  }

}
