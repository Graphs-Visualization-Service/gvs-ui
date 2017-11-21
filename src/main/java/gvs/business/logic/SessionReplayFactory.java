package gvs.business.logic;

import gvs.util.Action;

/**
 * Factory interface for Guice
 * 
 * @see https://github.com/google/guice/wiki/AssistedInject
 * @author Michi
 *
 */
public interface SessionReplayFactory {

  /**
   * Construct a graph session controller with dependencies, which are not
   * automatically injectable.
   * 
   * @param graphSessionController
   *          related graph session controller
   * @param callback
   *          callback which is executed after replay has finished
   * @param startGraphId
   *          id of the graph to start with
   * @return new instance
   */
  SessionReplay create(Session graphSessionController, Action callback,
      int startGraphId);
}
