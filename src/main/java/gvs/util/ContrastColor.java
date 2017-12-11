package gvs.util;

import javafx.scene.paint.Color;

/**
 * Represents a nice contrast color.
 * 
 * @author mtrentini
 */
public class ContrastColor {

  /**
   * Return either black or white depending on the brightness of the input
   * color.
   * 
   * @param color
   *          reference color
   * @return either black or white depending on the brightness of the input
   *         color
   */
  public static Color getContrastColor(Color color) {
    double brightness = color.getBrightness();
    if (brightness >= 0.95) {
      return Color.BLACK;
    } else {
      return Color.WHITE;
    }
  }

  /**
   * Return right CSS class depending on the brightness of the input color.
   * 
   * @param color
   *          reference color
   * @return color class
   */
  public static String getContrastColorClass(Color color) {
    double brightness = color.getBrightness();
    if (brightness >= 0.95) {
      return "text-fill-black";
    } else {
      return "text-fill-white";
    }
  }
}
