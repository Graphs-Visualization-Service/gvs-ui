package gvs.business.logic.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.ILayouter;
import gvs.business.model.graph.Graph;
import gvs.business.model.tree.TreeVertex;
import gvs.interfaces.Action;

/**
 * Calculates the position of each TreeVertex according to its depth in the tree
 * and the number of other vertices on the same depth.
 * 
 * @author mtrentini
 *
 */
@Singleton
public class TreeLayouter implements ILayouter {

  private final List<TreeVertex> vertices = new ArrayList<>();
  private final Map<Integer, List<TreeVertex>> depthMap = new HashMap<>();

  private static final int PANEWIDTH = 300;
  private static final int PANEHEIGHT = 100;
  private static final int MARGIN = 5;
  private static final Logger logger = LoggerFactory
      .getLogger(TreeLayouter.class);

  @Override
  public void layoutGraph(Graph currentGraph, boolean b, Action callback) {
    logger.info("Layouting tree...");
    currentGraph.getVertices().forEach(v -> vertices.add((TreeVertex) v));
    sortByDepth(-1, vertices.get(0));
    if (!vertices.isEmpty()) {
      logger.info("Multiple roots detected.");
      // TODO
    }
    calculateCoordinates();
    logger.info("Finished layouting tree.");
    if (callback != null) {
      logger.info("Running callback...");
      callback.execute();
    }
  }

  private void calculateCoordinates() {
    int maxDepth = depthMap.keySet().size() - 1;
    int spacingY = 0;
    if (maxDepth != 0) {
      spacingY = (PANEHEIGHT - 2 * MARGIN) / maxDepth;
    }
    final int finalSpacingY = spacingY;
    depthMap.keySet().forEach(depth -> {
      List<TreeVertex> depthList = depthMap.get(depth);
      int childrenPerDepthLevel = depthList.size();
      int spacingX = (PANEWIDTH - 2 * MARGIN) / (childrenPerDepthLevel + 1);
      for (int i = 0; i < childrenPerDepthLevel; i++) {
        TreeVertex current = depthList.get(i);
        int x = MARGIN + spacingX + i * spacingX;
        int y = depth * finalSpacingY + MARGIN;
        current.updateCoordinates(x, y);
      }
    });
  }

  private void sortByDepth(int parentDepth, TreeVertex vertex) {
    int currentDepth = parentDepth + 1;
    List<TreeVertex> depthList = depthMap.get(currentDepth);
    if (depthList == null) {
      depthList = new ArrayList<>();
      depthMap.put(currentDepth, depthList);
    }
    depthList.add(vertex);
    vertices.remove(vertex);
    if (!vertex.getChildren().isEmpty()) {
      vertex.getChildren().forEach(c -> sortByDepth(currentDepth, c));
    }
  }

}
