package gvs.ui.tree.view;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import gvs.interfaces.IBinaryNode;

/**
 * A Line with nodes which might be clustered. An abstract example of a line
 * with a cluster: ">OOOOO<" should finally become: ">O O O O O<"
 */
interface Line {
  /**
   * Returns the next cluster beginning from pStartPosition.
   * 
   * @param pStartPosition
   * @return The next cluster. null if none exist.
   */
  Cluster getNextCluster(int pStartPosition);

  /**
   * @return Length of the whole line.
   */
  int length();

  /**
   * @return The minimal allowed distance between two nodes.
   */
  int getNodeDistance();

  /**
   * @return A bigInteger representing the line as bit-pattern.
   */
  BigInteger getBigIntegerInterpretation();

}

/**
 * A cluster of codes.
 */
interface Cluster {

  int getLeftNodePos();

  int getRightNodePos();

  /**
   * Moves the leftmost node one position to the left.
   */
  void moveLeftNode();

  /**
   * Moves the rightmost node one position to the right.
   */
  void moveRightNode();

  /**
   * @return The number of nodes of the (remaining) cluster.
   */
  int length();
}

class CharArrayLineImpl implements Line {

  private final int DISTANCE;
  private char[] mArr;

  CharArrayLineImpl(char[] pArr) {
    this(pArr, 1);
  }

  CharArrayLineImpl(char[] pArr, int pDistance) {
    setmArr(pArr);
    DISTANCE = pDistance;
    print();
  }

  public static void main(String[] args) {
    CharArrayLineImpl line = null;
    CharArrayClusterImpl cluster = null;

    line = new CharArrayLineImpl("  OOO  ".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster.getStartPos() == 2);
    assert (cluster.getEndPos() == 4);

    line = new CharArrayLineImpl(" OOO".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster.getStartPos() == 1);
    assert (cluster.getEndPos() == 3);

    line = new CharArrayLineImpl("OOO ".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster.getStartPos() == 0);
    assert (cluster.getEndPos() == 2);

    line = new CharArrayLineImpl(" OO".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster.getStartPos() == 1);
    assert (cluster.getEndPos() == 2);

    line = new CharArrayLineImpl("OO ".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster.getStartPos() == 0);
    assert (cluster.getEndPos() == 1);

    line = new CharArrayLineImpl("O ".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster == null);

    line = new CharArrayLineImpl(" O".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster == null);

    line = new CharArrayLineImpl(" O ".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(0);
    System.out.println(cluster);
    assert (cluster == null);

    line = new CharArrayLineImpl("OO OO".toCharArray());
    cluster = (CharArrayClusterImpl) line.getNextCluster(1);
    System.out.println(cluster);
    assert (cluster.getStartPos() == 3);
    assert (cluster.getEndPos() == 4);

  }

  public Cluster getNextCluster(int pSearchStartPos) {
    int startPos = -1;
    int endPos = -1;
    for (int i = pSearchStartPos; i < getmArr().length; i++) {
      startPos = -1;
      for (int j = i; j < getmArr().length; j++) {
        if (getmArr()[j] != ' ') {
          startPos = j;
          break;
        }
      }
      if (startPos == -1) { // no Nodes
        return null;
      }
      endPos = getmArr().length - 1;
      for (int j = startPos + 1; j < getmArr().length; j++) {
        if (getmArr()[j] == ' ') {
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
      return new CharArrayClusterImpl(this, startPos, endPos);
    } else {
      return null;
    }
  }

  public int length() {
    return getmArr().length;
  }

  void print() {
    System.out.print(" ");
    for (int i = 0; i < length(); i++) {
      System.out.print(i % 10);
    }
    System.out.println("");
    System.out.print(">");
    System.out.print(getmArr());
    System.out.println("<");
  }

  public int getNodeDistance() {
    return DISTANCE;
  }

  public BigInteger getBigIntegerInterpretation() {
    // Interpret pLine as Binary-Polynom:
    int nrOfBits = getmArr().length;
    int nrOfBytes = nrOfBits / 8 + (((nrOfBits % 8) > 0) ? 1 : 0);
    byte[] bytes = new byte[nrOfBytes];
    for (int i = 0; i < nrOfBits; i++) {
      if (getmArr()[i] != ' ') {
        byte actualByte = bytes[bytes.length - 1 - i / 8];
        actualByte = (byte) (actualByte | (byte) (1 << (i % 8)));
        bytes[bytes.length - 1 - i / 8] = actualByte;
      }
    }
    BigInteger bigInt = new BigInteger(bytes);
    System.out.println("BigInteger = " + bigInt);
    return bigInt;
  }

  char[] getmArr() {
    return mArr;
  }

  void setmArr(char[] mArr) {
    this.mArr = mArr;
  }

} // End of class CharArrayClusterImpl

//TODO create own file and add public modifier
class CharArrayClusterImpl implements Cluster {

  private CharArrayLineImpl mLine;
  /**
   * The Begin of the Cluster. This Value may change as the Cluster becomes
   * smaller.
   */
  private int mStartPos;
  /**
   * The End of the Cluster. This Value may change as the Cluster becomes
   * smaller.
   */
  private int mEndPos;

  public CharArrayClusterImpl(CharArrayLineImpl pLine) {
    mLine = pLine;
  }

  public CharArrayClusterImpl(CharArrayLineImpl pLine, int pStartPos,
      int pEndPos) {
    this(pLine);
    mStartPos = pStartPos;
    mEndPos = pEndPos;
    print();
  }

  public int getLeftNodePos() {
    return mStartPos;
  }

  public int getRightNodePos() {
    return mEndPos;
  }

  public void moveLeftNode() {
    mLine.getmArr()[mStartPos - 1] = mLine.getmArr()[mStartPos];
    mLine.getmArr()[mStartPos] = ' ';
    mStartPos++;
    mLine.print();
  }

  public void moveRightNode() {
    mLine.getmArr()[mEndPos + 1] = mLine.getmArr()[mEndPos];
    mLine.getmArr()[mEndPos] = ' ';
    mEndPos--;
    mLine.print();
  }

  public int length() {
    return mEndPos - mStartPos + 1;
  }

  void print() {
    System.out.println("CharArrayClusterImpl: " + mStartPos + "/" + mEndPos);
  }

  int setStartPos(int pStartPos) {
    int oldValue = mStartPos;
    mStartPos = pStartPos;
    return oldValue;
  }

  int getStartPos() {
    return mStartPos;
  }

  int setEndPos(int pEndPos) {
    int oldValue = mEndPos;
    mEndPos = pEndPos;
    return oldValue;
  }

  int getEndPos() {
    return mEndPos;
  }

}

/*
 * do Cluster-Counter = 0 for each cluster from left to right cluster-counter++
 * while cluster-lenght > 1 move leftmost and rightmost node edge by one
 * position if possible. do while cluster-counter > 0
 * 
 */

public class ClusterSplitter {

  int split(Line pLine) {

    int leftBorder = 0; // max. allowed position from left side
    int rightBorder = pLine.length() - 1; // max. allowed position from
    // right side
    int clusterCounter;
    HashMap<BigInteger, Object> hashMap = new HashMap<BigInteger, Object>();

    do {

      // Was there already the same line ? : no Solution!
      BigInteger bigInt = pLine.getBigIntegerInterpretation();
      if (hashMap.put(bigInt, null) != null) {
        return -1; // no Solution: abort ! :-(
      }

      clusterCounter = 0;
      int actualPosition = 0;
      Cluster cluster;

      while ((cluster = pLine.getNextCluster(actualPosition)) != null) {
        clusterCounter++;

        // Separation of exactly one Node (typically the one in the
        // middle):
        while (cluster.length() > 1) {
          int actions = 0;
          int clusterLeftPos = cluster.getLeftNodePos();
          if (clusterLeftPos > leftBorder) {
            cluster.moveLeftNode();
            actions++;
            if ((clusterLeftPos - 1) == leftBorder) {
              leftBorder += pLine.getNodeDistance() + 1;
            }
          }
          int clusterRightPos = cluster.getRightNodePos();
          if ((clusterRightPos != pLine.length() - 1) && cluster.length() > 1) {
            cluster.moveRightNode();
            actions++;
          }
          if ((clusterRightPos + 1) == rightBorder) {
            rightBorder -= pLine.getNodeDistance() + 1;
          }
          if (actions == 0) {
            return -2; // no Solution: abort ! :-(
          }
        }

        // Verification of borders (if a node is attached to a border,
        // the border will change towards the middle.
        int leftPos = cluster.getLeftNodePos();
        if (leftPos == leftBorder) {
          leftBorder += pLine.getNodeDistance() + 1;
        }
        actualPosition = leftPos + 1;
        int rightPos = cluster.getRightNodePos();
        if (rightPos == rightBorder) {
          rightBorder -= pLine.getNodeDistance() + 1;
        }

      }
    } while (clusterCounter > 0); // As long as there was in this Loop
    // minimum
    // one Cluster in this Line.
    return 0;

  } // split()

  public static void main(String[] args) {
    ClusterSplitter splitter = new ClusterSplitter();
    CharArrayLineImpl line = null;
    System.out.println("=== Next Line: ==================================");
    line = new CharArrayLineImpl(" OOOOO   ".toCharArray());
    splitter.split(line);
    System.out.println("=== Next Line: ==================================");
    line = new CharArrayLineImpl("   OOOOO ".toCharArray());
    splitter.split(line);
    System.out.println("=== Next Line: ==================================");
    line = new CharArrayLineImpl("OOOOO    ".toCharArray());
    splitter.split(line);
    System.out.println("=== Next Line: ==================================");
    line = new CharArrayLineImpl("    OOOOO".toCharArray());
    splitter.split(line);
    System.out.println("=== Next Line: ==================================");
    line = new CharArrayLineImpl("  OO OOO ".toCharArray());
    splitter.split(line);

  } // main()

} // class ClusterSplitter

/*
 * Session-Log:
 * 
 * 0123456 > OOO < CharArrayClusterImpl: 2/4
 * gvs.visualization.tree.view.CharArrayClusterImpl@130c19b 0123 > OOO<
 * CharArrayClusterImpl: 1/3
 * gvs.visualization.tree.view.CharArrayClusterImpl@1f6a7b9 0123 >OOO <
 * CharArrayClusterImpl: 0/2
 * gvs.visualization.tree.view.CharArrayClusterImpl@7d772e 012 > OO<
 * CharArrayClusterImpl: 1/2
 * gvs.visualization.tree.view.CharArrayClusterImpl@11b86e7 012 >OO <
 * CharArrayClusterImpl: 0/1
 * gvs.visualization.tree.view.CharArrayClusterImpl@35ce36 01 >O < null 01 > O<
 * null 012 > O < null 01234 >OO OO< CharArrayClusterImpl: 3/4
 * gvs.visualization.tree.view.CharArrayClusterImpl@757aef === Next Line:
 * ================================== 012345678 > OOOOO < CharArrayClusterImpl:
 * 1/5 012345678 >O OOOO < 012345678 >O OOO O < 012345678 >O OO OO < 012345678
 * >O O OOO < CharArrayClusterImpl: 4/6 012345678 >O O OO O < 012345678 >O O O
 * OO < CharArrayClusterImpl: 6/7 012345678 >O O O O O< === Next Line:
 * ================================== 012345678 > OOOOO < CharArrayClusterImpl:
 * 3/7 012345678 > O OOOO < 012345678 > O OOO O< 012345678 > OO OO O< 012345678
 * > OO O OO< CharArrayClusterImpl: 7/8 012345678 > OO OO O<
 * CharArrayClusterImpl: 2/3 012345678 > O O OO O< CharArrayClusterImpl: 5/6
 * 012345678 > O OO O O< CharArrayClusterImpl: 3/4 012345678 > OO O O O<
 * CharArrayClusterImpl: 1/2 012345678 >O O O O O< === Next Line:
 * ================================== 012345678 >OOOOO < CharArrayClusterImpl:
 * 0/4 012345678 >OOOO O < 012345678 >OOO OO < 012345678 >OO OOO < 012345678 >O
 * OOOO < CharArrayClusterImpl: 2/5 012345678 >O OOO O < 012345678 >O OO OO <
 * 012345678 >O O OOO < CharArrayClusterImpl: 4/6 012345678 >O O OO O <
 * 012345678 >O O O OO < CharArrayClusterImpl: 6/7 012345678 >O O O O O< ===
 * Next Line: ================================== 012345678 > OOOOO<
 * CharArrayClusterImpl: 4/8 012345678 > O OOOO< 012345678 > OO OOO< 012345678 >
 * OOO OO< 012345678 > OOOO O< CharArrayClusterImpl: 3/6 012345678 > O OOO O<
 * 012345678 > O OO OO< 012345678 > OO O OO< CharArrayClusterImpl: 7/8 012345678
 * > OO OO O< CharArrayClusterImpl: 2/3 012345678 > O O OO O<
 * CharArrayClusterImpl: 5/6 012345678 > O OO O O< CharArrayClusterImpl: 3/4
 * 012345678 > OO O O O< CharArrayClusterImpl: 1/2 012345678 >O O O O O<
 * 
 */
