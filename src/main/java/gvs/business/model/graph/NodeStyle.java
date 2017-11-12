package gvs.business.model.graph;

import java.util.Collection;
import java.util.HashSet;

public class NodeStyle {
  public enum GVSColor {
    YELLOW("yellow"), GREEN("green"), LIGHTGREEN(
        "lightGreen"), DARKGREEN("darkGreen"), BLUE(
            "blue"), DARKBLUE("darkBlue"), LIGHTBLUE("lightBlue"), RED(
                "red"), LIGHTRED("lightRed"), BLACK(
                    "black"), GRAY("gray"), LIGHTGRAY("lightGray"), STANDARD("standard");

    private String color;

    GVSColor(String color) {
      this.color = color;
    }

    public String getColor() {
      return color;
    }
  }

  public enum GVSLineStyle {
    DOTTED("dotted"), DASHED("dashed"), THROUGH("through");
    private String style;

    GVSLineStyle(String style) {
      this.style = style;
    }

    public String getStyle() {
      return style;
    }
  }

  public enum GVSLineThickness {
    STANDARD("standard"),BOLD("bold"), SLIGHT("slight"), FAT("fat");
    private String thickness;

    GVSLineThickness(String thickness) {
      this.thickness = thickness;
    }

    public String getThickness() {
      return thickness;
    }
  }

  private GVSColor lineColor;
  private GVSLineStyle lineStyle;
  private GVSLineThickness lineThickness;
  private GVSColor fillColor;
  private Collection<GVSColor> darkColors;

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
      this.lineColor = GVSColor.valueOf(linecolor.toUpperCase());
    } catch (Exception e) {
      this.lineColor = GVSColor.STANDARD;
    }
    try {
      this.fillColor = GVSColor.valueOf(fillColor.toUpperCase());
    } catch (Exception e) {
      this.fillColor = GVSColor.STANDARD;
    }
    try {
      this.lineStyle = GVSLineStyle.valueOf(linestyle.toUpperCase());
    } catch (Exception e) {
      this.lineStyle = GVSLineStyle.THROUGH;
    }
    try {
      this.lineThickness = GVSLineThickness.valueOf(lineThickness.toUpperCase());
    } catch (Exception e) {
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
