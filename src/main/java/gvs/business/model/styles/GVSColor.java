package gvs.business.model.styles;

public enum GVSColor {
  STANDARD("standard"), RED("red"), LIGHTRED("lightred"), GREEN(
      "green"), LIGHTGREEN("lightgreen"), DARKGREEN("darkgreen"), BLUE(
          "blue"), LIGHTBLUE("lightblue"), DARKBLUE("darkblue"), YELLOW(
              "yellow"), ORANGE("orange"), BROWN("brown"), BLACK("black"), GRAY(
                  "gray"), LIGHTGRAY("lightgray"), LIGHTVIOLET(
                      "violet"), LIGHTPINK("pink"), LIGHTTURQOISE("turqoise");

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
