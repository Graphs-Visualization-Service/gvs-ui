package gvs.business.model.styles;

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
