package gvs.model.styles;

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
