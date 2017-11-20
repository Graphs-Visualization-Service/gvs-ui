package gvs.business.model.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.DefaultVertex;
import gvs.business.model.styles.GVSStyle;
import gvs.interfaces.IVertex;
import gvs.util.FontAwesome.Glyph;

public class TreeVertex extends Observable implements IVertex {
  private long id;
  private String label;
  private GVSStyle style;
  private double xPosition;
  private double yPosition;
  private boolean isUserPositioned;

  private boolean isRoot;
  private TreeVertex parent;
  // values are needed for layouting trees
  private TreeVertex thread;
  private double mod;
  private double prelim;
  private double change;
  private double shift;

  private final List<Long> childIds;
  private final List<TreeVertex> children;

  private final Glyph icon;

  private static final Logger logger = LoggerFactory
      .getLogger(DefaultVertex.class);

  public TreeVertex(long id, String label, GVSStyle style,
      boolean isUserPositioned, Glyph icon) {
    this.id = id;
    this.label = label;
    this.style = style;
    this.isUserPositioned = isUserPositioned;
    this.icon = icon;

    this.childIds = new ArrayList<>();
    this.children = new ArrayList<>();
    logger.info("Instantiated TreeVertex: {}", id);
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public double getXPosition() {
    return xPosition;
  }

  @Override
  public double getYPosition() {
    return yPosition;
  }

  @Override
  public void setXPosition(double xPos) {
    this.xPosition = xPos;
  }

  @Override
  public void setYPosition(double yPos) {
    this.yPosition = yPos;
  }

  /**
   * This property is used in layouting trees {@see TreeLayouter}. It specifies
   * by how much child vertices of a vertex need to be shifted on the x axis.
   * 
   * @return
   */
  public double getMod() {
    return mod;
  }

  public void setMod(double mod) {
    this.mod = mod;
  }

  @Override
  public void updateCoordinates(double xPos, double yPos) {
    this.xPosition = xPos;
    this.yPosition = yPos;
    setChanged();
    notifyObservers();
  }

  @Override
  public boolean isUserPositioned() {
    return isUserPositioned;
  }

  @Override
  public void setUserPositioned(boolean isUserPositioned) {
    this.isUserPositioned = isUserPositioned;
  }

  @Override
  public boolean isStable() {
    // TODO: remove this when IVertex is adapted -> issue #182
    return true;
  }

  @Override
  public void setStable(boolean isStable) {
    // TODO: remove this when IVertex is adapted -> issue #182
  }

  @Override
  public Glyph getIcon() {
    return icon;
  }

  @Override
  public GVSStyle getStyle() {
    return style;
  }

  public boolean isLeaf() {
    return getChildren().isEmpty();
  }

  public List<Long> getChildIds() {
    return childIds;
  }

  public List<TreeVertex> getChildren() {
    return children;
  }

  public boolean isRoot() {
    return isRoot;
  }

  public void setRoot(boolean isRoot) {
    this.isRoot = isRoot;
  }

  public void addChildId(long childId) {
    childIds.add(childId);
  }

  public void addChild(TreeVertex vertex) {
    children.add(vertex);
  }

  public TreeVertex getParent() {
    return parent;
  }

  public void setParent(TreeVertex parent) {
    this.parent = parent;
  }

  public TreeVertex getThread() {
    return thread;
  }

  public double getPrelim() {
    return prelim;
  }

  public void setPrelim(double prelim) {
    this.prelim = prelim;
  }

  public void setThread(TreeVertex thread) {
    this.thread = thread;
  }

  @Override
  public String toString() {
    return String.format("TreeVertex(%s [%f,%f])", label, xPosition, yPosition);
  }

  public void setChange(double change) {
    this.change = change;
  }

  public double getChange() {
    return change;
  }

  public void setShift(double shift) {
    this.shift = shift;
  }

  public double getShift() {
    return shift;
  }

  @Override
  public boolean isTreeVertex() {
    return true;
  }
}
