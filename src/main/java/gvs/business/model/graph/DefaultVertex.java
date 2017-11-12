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

  private long id = 0;
  private String label = null;
  private NodeStyle style;
  private boolean isFixed = false;
  private boolean isRelative = false;
  private double xPosition = 0;
  private double yPosition = 0;
  private Glyph icon;

  private static final Logger logger = LoggerFactory
      .getLogger(DefaultVertex.class);

  public DefaultVertex(long pId, String pLabel, NodeStyle style, Glyph icon) {
    this.id = pId;
    this.label = pLabel;
    this.style = style;
    this.isRelative = false;
    this.icon = icon;
  }

  public DefaultVertex(long pId, String pLabel, NodeStyle style,
      double pXPosition, double pYPosition, Glyph icon) {
    this(pId, pLabel, style, icon);
    this.xPosition = pXPosition;
    this.yPosition = pYPosition;
    this.isRelative = true;
  }

  public DefaultVertex(long pId, String pLabel, NodeStyle style,
      double pXPosition, double pYPosition, boolean pIsRelative, Glyph icon) {
    this(pId, pLabel, style, icon);
    this.xPosition = pXPosition;
    this.yPosition = pYPosition;
    this.isRelative = pIsRelative;
  }

  public double getXPosition() {
    return xPosition;
  }

  public void setXPosition(double position) {
    xPosition = position;
    setChanged();
    notifyObservers();
  }

  public double getYPosition() {
    return yPosition;
  }

  public void setYPosition(double position) {
    yPosition = position;
    setChanged();
    notifyObservers();
  }

  public boolean isRelative() {
    return isRelative;
  }

  public String getLabel() {
    return label;
  }

  public NodeStyle getStyle() {
    return style;
  }

  public boolean isFixedPosition() {
    return isFixed;
  }

  public void setFixedPosition(boolean pIsFixed) {
    isFixed = pIsFixed;
  }

  public long getId() {
    return id;
  }

  public Glyph getIcon() {
    return icon;
  }
}
