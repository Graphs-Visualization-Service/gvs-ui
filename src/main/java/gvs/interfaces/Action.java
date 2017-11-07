package gvs.interfaces;

import java.util.function.Consumer;

/**
 * Functional Interface like {@link Consumer} but with no arguments.
 */
@FunctionalInterface
public interface Action {

  /**
   * Execute callback function.
   */
  void execute();
}
