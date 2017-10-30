package gvs.ui.model.graph;

import java.util.Observable;

import gvs.business.model.graph.GraphModel;

/**
 * Model which gives updates to the visualization panel
 * 
 * @author aegli
 *
 */
public class VisualizationGraphModel extends Observable {

  private GraphModel graphModel;
  private boolean draggable = false;
  private boolean isLayouting = false;

  /**
   * Builds default visualization graph model
   *
   */
  public VisualizationGraphModel() {
  }

  /**
   * Sets a graph model
   * 
   * @param pGraphModel
   */
  public void setGraphModel(GraphModel pGraphModel) {
    this.graphModel = pGraphModel;
    setChanged();
    notifyObservers();
  }

  /**
   * Returns displayed graph model
   * 
   * @return
   */
  public GraphModel getGraphModel() {
    return graphModel;
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
