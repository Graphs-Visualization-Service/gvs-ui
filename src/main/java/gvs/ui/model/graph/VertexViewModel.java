package gvs.ui.model.graph;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.DefaultVertex;
import gvs.business.model.graph.IconVertex;
import gvs.interfaces.IVertex;
import gvs.util.Dimension;
import gvs.util.FontAwesome;
import gvs.util.StringSizeCalculator;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 * Contains JavaFX Properties which are used for bidirectional bindings.
 * 
 * @author Michi
 */
public class VertexViewModel implements Observer {
  private double dragOriginalSceneX;
  private double dragOriginalSceneY;

  private final IVertex vertex;
  private final Label node;
  private final DoubleProperty centerXPropery = new SimpleDoubleProperty();
  private final DoubleProperty centerYPropery = new SimpleDoubleProperty();

  private static final Logger logger = LoggerFactory
      .getLogger(VertexViewModel.class);

  /**
   * Create a new DefaultVertexViewModel with the corresponding Vertex
   * 
   * @param vertex
   *          JavaFX independent vertex representation
   */
  public VertexViewModel(IVertex vertex) {
    this.node = new Label();
    if (vertex instanceof IconVertex) {
      logger.info("Creating VertexViewModel with an icon");
      node.setGraphic(FontAwesome.createLabel(vertex.getIcon()));
    }
    node.setText(vertex.getLabel());
    node.setCursor(Cursor.HAND);
    node.setBackground(new Background(
        new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

    // bidirectional connection
    this.vertex = vertex;
    this.vertex.addObserver(this);
    node.layoutXProperty().addListener(this::xProperyListener);
    node.layoutYProperty().addListener(this::yProperyListener);
    updateCoordinates();
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
    if (Math.abs((double) oldValue - (double) newValue) <= 0.000001) {
      double newX = (double) newValue;
      vertex.setXPosition(newX);
    }
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
    if (Math.abs((double) oldValue - (double) newValue) <= 0.000001) {
      double newY = (double) newValue;
      vertex.setYPosition(newY);
    }
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
    // Hand updates over to JavaFX Thread
    Platform.runLater(() -> {
      // logger level debug, because this will happen very often when layouting
      logger.debug("Updating VertexViewModel coordinates");
      updateCoordinates();
    });
  }

  private void updateCoordinates() {
    updateCoordinates(vertex.getXPosition(), vertex.getYPosition());
  }
  
  private void updateCoordinates(double x, double y) {
    Dimension dim = StringSizeCalculator.calculate(node.getText(),
        node.getFont());
    node.setLayoutX(x - (dim.getWidth() / 2));
    node.setLayoutY(y - (dim.getHeight() / 2));
    centerXPropery.set(x);
    centerYPropery.set(y);
  }

  public void draw(ScalableContentPane p) {
    logger.info("Drawing VertexViewModel.");
    p.getContentPane().getChildren().add(node);
    dragSupport(p);
  }

  private void dragSupport(ScalableContentPane p) {
    logger.info("Adding drag support on VertexViewModel.");
    node.setOnMousePressed(e -> {
      dragOriginalSceneX = e.getSceneX();
      dragOriginalSceneY = e.getSceneY();
    });

    node.setOnMouseDragged(e -> {
      // logger level debug, because this will happen very often
      logger.debug("Mouse drag on VertexViewModel detected.");
      Label l = (Label) (e.getSource());
      l.setCursor(Cursor.HAND);

      double offsetX = (e.getSceneX() - dragOriginalSceneX)
          / p.getContentScaleTransform().getX();
      double offsetY = (e.getSceneY() - dragOriginalSceneY)
          / p.getContentScaleTransform().getY();

      double newX = centerXPropery.get() + offsetX;
      double newY = centerYPropery.get() + offsetY;

      newX = checkXBoundaries(l, newX, p);
      newY = checkYBoundaries(l, newY, p);

      updateCoordinates(newX, newY);
//      l.setLayoutX(newX);
//      l.setLayoutY(newY);

      dragOriginalSceneX = e.getSceneX();
      dragOriginalSceneY = e.getSceneY();
    });
  }

  private double checkXBoundaries(Label node, double newX,
      ScalableContentPane p) {
    double minimum = node.getWidth()/2;
    double maximumX = p.getBoundsInLocal().getWidth();

    if (newX < minimum) {
      newX = minimum;
    }
    if (newX > maximumX) {
      newX = maximumX;
    }
    return newX;
  }

  private double checkYBoundaries(Label node, double newY,
      ScalableContentPane p) {
    double minimum = node.getHeight()/2;
    double maximumY = p.getBoundsInLocal().getHeight();

    if (newY < minimum) {
      newY = minimum;
    }
    if (newY > maximumY) {
      newY = maximumY;
    }
    return newY;
  }

  public Label getNode() {
    return node;
  }

  public DoubleProperty centerXProperty() {
    return centerXPropery;
  }

  public DoubleProperty centerYProperty() {
    return centerYPropery;
  }

}
