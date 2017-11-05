package gvs.business.logic.graph;

import java.util.List;

import gvs.business.model.graph.Graph;
import gvs.interfaces.CallBackFunction;

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
   * @param c 
   * @return new instance
   */
  GraphSessionReplay create(GraphSessionController graphSessionController,
      List<Graph> graphs, CallBackFunction c);
}
