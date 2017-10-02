package gvs.ui.tree.layout;

import gvs.interfaces.IBinaryNode;
import gvs.ui.tree.model.BinaryNode;
import gvs.ui.tree.model.TreeModel;

import java.util.Iterator;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets positions of a received tree construct
 * 
 * @author aegli
 *
 */
public class TreeLayoutController {

  private Vector<IBinaryNode> sortedNodes = null;
  private Logger treeContLogger = null;
  private BinaryNode rootNode = null;
  private TreeModel myModel = null;

  private double maxHeight = 0;
  private final double maxDimensionHeight = 90;
  /**
   * Width which can be used for Nodes in %.
   */
  private final double maxDimensionWidth = 95;
  private double minHeightPerc = 0;
  /**
   * Distance between two sibling Nodes in % of total Width.
   */
  private double levelDistance = 0;

  private Vector<IBinaryNode> rootNodes;
  private Vector<IBinaryNode> splitNodes;
  private int numberOfRoots = 0;
  private double geteilteDim = 1;
  private int globalcounter = 0;
  private double offset = 0;

  /**
   * Builds an instance of TreeLayoutController
   *
   */
  public TreeLayoutController() {
    // TODO check Logger replacement
    // this.treeContLogger =
    // gvs.common.Logger.getInstance().getTreeControllerLogger();
    this.treeContLogger = LoggerFactory.getLogger(TreeLayoutController.class);
    treeContLogger.info("Starting tree layout controller");
  }

  /**
   * Elements of a model will be set for layouting
   * 
   * @param pModel
   */
  @SuppressWarnings("unchecked")
  public void setElements(TreeModel pModel) {
    treeContLogger.info(
        "Tree LayoutController has new elements detected, start layouting procedure");
    myModel = pModel;
    rootNodes = new Vector<IBinaryNode>();

    if (myModel.getRootNode() != null) {
      this.sortedNodes = new Vector<IBinaryNode>();
      this.rootNode = (BinaryNode) myModel.getRootNode();
      this.splitNodes = myModel.getNodes();

      prepareTreeBuild(splitNodes);
      setNodeYPositions();
    } else {
      Iterator it = myModel.getNodes().iterator();
      while (it.hasNext()) {
        searchRootNode((IBinaryNode) it.next());
      }

      Iterator it2 = myModel.getNodes().iterator();
      while (it2.hasNext()) {
        IBinaryNode isRoot = ((IBinaryNode) it2.next());
        if (!isRoot.hasParent()) {
          rootNodes.add(isRoot);
          numberOfRoots++;
        }
      }
      // TODO refactor naming
      this.geteilteDim = numberOfRoots;

      Iterator it3 = rootNodes.iterator();
      while (it3.hasNext()) {
        sortedNodes = new Vector<IBinaryNode>();
        splitNodes = new Vector<IBinaryNode>();

        rootNode = (BinaryNode) it3.next();
        addNodesToSplittedVector(rootNode);

        prepareTreeBuild(splitNodes);

        offset = globalcounter * (maxDimensionWidth / geteilteDim);
        globalcounter++;
        setNodeYPositions();
      }
    }

  }

  // Sets y position of each node, depends on treelevel
  // TODO this is X-Position !? (23.03.08/tl)
  private void setNodeYPositions() {
    treeContLogger
        .debug("setNodeYPositions(): Tree LayoutController set node positions");
    double lastFoundPosition = 0;
    boolean isFound = true;
    int nodeCounter = 0;

    Iterator it = sortedNodes.iterator();
    while (it.hasNext()) {
      BinaryNode temp = (BinaryNode) it.next();

      if (temp.getMyTreeLevel() == 0) {
        temp.setYPosition(+9);
      } else {
        temp.setYPosition(minHeightPerc * temp.getMyTreeLevel());
      }

      levelDistance = (maxDimensionWidth
          / Math.pow(2, temp.getMyTreeLevel() + 1));
      treeContLogger.debug(
          "Tree-Level: " + temp.getMyTreeLevel() + " = " + levelDistance);
      double relativePosition = (temp.getMyTreePosition()
          - Math.pow(2, temp.getMyTreeLevel()) + 1);

      do {
        nodeCounter++;
        if (nodeCounter > Math.pow(2, temp.getMyTreeLevel())) {
          if (nodeCounter == temp.getMyTreePosition()) {
            if (lastFoundPosition == 0) {
              relativePosition = Math.pow(2, temp.getMyTreeLevel());
              relativePosition = levelDistance + 2 * levelDistance
                  + lastFoundPosition;
              lastFoundPosition = relativePosition;
              temp.setXPosition(relativePosition / geteilteDim + offset);
              isFound = true;
            } else {
              relativePosition = 2 * levelDistance + lastFoundPosition;
              lastFoundPosition = relativePosition;
              temp.setXPosition(relativePosition / geteilteDim + offset);
              isFound = true;
            }
          } else {
            if (lastFoundPosition == 0) {
              relativePosition = Math.pow(2, temp.getMyTreeLevel());
              relativePosition = levelDistance + 2 * levelDistance
                  + lastFoundPosition;
              lastFoundPosition = relativePosition;
              isFound = false;
            } else {
              relativePosition = 2 * levelDistance + lastFoundPosition;
              lastFoundPosition = relativePosition;
              isFound = false;
            }
          }
        } else {
          if (nodeCounter == temp.getMyTreePosition()) {
            lastFoundPosition = 0;
            relativePosition = levelDistance;
            temp.setXPosition(relativePosition / geteilteDim + offset);
            isFound = true;
          } else {
            lastFoundPosition = 0;
            relativePosition = relativePosition * levelDistance;
            isFound = false;
          }
        }
      } while (!isFound);
    }
  }

  // Prepare model vector for layouting, check dimension
  @SuppressWarnings("unchecked")
  private void prepareTreeBuild(Vector splitNodes) {
    maxHeight = countNodes(rootNode, 0);
    minHeightPerc = maxDimensionHeight / (maxHeight);

    setNodesXPosition(rootNode, 0, 1);
    sortEntrysShortestDuration(splitNodes);
  }

  // When called as countNodes(root,0), this will compute the
  // max of the depths of all the leaves in the tree to which root
  // points. When called recursively, the depth parameter gives
  // the depth of the node, and the routine returns the max of the
  // depths of the leaves in the subtree to which node points.
  // In each recursive call to this routine, depth goes up by one.
  private int countNodes(IBinaryNode root, int depth) {
    if (root == null) {
      // The tree is empty. Return 0.
      return 0;
    } else if (root.getLeftChild() == null && root.getRightChild() == null) {
      return depth;
    } else {
      int leftMax = countNodes(root.getLeftChild(), depth + 1);
      int rightMax = countNodes(root.getRightChild(), depth + 1);
      if (leftMax > rightMax) {
        return leftMax;
      } else {
        return rightMax;
      }
    }
  }

  // Calculates x position of each node in current model
  // TODO this is Y-Position !? (23.03.08/tl)
  private void setNodesXPosition(IBinaryNode pRoot, int pDepth, int pPosition) {
    treeContLogger
        .debug("Tree LayoutController is seting depth of child nodes");
    if (pRoot == null) {
    } else if (pRoot.getLeftChild() == null && pRoot.getRightChild() == null) {
      pRoot.setMyTreeLevel(pDepth);
      pRoot.setMyTreePosition(pPosition);
    } else {
      pRoot.setMyTreeLevel(pDepth);
      pRoot.setMyTreePosition(pPosition);

      setNodesXPosition(pRoot.getLeftChild(), pDepth + 1, pPosition * 2);
      setNodesXPosition(pRoot.getRightChild(), pDepth + 1, pPosition * 2 + 1);
    }
  }

  // Sorts node entries in order of their tree position
  private void sortEntrysShortestDuration(Vector<IBinaryNode> pNodes) {
    treeContLogger.debug("Tree LayoutController is sorting nodes");
    Vector<IBinaryNode> origEntrys = new Vector<IBinaryNode>(pNodes);
    Vector<IBinaryNode> searchArea = new Vector<IBinaryNode>(pNodes);
    Iterator itSearch = origEntrys.iterator();
    while (itSearch.hasNext()) {
      BinaryNode searchEntry = (BinaryNode) itSearch.next();
      searchArea.remove(searchEntry);
      Iterator itArea = searchArea.iterator();
      boolean smallest = true;
      while (itArea.hasNext()) {
        BinaryNode areaEntry = (BinaryNode) itArea.next();
        if (searchEntry.getMyTreePosition() > areaEntry.getMyTreePosition()) {
          smallest = false;
          break;
        }
      }
      if (smallest) {
        sortedNodes.add(searchEntry);
        origEntrys.remove(searchEntry);
        break;
      }
    }
    if (origEntrys.size() >= 1) {
      sortEntrysShortestDuration(origEntrys);
    }
  }

  // When a collection of nodes arrives without declaration of a root
  // Search root node
  private void searchRootNode(IBinaryNode n) {
    if (n.getLeftChild() != null) {
      n.getLeftChild().hasParent(true);
      searchRootNode(n.getLeftChild());
    }
    if (n.getRightChild() != null) {
      n.getRightChild().hasParent(true);
      searchRootNode(n.getRightChild());
    }
  }

  // TODO rewrite comment... what did they mean??
  // When more than one root in a collection arrives, split nodes in order
  // of roots
  private void addNodesToSplittedVector(IBinaryNode root) {
    if (root == null) {
    } else {
      splitNodes.add(root);
      addNodesToSplittedVector(root.getLeftChild());
      addNodesToSplittedVector(root.getRightChild());
    }
  }

  public Vector<IBinaryNode> getSortedNodes() {
    return sortedNodes;
  }

  public double getMaxDimensionWidth() {
    return maxDimensionWidth;
  }

}
