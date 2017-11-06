package gvs.ui.model.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gvs.business.model.graph.NodeStyle;
import javafx.scene.paint.Color;

public class NodeStyleViewModel {

  private Color lineColor;
  private Color fillColor;
  private double lineThickness;
  private List<Double> lineStyle = new ArrayList<>();

  private Map<String, Double> lineThicknesses = new HashMap<>();
  private Map<String, List<Double>> lineStyles = new HashMap<>();

  public NodeStyleViewModel(NodeStyle style) {
    fillMaps();
    this.lineColor = Color.valueOf(style.getLineColor().name());
    this.fillColor = Color.valueOf(style.getFillColor().name());
    this.lineThickness = lineThicknesses.get(style.getLineThickness().name());
    this.lineStyle.addAll(lineStyles.get(style.getLineStyle().name()));
  }

  public Color getLineColor() {
    return lineColor;
  }

  public void setLineColor(Color lineColor) {
    this.lineColor = lineColor;
  }

  public Color getFillColor() {
    return fillColor;
  }

  public void setFillColor(Color fillColor) {
    this.fillColor = fillColor;
  }

  public double getLineThickness() {
    return lineThickness;
  }

  public void setLineThickness(double lineThickness) {
    this.lineThickness = lineThickness;
  }

  public List<Double> getLineStyle() {
    return lineStyle;
  }

  private void fillMaps() {
    lineThicknesses.put("STANDARD", 2d);
    lineThicknesses.put("BOLD", 3d);
    lineThicknesses.put("SLIGHT", 1d);
    lineThicknesses.put("FAT", 8d);

    lineStyles.put("THROUGH", new ArrayList<>());
    lineStyles.put("DASHED", Arrays.asList(10d, 2d));
    lineStyles.put("DOTTED", Arrays.asList(1d, 5d));
  }

}
