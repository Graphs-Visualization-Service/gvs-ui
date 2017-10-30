package gvs.ui.model.graph;

import java.util.Observable;
import java.util.Observer;

import gvs.business.model.graph.DefaultVertex;
import gvs.interfaces.IVertex;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * Contains JavaFX Properties which are used for bidirectional bindings.
 * 
 * @author Michi
 */
public class VertexViewModel implements Observer {

  private final IVertex vertex;
  private final DoubleProperty xProperty;
  private final DoubleProperty yProperty;
  private final BooleanProperty activeProperty;
  private final StringProperty labelProperty;

  // TODO move to the place, where circle is created
  private static final int DEFAULT_HEIGHT = 40;
  private static final int BOUNDARY_OFFSET_X = 30;
  private static final int BOUNDARY_OFFSET_Y = 25;

  /**
   * Create a new DefaultVertexViewModel with the corresponding Vertex
   * 
   * @param vertex
   *          JavaFX independent vertex representation
   */
  public VertexViewModel(IVertex vertex) {
    this.vertex = vertex;
    this.xProperty = new SimpleDoubleProperty();
    this.yProperty = new SimpleDoubleProperty();
    this.activeProperty = new SimpleBooleanProperty();
    this.labelProperty = new SimpleStringProperty();

    this.vertex.addObserver(this);

    xProperty.addListener(this::xProperyListener);
    yProperty.addListener(this::yProperyListener);
  }

  /**
   * Inform the business logic {@link DefaultVertex} about changes, made in the
   * UI. (e.g. drag vertex)
   * 
   * @param observable
   *          observable value
   * @param oldValue
   *          value before the change
   * @param newValue
   *          value after the change
   */
  private void xProperyListener(ObservableValue<? extends Number> observable,
      Number oldValue, Number newValue) {
    double newX = (double) newValue;
    vertex.setXPosition(newX);
  }

  /**
   * Inform the business logic {@link DefaultVertex} about changes, made in the
   * UI. (e.g. drag vertex)
   * 
   * @param observable
   *          observable value
   * @param oldValue
   *          value before the change
   * @param newValue
   *          value after the change
   */
  private void yProperyListener(ObservableValue<? extends Number> observable,
      Number oldValue, Number newValue) {
    double newY = (double) newValue;
    vertex.setYPosition(newY);
  }

  /**
   * Create the binding from the business logic {@link DefaultVertex} to the
   * presentation {@link VertexViewModel} properties.
   * 
   * As soon as any {@link DefaultVertex} field is updated, the corresponding
   * property will be notified.
   * 
   * @param o
   *          updated {@link DefaultVertex} object
   * @param arg
   *          additional arguments
   */
  @Override
  public void update(Observable o, Object arg) {
    DefaultVertex updatedVertex = (DefaultVertex) o;
    this.xProperty.set(updatedVertex.getXPosition());
    this.yProperty.set(updatedVertex.getYPosition());
  }

  public DoubleProperty getXProperty() {
    return xProperty;
  }

  public DoubleProperty getYProperty() {
    return yProperty;
  }

  public void setActive(boolean state) {
    this.activeProperty.set(state);
  }

}
