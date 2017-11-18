package gvs.business.model.graph;

import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeStyle {

  public enum GVSColor {
    STANDARD("standard"), RED("red"), LIGHTRED("lightred"), GREEN(
        "green"), LIGHTGREEN("lightgreen"), DARKGREEN("darkgreen"), BLUE(
            "blue"), LIGHTBLUE("lightblue"), DARKBLUE("darkblue"), YELLOW(
                "yellow"), ORANGE("orange"), BROWN(
                    "brown"), BLACK("black"), GRAY("gray"), LIGHTGRAY(
                        "lightgray"), LIGHTVIOLET("violet"), LIGHTPINK(
                            "pink"), LIGHTTURQOISE("turqoise");

    private String color;

    GVSColor(String color) {
      this.color = color.toLowerCase();
    }

    public String getColor() {
      return color;
    }

    public static GVSColor byName(String colorName) {
      return valueOf(colorName.toUpperCase());
    }
  }

  public enum GVSLineStyle {
    DOTTED("dotted"), DASHED("dashed"), THROUGH("through");
    private String style;

    GVSLineStyle(String style) {
      this.style = style.toLowerCase();
    }

    public String getStyle() {
      return style;
    }

    public static GVSLineStyle byName(String styleName) {
      return valueOf(styleName.toUpperCase());
    }
  }

  public enum GVSLineThickness {
    STANDARD("standard"), BOLD("bold"), SLIGHT("slight"), FAT("fat");
    private String thickness;

    GVSLineThickness(String thickness) {
      this.thickness = thickness.toLowerCase();
    }

    public String getThickness() {
      return thickness;
    }

    public static GVSLineThickness byName(String ticknessName) {
      return valueOf(ticknessName.toUpperCase());
    }
  }

  private GVSColor lineColor;
  private GVSLineStyle lineStyle;
  private GVSLineThickness lineThickness;
  private GVSColor fillColor;
  private Collection<GVSColor> darkColors;

  private static final Logger logger = LoggerFactory.getLogger(NodeStyle.class);

  public NodeStyle(GVSColor lineColor, GVSLineStyle lineStyle,
      GVSLineThickness lineThickness, GVSColor fillColor) {
    this.lineColor = lineColor;
    this.lineStyle = lineStyle;
    this.lineThickness = lineThickness;
    this.fillColor = fillColor;
    fillDarkColors();
  }

  private void fillDarkColors() {
    darkColors = new HashSet<>();
    darkColors.add(GVSColor.BLACK);
    darkColors.add(GVSColor.DARKBLUE);
    darkColors.add(GVSColor.DARKGREEN);
    darkColors.add(GVSColor.BLUE);
  }

  public NodeStyle(String linecolor, String linestyle, String lineThickness,
      String fillColor) {
    try {
      this.lineColor = GVSColor.byName(linecolor);
    } catch (Exception e) {
      logger.info("Linecolor {} not supported. Use standard", linecolor);
      this.lineColor = GVSColor.STANDARD;
    }
    try {
      this.fillColor = GVSColor.byName(fillColor);
    } catch (Exception e) {
      logger.info("Fillcolor {} not supported. Use standard", fillColor);
      this.fillColor = GVSColor.STANDARD;
    }
    try {
      this.lineStyle = GVSLineStyle.byName(linestyle);
    } catch (Exception e) {
      logger.info("Linestyle {} not supported. Use standard", linestyle);
      this.lineStyle = GVSLineStyle.THROUGH;
    }
    try {
      this.lineThickness = GVSLineThickness.byName(lineThickness);
      System.out.println(lineThickness);
    } catch (Exception e) {
      logger.info("Line Tickness {} not supported. Use standard",
          lineThickness);
      this.lineThickness = GVSLineThickness.STANDARD;
    }

    fillDarkColors();
  }

  public GVSColor getFillColor() {
    return fillColor;
  }

  public void setFillColor(GVSColor fillColor) {
    this.fillColor = fillColor;
  }

  public GVSColor getLineColor() {
    return lineColor;
  }

  public void setLineColor(GVSColor lineColor) {
    this.lineColor = lineColor;
  }

  public GVSLineStyle getLineStyle() {
    return lineStyle;
  }

  public void setLineStyle(GVSLineStyle lineStroke) {
    this.lineStyle = lineStroke;
  }

  public GVSLineThickness getLineThickness() {
    return lineThickness;
  }

  public void setLineThickness(GVSLineThickness lineThickness) {
    this.lineThickness = lineThickness;
  }

  public Collection<GVSColor> getDarkColors() {
    return darkColors;
  }
}
