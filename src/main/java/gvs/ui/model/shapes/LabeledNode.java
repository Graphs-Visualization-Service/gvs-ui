package gvs.ui.model.shapes;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.shape.Ellipse;

public class LabeledNode extends Ellipse{
  private Label label = new Label();

  private final StringProperty labelProperty = new SimpleStringProperty();

  public LabeledNode() {
    label.textProperty().bindBidirectional(labelProperty);
    label.translateXProperty()
        .bind(centerXProperty().subtract(label.widthProperty().divide(2)));
    label.translateYProperty()
        .bind(centerYProperty().add(label.heightProperty().divide(2)));
    label.toFront();
    radiusXProperty().bind(label.widthProperty());
    radiusYProperty().bind(label.heightProperty());
  }

  public void setText(String text) {
    labelProperty.set(text);
  }
  
  
  public StringProperty labelProperty() {
    return labelProperty;
  }
  

}
