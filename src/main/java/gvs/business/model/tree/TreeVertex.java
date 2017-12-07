package gvs.business.model.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import gvs.Configuration;
import gvs.business.model.IVertex;
import gvs.business.model.styles.GVSStyle;
import gvs.util.FontAwesome.Glyph;

/**
 * Model of a tree vertex. It represents a parent or a child of a tree. For
 * non-existing child vertices use {@link LeafVertex}.
 * 
 * @author mtrentini
 *
 */

public class TreeVertex extends Observable implements IVertex {
  private long id;
  private String label;
  private GVSStyle style;
  private double xPosition;
  private double yPosition;
  private boolean isUserPositioned;

  private boolean isRoot;
  private TreeVertex parent;

  private final List<Long> childIds;
  private final List<TreeVertex> children;

  private final Glyph icon;
  private final int maxLabelLength;

  public TreeVertex(long id, String label, GVSStyle style,
      boolean isUserPositioned, Glyph icon) {
    this.id = id;
    this.label = label;
    this.style = style;
    this.isUserPositioned = isUserPositioned;
    this.icon = icon;
    this.maxLabelLength = Configuration.getMaxLabelLengthForTree();

    this.childIds = new ArrayList<>();
    this.children = new ArrayList<>();
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public String getLabel() {
    return label;
  }

  /**
   * The real x coordinate of the vertex. It is calculated by the preliminary x
   * coordinate plus the sum of all modifiers from the parent of this vertex to
   * the root.
   */
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
  public Glyph getIcon() {
    return icon;
  }

  @Override
  public GVSStyle getStyle() {
    return style;
  }

  public int getMaxLabelLength() {
    return maxLabelLength;
  }

  @Override
  public boolean isTreeVertex() {
    return true;
  }

  // A TreeVertex is never a leaf. Only LeafVertices are leafs.
  public boolean isLeaf() {
    return false;
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

  @Override
  public String toString() {
    return String.format("TreeVertex(%s [%f,%f])", label, xPosition, yPosition);
  }

}
