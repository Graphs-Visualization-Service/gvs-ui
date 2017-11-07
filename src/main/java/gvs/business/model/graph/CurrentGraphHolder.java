package gvs.business.model.graph;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;


import gvs.business.logic.graph.GraphSessionController;

@Singleton
public class CurrentGraphHolder extends Observable {

  private Graph currentGraph;

  private static final Logger logger = LoggerFactory
      .getLogger(CurrentGraphHolder.class);
  
  public void setCurrentGraph(Graph newGraph) {
    logger.info("Setting current graph and notifying observers.");
    this.currentGraph = newGraph;
    setChanged();
    notifyObservers();
  }

  /**
   * Returns current session, which is held by the model
   * 
   * @return session controller
   */
  public Graph getCurrentGraph() {
    return currentGraph;
  }
  
}
