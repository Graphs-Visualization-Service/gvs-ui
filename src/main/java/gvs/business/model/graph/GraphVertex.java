package gvs.business.model.graph;

import java.util.Observable;

import gvs.Configuration;
import gvs.business.model.IVertex;
import gvs.business.model.styles.GVSStyle;
import gvs.util.FontAwesome.Glyph;

/**
 * Model of a default vertex
 * 
 * @author aegli
 *
 */
public class GraphVertex extends Observable implements IVertex {

  private long id;
  private String label;
  private GVSStyle style;
  private double xPosition;
  private double yPosition;
  private boolean isStable;
  private boolean isUserPositioned;

  private final Glyph icon;
  private final int maxLabelLength;

  public GraphVertex(long id, String label, GVSStyle style, double xPosition,
      double yPosition, Glyph icon) {

    this.id = id;
    this.label = label;
    this.xPosition = xPosition;
    this.yPosition = yPosition;
    this.isStable = false;
    this.icon = icon;
    this.style = style;
    this.maxLabelLength = Configuration.getMaxLabelLengthForGraph();
  }

  public double getXPosition() {
    return xPosition;
  }

  public void updateCoordinates(double xPos, double yPos) {
    xPosition = xPos;
    yPosition = yPos;
    setChanged();
    notifyObservers();
  }

  public void setXPosition(double position) {
    xPosition = position;
  }

  public double getYPosition() {
    return yPosition;
  }

  public void setYPosition(double position) {
    yPosition = position;
  }

  public boolean isStable() {
    return isStable;
  }

  public void setStable(boolean stable) {
    this.isStable = stable;
  }

  public boolean isUserPositioned() {
    return isUserPositioned;
  }

  public void setUserPositioned(boolean userPositioned) {
    this.isUserPositioned = userPositioned;
  }

  public String getLabel() {
    return label;
  }

  public GVSStyle getStyle() {
    return style;
  }

  public long getId() {
    return id;
  }

  public Glyph getIcon() {
    return icon;
  }

  public int getMaxLabelLength() {
    return maxLabelLength;
  }

  @Override
  public boolean isTreeVertex() {
    return false;
  }

  @Override
  public String toString() {
    return String.format("GraphVertex(%s [%f,%f])", label, xPosition,
        yPosition);
  }
}
