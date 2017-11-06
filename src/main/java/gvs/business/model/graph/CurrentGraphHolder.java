package gvs.business.model.graph;

import java.util.Observable;

import com.google.inject.Singleton;

@Singleton
public class CurrentGraphHolder extends Observable {

  private Graph currentGraph;
  
  public void setCurrentGraph(Graph newGraph) {
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
