package gvs.ui.view;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

/**
 * Visual pane which zooms according to its content.
 * 
 * @author mwieland
 */
public class ScalableScrollPane extends ScrollPane {

  private final Group contentGroup;
  private final Scale scale;

  public ScalableScrollPane() {

    setPadding(new Insets(20));
    setStyle("-fx-background-color: red");
    setPannable(false);
    setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    scale = new Scale(1.0, 1.0, 0, 0);

    contentGroup = new Group();
    contentGroup.setAutoSizeChildren(true);
    contentGroup.scaleXProperty().bind(scale.xProperty());
    contentGroup.scaleYProperty().bind(scale.yProperty());
    contentGroup.boundsInParentProperty().addListener((o, old, newVal) -> {
      zoomToContent();
    });
    setContent(contentGroup);
  }

  public void zoomToContent() {
    double contentWidth = contentGroup.getBoundsInLocal().getWidth();
    double contentHeight = contentGroup.getBoundsInLocal().getHeight();

    double viewPortWidth = getViewportBounds().getWidth();
    double viewPortHeight = getViewportBounds().getHeight();

    if (contentWidth > 0 && contentHeight > 0) {

      double scaleX = viewPortWidth / contentWidth;
      double scaleY = viewPortHeight / contentHeight;

      double minScale = Math.min(scaleX, scaleY);
      scale.setX(minScale);
      scale.setY(minScale);
      contentGroup.relocate((viewPortWidth - contentWidth) / 2,
          (viewPortHeight - contentHeight) / 2);
    }
  }

  public void addNodes(Node... nodes) {
    contentGroup.getChildren().addAll(nodes);
  }

  public void clear() {
    contentGroup.getChildren().clear();
  }

  public double getScaleValue() {
    return scale.getX();
  }
}
