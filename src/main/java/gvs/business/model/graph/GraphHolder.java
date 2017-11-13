package gvs.business.model.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class GraphHolder extends Observable {

  private Graph currentGraph;
  private final List<Graph> graphs;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphHolder.class);

  public GraphHolder() {
    this.graphs = new ArrayList<>();
  }

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

  /**
   * Add a graph.
   * 
   * @param graph
   *          new graph
   */
  public synchronized void addGraph(Graph graph) {
    this.graphs.add(graph);
  }

  /**
   * Remove a graph.
   * 
   * @param graph
   *          graph to delete
   */
  public synchronized void removeGraph(Graph graph) {
    this.graphs.remove(graph);
  }

  public synchronized List<Graph> getGraphs() {
    return graphs;
  }

}
