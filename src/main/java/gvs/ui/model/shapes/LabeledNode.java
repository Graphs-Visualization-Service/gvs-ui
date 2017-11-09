package gvs.ui.model.shapes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

public class LabeledNode {
  private final Text label = new Text();
  private final Ellipse ellipse = new Ellipse();

  private final StringProperty labelProperty = new SimpleStringProperty();

  private static final String CSS_NODE_LABEL = "node-label";
  private static final String CSS_NODE = "node";

  public LabeledNode(StringProperty labelText, double scaleX, double scaleY) {
    label.textProperty().set(labelText.get());
    label.getStyleClass().add(CSS_NODE_LABEL);
    ellipse.getStyleClass().add(CSS_NODE);
    label.applyCss();
    
    centerLabel(scaleX, scaleY);

    label.toFront();
    ellipse.radiusXProperty().set(label.getLayoutBounds().getWidth() / 2);
    ellipse.radiusYProperty().set(label.getLayoutBounds().getHeight() / 2);
  }
  
  private void centerLabel(double scaleX, double scaleY) {
    label.xProperty().bind(ellipse.centerXProperty()
        .subtract((label.getLayoutBounds().getWidth() / 2) / scaleX));
    label.yProperty().bind(ellipse.centerYProperty()
        .add((label.getLayoutBounds().getHeight() / 2) / scaleY));
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
