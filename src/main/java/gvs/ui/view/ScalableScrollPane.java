package gvs.ui.view;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.transform.Scale;

/**
 * Visual pane which zooms according to its content.
 * 
 * The pane does only zoom out. (e.g. scale factor < 1)
 * 
 * @author mwieland
 */
public class ScalableScrollPane extends ScrollPane {

  private final Group contentGroup;
  private final Scale scale;

  private static final int MAX_SCALE = 1;

  public ScalableScrollPane() {

    setPadding(new Insets(20));
    setPannable(false);
    setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    setPrefWidth(USE_PREF_SIZE);
    setPrefHeight(USE_PREF_SIZE);

    scale = new Scale(1.0, 1.0, 0, 0);

    contentGroup = new Group();
    contentGroup.scaleXProperty().bind(scale.xProperty());
    contentGroup.scaleYProperty().bind(scale.yProperty());
    setContent(contentGroup);

    needsLayoutProperty().addListener((o, old, newVal) -> {
      zoomToContent();
    });
  }

  public void zoomToContent() {
    double viewPortWidth = getViewportBounds().getWidth();
    double viewPortHeight = getViewportBounds().getHeight();

    double contentWidth = contentGroup.prefWidth(-1);
    double contentHeight = contentGroup.prefHeight(-1);

    // center
    double centerX = (viewPortWidth - contentWidth) / 2;
    double centerY = (viewPortHeight - contentHeight) / 2;
    contentGroup.relocate(centerX, centerY);

    // zoom
    if (contentWidth > 0 && contentHeight > 0) {

      double scaleX = viewPortWidth / contentWidth;
      double scaleY = viewPortHeight / contentHeight;

      double minScale = Math.min(scaleX, scaleY);
      minScale = Math.min(MAX_SCALE, minScale);

      scale.setX(minScale);
      scale.setY(minScale);
      contentGroup.resize(contentWidth / scaleX, contentHeight / scaleY);
    }
  }

  public void addNodes(Node... nodes) {
    addListener(nodes);
    contentGroup.getChildren().addAll(nodes);
  }

  private void addListener(Node... nodes) {
    final ChangeListener<Bounds> boundsListener = (o, oldVal, newVal) -> {
      setNeedsLayout(false);
      contentGroup.requestLayout();
      requestLayout();
    };

    final ChangeListener<Number> layoutListener = (o, oldVal, newVal) -> {
      setNeedsLayout(false);
      contentGroup.requestLayout();
      requestLayout();
    };

    for (Node node : nodes) {
      node.boundsInLocalProperty().addListener(boundsListener);
      node.layoutXProperty().addListener(layoutListener);
      node.layoutYProperty().addListener(layoutListener);
      node.applyCss();
    }
  }

  public void clear() {
    contentGroup.getChildren().clear();
  }

  public double getScaleValue() {
    return scale.getX();
  }

  @Override
  protected void layoutChildren() {
    super.layoutChildren();
  }

  @Override
  protected double computePrefWidth(double d) {
    double result = 1;
    return result;
  }

  @Override
  protected double computePrefHeight(double d) {
    double result = 1;
    return result;
  }

  @Override
  protected double computeMinWidth(double d) {
    double result = getInsets().getLeft() + getInsets().getRight() + 1;
    return result;
  }

  @Override
  protected double computeMinHeight(double d) {
    double result = getInsets().getTop() + getInsets().getBottom() + 1;
    return result;
  }
}
