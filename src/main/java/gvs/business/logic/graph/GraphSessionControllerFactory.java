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
public interface GraphSessionControllerFactory {

  /**
   * Construct a graph session controller with dependencies, which are not
   * automatically injectable.
   * 
   * @param sessionId
   *          id of the session
   * @param sessionName
   *          name of the session
   * @param graphs
   *          list of graphs
   * @return new instance
   */
  GraphSessionController create(long sessionId, String sessionName,
      List<Graph> graphs);
}
