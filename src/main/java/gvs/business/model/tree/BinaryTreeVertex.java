package gvs.business.model.tree;

import gvs.business.model.styles.GVSStyle;
import gvs.util.FontAwesome.Glyph;

public class BinaryTreeVertex extends TreeVertex {
  private boolean hasLeftChild;
  private boolean hasRightChild;

  public BinaryTreeVertex(long id, String label, GVSStyle style,
      boolean isUserPositioned, Glyph icon) {
    super(id, label, style, isUserPositioned, icon);
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
