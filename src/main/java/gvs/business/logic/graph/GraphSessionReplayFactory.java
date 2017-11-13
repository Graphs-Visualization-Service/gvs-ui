package gvs.business.logic.graph;

import gvs.interfaces.Action;

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
   * @param callback
   *          callback which is executed after replay has finished
   * @return new instance
   */
  SessionReplay create(Session graphSessionController, Action callback);
}
