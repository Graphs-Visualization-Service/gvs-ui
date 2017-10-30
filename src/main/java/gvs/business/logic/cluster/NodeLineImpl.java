package gvs.business.logic.cluster;

import java.math.BigInteger;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.interfaces.IBinaryNode;

class NodeLineImpl implements Line {

  private final int depth;
  private final int distance;
  private Vector<IBinaryNode> mArr;
  /**
   * The width in percentage that can be used for Layouting.
   */
  private final double mWidth;
  /**
   * The size of steps for the x-direction of nodes. Remark: This is the
   * distance from the edge to the first resp. last Node. Between two nodes are
   * two such steps!
   */
  private final double mNodeStepDistance;
  private int mSize;
  private Logger mTreeContLogger = null;

  NodeLineImpl(int pDepth, double pWidth, int pDistance) {
    depth = pDepth;
    mWidth = pWidth;
    distance = pDistance;
    mNodeStepDistance = mWidth / Math.pow(2, depth) / 2;
    setmArr(new Vector<IBinaryNode>());
    getmArr().setSize(((int) Math.pow(2, depth)));
    // TODO check Logger replacement
    // mTreeContLogger =
    // gvs.common.Logger.getInstance().getTreeControllerLogger();
    setmTreeContLogger(LoggerFactory.getLogger(NodeLineImpl.class));
    getmTreeContLogger().debug("NodeLineImpl::NodeLineImpl():"
        + "  DISTANCE=" + distance
        + "  DEPTH=" + depth);
  }

  public Cluster getNextCluster(int pSearchStartPos) {
    int startPos = -1;
    int endPos = -1;
    int len = getmArr().size();
    for (int i = pSearchStartPos; i < len; i++) {
      startPos = -1;
      for (int j = i; j < len; j++) {
        if (getmArr().get(j) != null) {
          startPos = j;
          break;
        }
      }
      if (startPos == -1) { // no Nodes
        return null;
      }
      endPos = len - 1;
      for (int j = startPos + 1; j < len; j++) {
        int nulls = 0;
        // are there Nodes in the Range of DISTANCE?
        for (int k = 0; k < distance; k++) {
          if (getmArr().get(Math.min(j + k, getmArr().size() - 1)) == null) {
            nulls++;
          }
        }
        if (nulls == distance) { // no Nodes inbetween DISTANCE: End
          // reached
          endPos = j - 1;
          break;
        }
      }
      if (endPos - startPos < 1) {
        continue;
      }
      // we found a Cluster:
      break;
    }
    if (endPos - startPos > 0) {
      return new NodeClusterImpl(this, startPos, endPos);
    } else {
      return null;
    }
  }

  public int length() {
    return getmArr().size();
  }

  public void add(IBinaryNode pNode) {
    double xpos = pNode.getXPosition();
    int index = (int) (xpos / (getmNodeStepDistance() * 2));
    getmArr().set(index, pNode);
    String str = String.format(
        "NodeLineImpl::add(): >%s<  X-Pos: %f  Index: %d\n",
        pNode.getNodeLabel(), pNode.getXPosition(), index);
    getmTreeContLogger().debug(str);
    mSize++;
  }

  public void print() {
    getmTreeContLogger().debug("NodeLineImpl::print(): ");
    int len = getmArr().size();
    for (int i = 0; i < len; i++) {
      IBinaryNode node = getmArr().get(i);
      String str = String.format("  [%03d]: %s ", i,
          (getmArr().get(i) != null) ? node.toString() : "null");
      getmTreeContLogger().debug(str);
    }
    if (ClusterSplitterGVS.getmPanel() != null) {
      ClusterSplitterGVS.getmPanel().repaint();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
    }
  }

  public int getNodeDistance() {
    return distance;
  }

  public BigInteger getBigIntegerInterpretation() {
    // Interpret pLine as Binary-Polynom:
    int nrOfBits = getmArr().size();
    int nrOfBytes = nrOfBits / 8 + (((nrOfBits % 8) > 0) ? 1 : 0);
    byte[] bytes = new byte[nrOfBytes];
    for (int i = 0; i < nrOfBits; i++) {
      if (getmArr().get(i) != null) {
        byte actualByte = bytes[bytes.length - 1 - i / 8];
        actualByte = (byte) (actualByte | (byte) (1 << (i % 8)));
        bytes[bytes.length - 1 - i / 8] = actualByte;
      }
    }
    BigInteger bigInt = new BigInteger(bytes);
    getmTreeContLogger().debug("BigInteger = " + bigInt);
    return bigInt;
  }

  int getSize() {
    return mSize;
  }

  Vector<IBinaryNode> getmArr() {
    return mArr;
  }

  void setmArr(Vector<IBinaryNode> mArr) {
    this.mArr = mArr;
  }

  double getmNodeStepDistance() {
    return mNodeStepDistance;
  }

  Logger getmTreeContLogger() {
    return mTreeContLogger;
  }

  void setmTreeContLogger(Logger mTreeContLogger) {
    this.mTreeContLogger = mTreeContLogger;
  }

}
