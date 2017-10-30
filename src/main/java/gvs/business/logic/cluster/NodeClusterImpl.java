package gvs.business.logic.cluster;

public class NodeClusterImpl implements Cluster {

  private NodeLineImpl mLine;
  private int mStartPos;
  private int mEndPos;

  public NodeClusterImpl(NodeLineImpl pLine) {
    mLine = pLine;
  }

  public NodeClusterImpl(NodeLineImpl pLine, int pStartPos, int pEndPos) {
    this(pLine);
    mStartPos = pStartPos;
    mEndPos = pEndPos;
    print();
  }

  public final int getLeftNodePos() {
    return mStartPos;
  }

  public final int getRightNodePos() {
    return mEndPos;
  }

  public final int length() {
    int nodesOfCluster = 0;
    for (int i = mStartPos; i <= mEndPos; i++) {
      if (mLine.getmArr().get(i) != null) {
        nodesOfCluster++;
      }
    }
    return nodesOfCluster;
  }

  /**
   * Move left node.
   */
  public final void moveLeftNode() {
    int leftPost = mStartPos - 1;
    mLine.getmArr().set(leftPost, mLine.getmArr().get(mStartPos));
    mLine.getmArr().set(mStartPos, null);
    mLine.getmArr().get(leftPost)
        .setXPosition((leftPost * 2 + 1) * mLine.getmNodeStepDistance());
    while (mLine.getmArr().get(++mStartPos) == null) {
      // do nothing?
    }
    mLine.print();
  }

  /**
   * Move right node.
   */
  public final void moveRightNode() {
    int rightPos = mEndPos + 1;
    mLine.getmArr().set(rightPos, mLine.getmArr().get(mEndPos));
    mLine.getmArr().set(mEndPos, null);
    mLine.getmArr().get(rightPos)
        .setXPosition((rightPos * 2 + 1) * mLine.getmNodeStepDistance());
    while (mLine.getmArr().get(--mEndPos) == null) {
    }
    mLine.print();
  }

  /**
   * print
   */
  public final void print() {
    mLine.getmTreeContLogger()
        .debug("NodeClusterImpl: " + mStartPos + "/" + mEndPos);
  }

}
