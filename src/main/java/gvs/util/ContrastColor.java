package gvs.util;

import javafx.scene.paint.Color;

public class ContrastColor {
  /**
   * Return either black or white depending on the brightness of the input
   * color.
   * 
   * @param color
   * @return either black or white depending on the brightness of the input
   *         color
   */
  public static Color getContrastColor(Color color) {
    double brightness = color.getBrightness();
    return brightness >= 0.95 ? Color.BLACK : Color.WHITE;
  }
}
