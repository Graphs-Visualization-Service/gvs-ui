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

@Singleton
public class TreeLayouter implements ILayouter {

  private Graph currentGraph;

  private final List<TreeVertex> vertices = new ArrayList<>();
  private final Map<Integer, List<TreeVertex>> depthMap = new HashMap<>();

  private final static int PANEWIDTH = 5;
  private final static int PANEHEIGHT = 5;
  private final static int MARGIN = 5;
  private static final Logger logger = LoggerFactory
      .getLogger(TreeLayouter.class);

  @Override
  public void layoutGraph(Graph currentGraph, boolean b, Action callback) {
    logger.info("Layouting tree...");
    this.currentGraph = currentGraph;
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
    int spacingY = (PANEHEIGHT - 2 * MARGIN) / maxDepth;
    depthMap.keySet().forEach(depth -> {
      List<TreeVertex> depthList = depthMap.get(depth);
      int childrenPerDepthLevel = depthList.size();
      int spacingX = (PANEWIDTH - 2 * MARGIN) / (childrenPerDepthLevel + 1);
      for (int i = 0; i < childrenPerDepthLevel; i++) {
        TreeVertex current = depthList.get(i);
        int x = MARGIN + spacingX + i * spacingX;
        int y = depth * spacingY + MARGIN;
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
    vertex.getChildren().forEach(c -> sortByDepth(currentDepth, c));
  }

}
