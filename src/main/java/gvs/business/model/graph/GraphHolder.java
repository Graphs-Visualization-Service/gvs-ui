package gvs.business.model.graph;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class GraphHolder extends Observable {

  private Graph currentGraph;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphHolder.class);

  /**
   * Set Current Graph and notify ui observers.
   * 
   * @param newGraph
   *          udpated graph
   */
  public synchronized void setCurrentGraph(Graph newGraph) {
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
  public synchronized Graph getCurrentGraph() {
    return currentGraph;
  }
}
