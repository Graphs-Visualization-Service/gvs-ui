package gvs.interfaces;

import javafx.beans.property.DoubleProperty;

/**
 * Base interface for all vertex View Model.
 * 
 * The View Model contains Properties with are need to create JavaFX Bindings.
 * 
 * @author Michi
 */
public interface IVertexViewModel {

  /**
   * Return the current X position of a vertex.
   * 
   * @return current X position
   */
  DoubleProperty getXProperty();

  /**
   * Return the current Y position of a vertex.
   * 
   * @return current Y position
   */
  DoubleProperty getYProperty();

  /**
   * Set the active flag of a vertex.
   * 
   * @param state
   *          flag value
   */
  void setActive(boolean state);

}
