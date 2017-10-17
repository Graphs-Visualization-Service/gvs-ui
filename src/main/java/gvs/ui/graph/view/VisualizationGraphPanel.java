package gvs.ui.graph.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.common.Configuration;
import gvs.interfaces.IDefaultVertex;
import gvs.interfaces.IEdge;
import gvs.interfaces.IIconVertex;
import gvs.interfaces.IVertex;
import gvs.interfaces.IVertexComponent;
import gvs.interfaces.IVisualizationGraphPanel;
import gvs.ui.graph.model.GraphModel;
import gvs.ui.graph.model.VisualizationGraphModel;

/**
 * Is responsible for the visualization of a model and its components.
 * 
 * @author aegli
 *
 */
public class VisualizationGraphPanel extends JPanel implements Observer,
    MouseListener, MouseMotionListener, IVisualizationGraphPanel {

  private static final int OVAL_HEIGHT = 20;
  private static final int OVAL_WIDTH = 20;
  private Logger graphContLogger = null;
  private static final long serialVersionUID = 1L;
  private static final int PIXELLENGTH = 12;
  private static final int HUNDREDPERCENT = 100;

  private Vector<IVertexComponent> nodeComponents = null;
  private Vector<EdgeComponent> edgeComponents = null;
  private EdgeComponent edgeComponent = null;
  private IVertexComponent vertexBeingDragged = null;

  private VisualizationGraphModel visualModel = null;
  private GraphModel graphModel = null;
  private Color backgroundColor = null;
  private Image icon = null;
  private LabelConflictCheck check = null;

  private int maxLabelLength = 0;
  private int maxClientLabelLength = 0;
  private int effectiveLabelLength = 0;
  private int effectiveLabelPixel = 0;

  private MediaTracker mediaTracker = null;

  // During dragging, these record the x and y coordinates of the vertex
  private int prevDragPosX = 0;
  private int prevDragPosY = 0;

  /**
   * Build an instance of VisualizationGraphPanel.
   * 
   * @param vm
   *          visualModel
   */
  public VisualizationGraphPanel(VisualizationGraphModel vm) {
    super();
    // TODO check replacment of logger
    // this.graphContLogger =
    // gvs.common.Logger.getInstance().getGraphControllerLogger();
    this.graphContLogger = LoggerFactory
        .getLogger(VisualizationGraphPanel.class);
    graphContLogger.debug("Build new Visualization-/panel and model");
    this.visualModel = vm;
    this.visualModel.addObserver(this);
    edgeComponents = new Vector<>();
    nodeComponents = new Vector<>();
    mediaTracker = new MediaTracker(this);
  }

  /**
   * Build for each vertex an appropriate vertex component.
   * 
   * @param pVertizes
   *          vetices
   * @param pDimension
   *          dimension
   */
  private void createVertexGraphComponents(Vector<IVertex> pVertizes,
      Dimension pDimension) {
    graphContLogger.debug("Creating vertizes components");
    Iterator<IVertex> verIt = pVertizes.iterator();
    while (verIt.hasNext()) {
      Object vertex = verIt.next();

      Class<?>[] interfaces = vertex.getClass().getInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
        if (interfaces[i] == IDefaultVertex.class) {
          graphContLogger.debug("Creating default vertex");
          IVertexComponent nodeComponent = new DefaultVertexComponent(
              (IDefaultVertex) vertex, pDimension, effectiveLabelLength,
              effectiveLabelPixel);
          nodeComponents.add(nodeComponent);
        } else {
          graphContLogger.debug("Creating icon vertex");
          IVertexComponent iconComponent = new IconVertexComponent(
              (IIconVertex) vertex, pDimension, effectiveLabelLength,
              effectiveLabelPixel);
          nodeComponents.add(iconComponent);
        }
      }
    }
  }

  /**
   * Create for each edge an appropriate edge component.
   * 
   * @param pEdges
   *          edges
   * @param dimension
   *          dimension
   */
  private void createEdgeGraphComponents(Vector<IEdge> pEdges,
      Dimension dimension) {
    graphContLogger.debug("Creating edge components");
    Iterator<IEdge> edgIt = pEdges.iterator();
    while (edgIt.hasNext()) {
      IEdge edge = (IEdge) edgIt.next();
      edgeComponent = new EdgeComponent(edge, dimension, backgroundColor, check,
          effectiveLabelPixel);
      edgeComponents.add(edgeComponent);
    }
  }

  /**
   * Calculate maximal length of vertex components in order of longest vertex
   * label.
   * 
   * @param pVertizes
   *          vetices
   */
  private void checkLength(Vector<IVertex> pVertizes) {
    graphContLogger.debug("Calculate maximal vertex width");
    Vector<IVertex> vertizes = pVertizes;

    Iterator<IVertex> it = vertizes.iterator();
    while (it.hasNext()) {
      String label = ((IVertex) it.next()).getLabel();
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

  /**
   * Calculate pixel length of vertex components.
   */
  private void calculateStringPixel() {
    graphContLogger.debug("Calculate vertex width in pixels");
    effectiveLabelPixel = PIXELLENGTH * effectiveLabelLength;
  }

  /**
   * Add or removes mouse listener.
   */
  private void mouseController() {
    graphContLogger.info("Check if mouse dragging is allowed");
    removeMouseListener(this);
    removeMouseMotionListener(this);

    if (visualModel.isDraggable()) {
      addMouseListener(this);
      addMouseMotionListener(this);
    }
  }

  /**
   * Creates new vertex and edge components in order to notify visual model.
   * 
   * @param o
   *          Observable
   * @param arg
   *          Object
   */
  public void update(Observable o, Object arg) {
    Dimension dimension = new Dimension((this.getWidth() / HUNDREDPERCENT),
        (this.getHeight() / HUNDREDPERCENT));

    if (!visualModel.isLayouting()) {
      mouseController();
      check = new LabelConflictCheck();
      graphModel = visualModel.getGraphModel();
      icon = graphModel.getBackgroundImage();
      backgroundColor = graphModel.getBackgroundColor();
      edgeComponents = new Vector<>();
      nodeComponents = new Vector<>();

      effectiveLabelLength = 0;
      effectiveLabelPixel = 0;
      maxClientLabelLength = graphModel.getMaxLabelLength();
      maxLabelLength = Configuration.getInstance().getMaxLabelLength();
      checkLength(graphModel.getVertizes());
      createVertexGraphComponents(graphModel.getVertizes(), dimension);
      createEdgeGraphComponents(graphModel.getEdges(), dimension);
    } else {
      graphModel = visualModel.getGraphModel();
      icon = graphModel.getBackgroundImage();
      backgroundColor = graphModel.getBackgroundColor();
    }
    repaint();
  }

  /**
   * Paints visual panel and its components.
   * 
   * @param g
   *          Graphic
   */
  public void paint(Graphics g) {
    super.paint(g);
    Dimension d = getSize();
    mediaTracker.addImage(icon, 0);
    try {
      mediaTracker.waitForID(0);
    } catch (InterruptedException ie) {
      System.exit(1);
    }

    if (backgroundColor != null) {
      this.setBackground(backgroundColor);
    } else {
      g.drawImage(icon, 0, 0, d.width, d.height, null);
    }

    if (!visualModel.isLayouting()) {
      Iterator<IVertexComponent> ver = nodeComponents.iterator();
      while (ver.hasNext()) {
        IVertexComponent nComponent = (IVertexComponent) ver.next();
        nComponent.setDimension(d);
        nComponent.paint(g);
      }

      Iterator<EdgeComponent> edg = edgeComponents.iterator();
      while (edg.hasNext()) {
        EdgeComponent eComponent = (EdgeComponent) edg.next();
        eComponent.setDimension(d);
        eComponent.paint(g);
      }
    } else {
      double onePercentX = d.getWidth() / HUNDREDPERCENT;
      double onePercentY = d.getHeight() / HUNDREDPERCENT;
      Iterator<IVertex> verL = graphModel.getVertizes().iterator();
      while (verL.hasNext()) {
        IVertex temp = ((IVertex) verL.next());
        int x = (int) (temp.getXPosition() * onePercentX),
            y = (int) (temp.getYPosition() * onePercentY);
        // TODO Why is property overwritten?
        g.setColor(Color.BLUE);
        g.fillOval(x - 14, y - 14, 28, 28);
        g.setColor(Color.RED);
        g.fillOval(x - OVAL_WIDTH / 2, y - OVAL_HEIGHT / 2, OVAL_WIDTH,
            OVAL_HEIGHT);
      }

      Iterator<IEdge> edg = graphModel.getEdges().iterator();
      while (edg.hasNext()) {
        IEdge temp = ((IEdge) edg.next());
        IVertex tempStart = temp.getStartVertex();
        IVertex tempEnd = temp.getEndVertex();

        int x1 = (int) (tempStart.getXPosition() * onePercentX);
        int y1 = (int) (tempStart.getYPosition() * onePercentY);
        int x2 = (int) (tempEnd.getXPosition() * onePercentX);
        int y2 = (int) (tempEnd.getYPosition() * onePercentY);

        g.setColor(Color.BLACK);
        g.drawLine(x1, y1, x2, y2);
      }
    }
  }

  /**
   * Updates positions when components are dragged.
   * 
   * @param evt
   *          MouseEvent
   */
  public void mouseDragged(MouseEvent evt) {
    graphContLogger.debug("Drag vertex");
    int x = evt.getX();
    int y = evt.getY();

    if (vertexBeingDragged != null) {
      vertexBeingDragged.moveBy(x - prevDragPosX, y - prevDragPosY);
      prevDragPosX = x;
      prevDragPosY = y;
      repaint();
    }
  }

  /**
   * Loads pressed vertex component in order to change its positions.
   * 
   * @param evt
   *          MouseEvent
   */
  public void mousePressed(MouseEvent evt) {
    graphContLogger.info("Mouse pressed detected, prepare for dragging");
    try {
      int x = evt.getX(); // x-coordinate of point where mouse was clicked
      int y = evt.getY(); // y-coordinate of point

      Iterator<IVertexComponent> it = nodeComponents.iterator();
      while (it.hasNext()) {
        IVertexComponent n = (IVertexComponent) it.next();

        if ((Math.abs(evt.getX() - n.getXPosition()) < 20)
            && (Math.abs(evt.getY() - n.getYPosition()) < 20)) {
          vertexBeingDragged = n;
          vertexBeingDragged.setActive(true);

          prevDragPosX = x;
          prevDragPosY = y;
        }

      }
    } catch (Exception ex) {
    }
  }

  /**
   * Sets dragged vertex component as not active.
   * 
   * @param e
   *          MouseEvent
   */
  public void mouseReleased(MouseEvent e) {
    graphContLogger.debug("Mouse released, fixing new positions");
    try {
      vertexBeingDragged.setActive(false);
      vertexBeingDragged = null;
    } catch (Exception ex) {
    }

    Iterator<IVertexComponent> it1 = nodeComponents.iterator();
    while (it1.hasNext()) {
      ((IVertexComponent) (it1.next())).setActive(false);
    }

    Iterator<EdgeComponent> it = edgeComponents.iterator();
    while (it.hasNext()) {
      ((EdgeComponent) (it.next())).vertexDragged(true);
    }
    repaint();
  }

  // TODO why are these empty?
  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseMoved(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent evt) {
  }
}
