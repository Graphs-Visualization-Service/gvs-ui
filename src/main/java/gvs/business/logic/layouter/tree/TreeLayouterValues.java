package gvs.business.logic.layouter.tree;

import java.util.HashMap;
import java.util.Map;

import gvs.business.model.tree.TreeVertex;

public class TreeLayouterValues {
  private Map<TreeVertex, Double> mod;
  private Map<TreeVertex, TreeVertex> thread;
  private Map<TreeVertex, Double> preliminary;
  private Map<TreeVertex, Double> change;
  private Map<TreeVertex, Double> shift;
  private Map<TreeVertex, TreeVertex> ancestor;
  private Map<TreeVertex, Integer> childNumber;
  private Map<TreeVertex, NormalizedPosition> positions;

  public TreeLayouterValues() {
    this.mod = new HashMap<>();
    this.thread = new HashMap<>();
    this.preliminary = new HashMap<>();
    this.change = new HashMap<>();
    this.shift = new HashMap<>();
    this.ancestor = new HashMap<>();
    this.childNumber = new HashMap<>();
    this.positions = new HashMap<>();
  }

  public Map<TreeVertex, NormalizedPosition> getPositions() {
    return positions;
  }

  double getMod(TreeVertex vertex) {
    Double d = mod.get(vertex);
    if (d != null) {
      return d;
    } else {
      return 0;
    }
  }

  void setMod(TreeVertex vertex, double d) {
    mod.put(vertex, d);
  }

  TreeVertex getThread(TreeVertex vertex) {
    TreeVertex n = thread.get(vertex);
    if (n != null) {
      return n;
    } else {
      return null;
    }
  }

  void setThread(TreeVertex vertex, TreeVertex threadVertex) {
    thread.put(vertex, threadVertex);
  }

  TreeVertex getAncestor(TreeVertex vertex) {
    TreeVertex n = ancestor.get(vertex);
    if (n != null) {
      return n;
    } else {
      return vertex;
    }
  }

  void setAncestor(TreeVertex vertex, TreeVertex ancestorVertex) {
    ancestor.put(vertex, ancestorVertex);
  }

  double getPreliminary(TreeVertex vertex) {
    Double d = preliminary.get(vertex);
    if (d != null) {
      return d;
    } else {
      return 0;
    }
  }

  void setPreliminary(TreeVertex vertex, double d) {
    preliminary.put(vertex, d);
  }

  double getChange(TreeVertex vertex) {
    Double d = change.get(vertex);
    if (d != null) {
      return d;
    } else {
      return 0;
    }
  }

  void setChange(TreeVertex vertex, double d) {
    change.put(vertex, d);
  }

  double getShift(TreeVertex vertex) {
    Double d = shift.get(vertex);
    if (d != null) {
      return d;
    } else {
      return 0;
    }
  }

  void setShift(TreeVertex vertex, double d) {
    shift.put(vertex, d);
  }

  int getChildNumber(TreeVertex vertex, TreeVertex parentVertex) {
    Integer nr = childNumber.get(vertex);
    if (nr == null) {
      int i = 1;
      for (TreeVertex child : parentVertex.getChildren()) {
        childNumber.put(child, i++);
      }
      nr = childNumber.get(vertex);
    }
    return nr;
  }

}
