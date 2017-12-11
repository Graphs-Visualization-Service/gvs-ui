package gvs.business.model.styles;

public enum GVSColor {
  STANDARD("standard"), BLACK("black"), WHITE("white"), DARKGREY(
      "darkgrey"), GREY("grey"), LIGHTGREY("lightgrey"), BLUE(
          "blue"), LIGHTBLUE("lightblue"), RED(
              "red"), YELLOW("yellow"), ORANGE("orange"), GREEN("green");

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
