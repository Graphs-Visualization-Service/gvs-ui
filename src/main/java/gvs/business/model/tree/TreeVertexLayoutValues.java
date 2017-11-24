package gvs.business.model.tree;

public class TreeVertexLayoutValues {
  private TreeVertex thread;
  private double mod;
  private double preliminary;
  private double change;
  private double shift;

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

  /**
   * A thread points to the next vertex in a contour. See Reingold - Tilford
   * Algorithm.
   * 
   * @return reference to the next vertex in a contour
   */
  public TreeVertex getThread() {
    return thread;
  }

  /**
   * Get the preliminary x coordinate of the vertex.
   * 
   * @return
   */
  public double getPreliminary() {
    return preliminary;
  }

  /**
   * Set the preliminary x coordinate of the vertex. Only this value and the mod
   * value are adjusted in the bottom-up traversal of the tree, whenever a
   * subtree is shifted. This is needed so the algorithm works in linear time!
   * 
   * @param preliminary
   */
  public void setPreliminary(double preliminary) {
    this.preliminary = preliminary;
  }

  public void setThread(TreeVertex thread) {
    this.thread = thread;
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
}
