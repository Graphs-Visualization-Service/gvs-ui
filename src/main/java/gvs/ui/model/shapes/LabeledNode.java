package gvs.ui.model.shapes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

public class LabeledNode {
  private Text label = new Text();
  private Ellipse ellipse = new Ellipse();

  private final StringProperty labelProperty = new SimpleStringProperty();

  public LabeledNode(StringProperty labelText, double scaleX, double scaleY) {
    label.textProperty().set(labelText.get());
    label.getStyleClass().add("node-label");
    ellipse.getStyleClass().add("node");
    label.applyCss();
    label.xProperty().bind(ellipse.centerXProperty()
        .subtract((label.getLayoutBounds().getWidth() / 2) / scaleX));
    label.yProperty().bind(ellipse.centerYProperty()
        .add((label.getLayoutBounds().getHeight() / 2) / scaleY));

    label.toFront();
    ellipse.radiusXProperty().set(label.getLayoutBounds().getWidth() / 2);
    ellipse.radiusYProperty().set(label.getLayoutBounds().getHeight() / 2);
  }

  public void setText(String text) {
    labelProperty.set(text);
  }

  public Ellipse getEllipse() {
    return ellipse;
  }

  public StringProperty labelProperty() {
    return labelProperty;
  }

  public Text getLabel() {
    return label;
  }

}
