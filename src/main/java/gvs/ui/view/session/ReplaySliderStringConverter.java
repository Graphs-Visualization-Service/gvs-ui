package gvs.ui.view.session;

import javafx.util.StringConverter;

/**
 * String converter for the tick labels of the speed slider.
 * 
 * @author mwieland
 */
public class ReplaySliderStringConverter extends StringConverter<Double> {

  private static final double SLIDER_SLOW = 0.0;
  private static final double SLIDER_MEDIUM = 0.5;
  private static final double SLIDER_DEFAULT = 1.0;
  private static final double SLIDER_FAST = 1.5;
  private static final double SLIDER_FASTEST = 2.0;

  @Override
  public String toString(Double number) {
    if (number == SLIDER_SLOW) {
      return "slow";
    } else if (number <= SLIDER_MEDIUM) {
      return "medium";
    } else if (number == SLIDER_DEFAULT) {
      return "default";
    } else if (number <= SLIDER_FAST) {
      return "fast";
    } else {
      return "fastest";
    }
  }

  @Override
  public Double fromString(String s) {
    switch (s) {
    case "slow":
      return SLIDER_SLOW;
    case "medium":
      return SLIDER_MEDIUM;
    case "default":
      return SLIDER_DEFAULT;
    case "fast":
      return SLIDER_FAST;
    case "fastest":
      return SLIDER_FASTEST;
    default:
      return SLIDER_DEFAULT;
    }
  }

}
