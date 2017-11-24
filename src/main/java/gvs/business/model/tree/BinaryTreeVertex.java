package gvs.business.model.tree;

import gvs.business.model.styles.GVSStyle;
import gvs.util.FontAwesome.Glyph;

public class BinaryTreeVertex extends TreeVertex {
  private boolean hasLeftChild;
  private boolean hasRightChild;

  public BinaryTreeVertex(long id, String label, GVSStyle style,
      boolean isUserPositioned, Glyph icon) {
    super(id, label, style, isUserPositioned, icon);
    getChildren().add(new TreeVertex(-1, label, style, isUserPositioned, icon));
    getChildren().add(new TreeVertex(-1, label, style, isUserPositioned, icon));
    getChildIds().add(-1l);
    getChildIds().add(-1l);
  }

  public TreeVertex getLeftChild() {
    if (hasLeftChild) {
      return getChildren().get(0);
    }
    return null;
  }

  public TreeVertex getRightChild() {
    if (hasRightChild) {
      return getChildren().get(1);
    }
    return null;
  }

  public void setLeftChildId(long id) {
    getChildIds().set(0, id);
    hasLeftChild = true;
  }

  public void setRightChildId(long id) {
    getChildIds().set(1, id);
    hasRightChild = true;
  }

  public long getLeftChildId() {
    if (hasLeftChild) {
      return getChildIds().get(0);
    }
    return -1;
  }

  public long getRightChildId() {
    if (hasRightChild) {
      return getChildIds().get(1);
    }
    return -1;
  }

  public void setLeftChild(TreeVertex child) {
    getChildren().set(0, child);
    hasLeftChild = true;
  }

  public void setRightChild(TreeVertex child) {
    getChildren().set(1, child);
    hasRightChild = true;
  }

  public boolean isLeftChild() {
    TreeVertex parent = getParent();
    if (parent != null) {
      return parent.getChildren().indexOf(this) == 0;
    }
    return false;
  }

  public boolean isRightChild() {
    TreeVertex parent = getParent();
    if (parent != null) {
      return parent.getChildren().indexOf(this) == 1;
    }
    return false;
  }

}
