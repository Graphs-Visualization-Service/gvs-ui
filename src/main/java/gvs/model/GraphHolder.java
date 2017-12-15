package gvs.model;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * Holds the current graph. State class for all observers of the current graph.
 * 
 * @author mwieland
 *
 */
@Singleton
public class GraphHolder extends Observable {

  private Graph currentGraph;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphHolder.class);

  /**
   * Set Current Graph and notify ui observers.
   * 
   * @param newGraph
   *          updated graph
   */
  public void setCurrentGraph(Graph newGraph) {
    logger.info("Setting current graph and notify observers.");
    this.currentGraph = newGraph;

    setChanged();
    notifyObservers();
  }

  /**
   * Returns current graph, which is displayed
   * 
   * @return current graph
   */
  public Graph getCurrentGraph() {
    return currentGraph;
  }
}
