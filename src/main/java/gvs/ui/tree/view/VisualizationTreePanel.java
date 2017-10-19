package gvs.ui.tree.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.common.Configuration;
import gvs.interfaces.IBinaryNode;
import gvs.interfaces.INode;
import gvs.interfaces.IVisualizationTreePanel;
import gvs.ui.tree.layout.TreeLayoutController;
import gvs.ui.tree.model.TreeModel;
import gvs.ui.tree.model.VisualizationTreeModel;

/**
 * Is responsible for the visualization of a model and its components
 * 
 * @author aegli
 *
 */
public class VisualizationTreePanel extends JPanel
    implements Observer, IVisualizationTreePanel {

  private Logger treeContLogger = null;
  private static final long serialVersionUID = 1L;
  private static final int PIXELLENGTH = 12;
  private static final int HUNDREDPERCENT = 100;

  private VisualizationTreeModel visualModel;
  private TreeModel myTreeModel;
  private Vector<DefaultNodeComponent> nodeComponents;
  private Dimension dimension;

  private int maxLabelLength;
  private int maxClientLabelLength;
  private int effectiveLabelLength = 0;
  private int effectiveLabelPixel = 0;

  private ClusterSplitterGVS mClusterSplitter;
  private TreeLayoutController mTreeLayoutController;

  /**
   * Builds an instance of VisualizationTreePanel
   * 
   * @param visualModel
   */
  public VisualizationTreePanel(VisualizationTreeModel visualModel) {
    super();
    // TODO check Logger replacement
    // this.treeContLogger =
    // gvs.common.Logger.getInstance().getTreeControllerLogger();
    treeContLogger = LoggerFactory.getLogger(VisualizationTreePanel.class);
    treeContLogger.debug("Build new Treevisualization-/panel and model");
    this.visualModel = visualModel;
    this.visualModel.addObserver(this);
  }

  // Builds an appropriate node component for each node
  private void createNodeComponents(Vector<INode> pNodes,
      Dimension pDimension) {
    treeContLogger.debug("Creating node components");
    Iterator<INode> verIt = pNodes.iterator();
    while (verIt.hasNext()) {
      INode node = (INode) verIt.next();
      DefaultNodeComponent nodeComponent = new DefaultNodeComponent(
          (IBinaryNode) node, pDimension, effectiveLabelLength,
          effectiveLabelPixel);
      nodeComponents.add(nodeComponent);
    }
  }

  // Calculates maximum length of node components in order of longest
  // node label
  private void checkLength(Vector<INode> pNodes) {
    treeContLogger.debug("Calculate maximal node width");
    Vector<INode> nodes = pNodes;
    Iterator<INode> it = nodes.iterator();

    while (it.hasNext()) {
      String label = ((INode) it.next()).getNodeLabel();
      int labelLength = label.length();

      if (maxClientLabelLength > maxLabelLength) {
        maxClientLabelLength = maxLabelLength;

        if (effectiveLabelLength < labelLength) {
          effectiveLabelLength = labelLength;
        }
      } else {
        if (maxClientLabelLength == 0) {
          if ((labelLength <= maxLabelLength)) {
            if (labelLength > effectiveLabelLength) {
              effectiveLabelLength = labelLength;
            }
          }

          if ((labelLength > maxLabelLength)) {
            effectiveLabelLength = maxLabelLength;
          }
        } else {
          if (label.length() > maxClientLabelLength) {
            effectiveLabelLength = maxClientLabelLength;
          } else {
            if (effectiveLabelLength < labelLength) {
              effectiveLabelLength = labelLength;

            }
          }
        }
      }
    }
    calculateStringPixel();
  }

  // Calculates pixel length of vertex components
  private void calculateStringPixel() {
    treeContLogger.debug("Calculate node width in pixels");
    effectiveLabelPixel = PIXELLENGTH * effectiveLabelLength;
  }

  /**
   * Creates new node components in order of notification from visual model
   * 
   */
  public void update(Observable arg0, Object arg1) {
    treeContLogger.debug("VisualizationTreeModel.update()");
    mTreeLayoutController = (TreeLayoutController) arg1;
    this.setBackground(Color.WHITE);
    dimension = new Dimension((this.getWidth() / HUNDREDPERCENT),
        (this.getHeight() / HUNDREDPERCENT));

    myTreeModel = visualModel.getTreeModel();
    nodeComponents = new Vector<DefaultNodeComponent>();

    effectiveLabelLength = 0;
    effectiveLabelPixel = 0;
    maxLabelLength = Configuration.getInstance().getMaxLabelLength();
    maxClientLabelLength = myTreeModel.getMaxLabelLength();
    checkLength(myTreeModel.getNodes());
    createNodeComponents(myTreeModel.getNodes(), dimension);

    repaint();
  }

  /**
   * Paints visual panel and its components
   */
  public void paint(Graphics g) {
    treeContLogger.debug("VisualizationTreePanel.paint()");
    super.paint(g);
    Dimension paintDimension = this.getSize();
    Iterator<DefaultNodeComponent> it;

    // Now just drawing the Edges and Labels (not the Ellipse):
    it = nodeComponents.iterator();
    while (it.hasNext()) {
      DefaultNodeComponent nComponent = (DefaultNodeComponent) it.next();
      nComponent.drawEdges();
      nComponent.setDimension(paintDimension);
      nComponent.paint(g);
    }

    // Now drawing the Ellipse:
    it = nodeComponents.iterator();
    while (it.hasNext()) {
      DefaultNodeComponent nComponent = (DefaultNodeComponent) it.next();
      // nComponent.setDimension(dimension);
      nComponent.paint(g);
    }

    if (ClusterSplitterGVS.isEnabled()) {
      if (mClusterSplitter == null) {
        mClusterSplitter = new ClusterSplitterGVS(this);
        Thread clusterSplitterThread = new Thread(mClusterSplitter);
        clusterSplitterThread.start();
      }
    }

  } // paint

  /**
   * Set cluster splitter.
   * 
   * @param pClusterSplitter
   *          new cluster splitter
   * @return old cluster splitter
   */
  public final ClusterSplitter setClusterSplitter(
      ClusterSplitterGVS pClusterSplitter) {
    ClusterSplitter old = mClusterSplitter;
    mClusterSplitter = pClusterSplitter;
    return old;
  }

  public final Vector<DefaultNodeComponent> getNodeComponents() {
    return nodeComponents;
  }

  public final TreeLayoutController getTreeLayoutController() {
    return mTreeLayoutController;
  }

}
