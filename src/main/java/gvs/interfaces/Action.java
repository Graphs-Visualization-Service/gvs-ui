package gvs.interfaces;

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
