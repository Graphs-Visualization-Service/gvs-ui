package gvs.ui.model.graph;

import java.util.Observable;

import gvs.business.model.graph.Graph;

/**
 * Model which gives updates to the visualization panel
 * 
 * @author aegli
 *
 */
public class VisualizationGraphModel extends Observable {

  private Graph graph;
  private boolean draggable = false;
  private boolean isLayouting = false;

  /**
   * Sets a graph model
   * 
   * @param graph
   */
  public void setGraph(Graph graph) {
    this.graph = graph;
    setChanged();
    notifyObservers();
  }

  /**
   * Returns displayed graph model
   * 
   * @return
   */
  public Graph getGraph() {
    return graph;
  }

  /**
   * Checks whether model is draggable
   * 
   * @return
   */
  public boolean isDraggable() {
    return draggable;
  }

  /**
   * Sets whether graph is draggable
   * 
   * @param draggable
   */
  public void setDragging(boolean draggable) {
    this.draggable = draggable;
  }

  /**
   * Sets whether graph is layouted
   * 
   * @param isLayouting
   */
  public void setLayouting(boolean isLayouting) {
    this.isLayouting = isLayouting;
  }

  /**
   * Returns if graph will be layouted
   * 
   * @return
   */
  public boolean isLayouting() {
    return isLayouting;
  }
}
