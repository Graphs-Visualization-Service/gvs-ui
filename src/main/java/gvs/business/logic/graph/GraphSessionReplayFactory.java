package gvs.business.logic.graph;

import java.util.List;

import gvs.business.model.graph.Graph;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface GraphSessionReplayFactory {

  /**
   * Construct a graph session controller with dependencies, which are not
   * automatically injectable.
   * 
   * @param graphSessionController
   *          related graph session controller
   * @param graphs
   *          graphs to step through
   * @return new instance
   */
  GraphSessionReplay create(GraphSessionController graphSessionController,
      List<Graph> graphs);
}
