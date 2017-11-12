package gvs.util;

import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StringSizeCalculator {
  public static Dimension calculate(String s, Font myFont) {
    Text text = new Text(s);
    text.setFont(myFont);
    Bounds tb = text.getBoundsInLocal();
    Rectangle stencil = new Rectangle(tb.getMinX(), tb.getMinY(), tb.getWidth(),
        tb.getHeight());
    Shape intersection = Shape.intersect(text, stencil);
    Bounds ib = intersection.getBoundsInLocal();
    return new Dimension(ib.getWidth(), ib.getHeight());
  }
}
