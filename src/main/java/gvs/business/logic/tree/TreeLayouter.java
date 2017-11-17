package gvs.business.logic.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.ILayouter;
import gvs.business.model.graph.Graph;
import gvs.interfaces.Action;

@Singleton
public class TreeLayouter implements ILayouter {

  private static final Logger logger = LoggerFactory
      .getLogger(TreeLayouter.class);

  @Override
  public void layoutGraph(Graph currentGraph, boolean b, Action callback) {
    logger.info("Starting to layout tree...");
    if (callback != null) {
      callback.execute();
    }
  }

}
