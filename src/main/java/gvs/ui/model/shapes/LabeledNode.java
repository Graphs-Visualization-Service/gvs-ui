package gvs.ui.model.shapes;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;

public class LabeledNode extends Ellipse{
  private Text label = new Text();

  private final StringProperty labelProperty = new SimpleStringProperty();

  public LabeledNode(StringProperty labelText) {
    label.textProperty().set(labelText.get());
    label.getStyleClass().add("node-label");
    label.setFill(Color.BLACK);
    getStyleClass().add("node");
    label.applyCss();
//    label.translateXProperty()
//        .bind(centerXProperty().subtract(label.getLayoutBounds().getWidth()/2));
//    label.translateYProperty()
//        .bind(centerYProperty().add(label.getLayoutBounds().getHeight()/2));
    label.translateXProperty().bind(centerXProperty());
    label.translateYProperty().bind(centerYProperty());
    label.toFront();
    radiusXProperty().set(label.getLayoutBounds().getWidth());
    radiusYProperty().set(label.getLayoutBounds().getHeight());
  }

  public void setText(String text) {
    labelProperty.set(text);
  }
  
  
  public StringProperty labelProperty() {
    return labelProperty;
  }
  

}
