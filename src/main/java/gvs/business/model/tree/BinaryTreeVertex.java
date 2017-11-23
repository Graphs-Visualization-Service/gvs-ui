package gvs.business.model.tree;

import gvs.business.model.styles.GVSStyle;
import gvs.util.FontAwesome.Glyph;

public class BinaryTreeVertex extends TreeVertex {

  public BinaryTreeVertex(long id, String label, GVSStyle style,
      boolean isUserPositioned, Glyph icon) {
    super(id, label, style, isUserPositioned, icon);
  }

  public TreeVertex getLeftChild() {
    return getChildren().get(0);
  }

  public TreeVertex getRightChild() {
    return getChildren().get(1);
  }

  public void setLeftChild(TreeVertex child) {
    getChildren().set(0, child);
  }

  public void setRightChild(TreeVertex child) {
    getChildren().set(1, child);
  }

}
