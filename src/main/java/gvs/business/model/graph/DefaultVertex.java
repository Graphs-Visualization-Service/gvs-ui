package gvs.business.model.graph;

import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.interfaces.IVertex;
import gvs.util.FontAwesome.Glyph;

/**
 * Model of a default vertex
 * 
 * @author aegli
 *
 */
public class DefaultVertex extends Observable implements IVertex {

  private long id;
  private String label;
  private NodeStyle style;
  private double xPosition;
  private double yPosition;
  private boolean isLayouted;
  private boolean isFixedPositioned;

  private final Glyph icon;

  private static final Logger logger = LoggerFactory
      .getLogger(DefaultVertex.class);

  public DefaultVertex(long id, String label, NodeStyle style, double xPosition,
      double yPosition, Glyph icon) {

    this.id = id;
    this.label = label;
    this.xPosition = xPosition;
    this.yPosition = yPosition;
    this.isLayouted = false;
    this.icon = icon;
    this.style = style;

    if (xPosition > 0 && yPosition > 0) {
      this.isFixedPositioned = true;
      this.isLayouted = true;
    }
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

  public boolean isLayouted() {
    return isLayouted;
  }

  public void setIsLayouted(boolean isLayouted) {
    this.isLayouted = isLayouted;
  }

  public boolean isFixedPositioned() {
    return isFixedPositioned;
  }

  public String getLabel() {
    return label;
  }

  public NodeStyle getStyle() {
    return style;
  }

  public long getId() {
    return id;
  }

  public Glyph getIcon() {
    return icon;
  }
}
