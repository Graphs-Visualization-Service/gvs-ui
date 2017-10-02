package gvs.ui.tree.view;

import gvs.interfaces.IBinaryNode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent;

/**
 * Default visualization of a node
 * 
 * @author aegli
 *
 */
public class DefaultNodeComponent extends JComponent {

  private static final long serialVersionUID = 1L;
  private final int DEFAULTHIGHT = 40;
  private final int BORDEROFFSET = 30;

  private IBinaryNode node;
  private String nodeLabel;
  private Color lineColor;
  private BasicStroke lineStroke;
  private Color fillColor;
  private double xPosition;
  private double yPosition;

  private int normLabelPixel;
  private int labelLength;
  private String displayLabel;
  private FontMetrics fm;

  private Dimension dim;
  private Font font;
  private boolean drawEdge = false;
  private double onePercentX;
  private double onePercentY;

  /* Unmarked Ellpise */
  private int rectWidth;
  private int rectHeight;
  private int xEllipseOffset;
  private int yEllipseOffset;

  /* Unmarked EllipseLine */
  private int lineOffset;
  private int rectWidthLine;
  private int rectHeightLine;
  private int xlineOffset;
  private int ylineOffset;

  /**
   * Builds an instance of a DefaultNodeComponent
   * 
   * @param pNode
   * @param pDim
   * @param pLabelLength
   * @param pMaxPixel
   */
  public DefaultNodeComponent(IBinaryNode pNode, Dimension pDim,
      int pLabelLength, int pMaxPixel) {
    super();
    this.node = pNode;
    this.dim = pDim;

    this.onePercentX = pDim.getWidth() / 100;
    this.onePercentY = pDim.getHeight() / 100;

    this.nodeLabel = node.getNodeLabel();
    this.lineColor = node.getLineColor();
    this.lineStroke = (BasicStroke) node.getLineStroke();
    this.fillColor = node.getFillColor();

    // Background: White
    this.fillColor = new Color(255, 255, 255);

    this.xPosition = node.getXPosition() * (double) dim.width;
    this.yPosition = node.getYPosition() * (double) dim.height;

    this.font = new Font("Arial", Font.BOLD, 25);
    this.lineOffset = (int) lineStroke.getLineWidth();
    this.labelLength = pLabelLength;
    this.normLabelPixel = pMaxPixel;

    setNodeCoordinates();
    displayLabel();
  }

  // Calculate width and height of a node component accoriding to the
  // maximale label length
  private void setNodeCoordinates() {
    if (normLabelPixel < DEFAULTHIGHT) {
      rectWidth = DEFAULTHIGHT;
    } else {
      rectWidth = normLabelPixel;
    }

    rectHeight = DEFAULTHIGHT;
    xEllipseOffset = rectWidth / 2;
    yEllipseOffset = rectHeight / 2;

    /* Unmarked EllipseLine */
    rectWidthLine = rectWidth + lineOffset;
    rectHeightLine = rectHeight + lineOffset;
    xlineOffset = xEllipseOffset + lineOffset / 2;
    ylineOffset = yEllipseOffset + lineOffset / 2;
  }

  // Paints node component
  private void drawBinaryNode(Graphics2D g2) {
    GradientPaint vertexFillColor = new GradientPaint((int) xPosition,
        (int) yPosition, fillColor, (int) xPosition + rectWidth,
        (int) yPosition, Color.WHITE);

    g2.draw(new Ellipse2D.Double(xPosition - xlineOffset,
        yPosition - ylineOffset, rectWidthLine, rectHeightLine));

    g2.setPaint(vertexFillColor);
    g2.fill(new Ellipse2D.Double(xPosition - xEllipseOffset,
        yPosition - yEllipseOffset, rectWidth, rectHeight));
  }

  // Paints edges to child nodes
  private void drawChildEdges(Graphics2D g2) {
    if (node.getLeftChild() != null) {
      g2.setStroke(node.getLeftChild().getLineStroke());
      g2.setColor(node.getLeftChild().getLineColor());
      g2.drawLine((int) xPosition, (int) yPosition,
          (int) (node.getLeftChild().getXPosition() * onePercentX)
              + BORDEROFFSET,
          (int) (node.getLeftChild().getYPosition() * onePercentY));
    }

    if (node.getRightChild() != null) {
      g2.setStroke(node.getRightChild().getLineStroke());
      g2.setColor(node.getRightChild().getLineColor());
      g2.drawLine((int) xPosition, (int) yPosition,
          (int) (node.getRightChild().getXPosition() * onePercentX)
              + BORDEROFFSET,
          (int) (node.getRightChild().getYPosition() * onePercentY));
    }
  }

  // Writes label into node component
  private void labelNode(Graphics2D g2) {
    fm = getFontMetrics(font);
    g2.setFont(font);
    g2.setPaint(Color.BLACK);

    int xstart = ((int) xPosition - (fm.stringWidth(displayLabel)) / 2);
    int ystart = (int) yPosition + ((fm.getAscent()) / 2);

    g2.drawString(displayLabel, xstart, ystart);
  }

  // Calculates node label length according to maximum label length
  // set in graph model
  private void displayLabel() {
    if (nodeLabel.length() > labelLength) {
      char[] temp = new char[labelLength];
      for (int i = 0; i < labelLength; i++) {
        temp[i] = nodeLabel.charAt(i);
      }

      displayLabel = new String(temp);
      displayLabel = displayLabel + "..";
    } else {
      displayLabel = nodeLabel;
    }
  }

  /**
   * Paints node component
   */
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2 = (Graphics2D) g;

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(lineColor);
    g2.setStroke(lineStroke);

    if (!drawEdge) {
      drawBinaryNode(g2);
    } else {
      drawChildEdges(g2);
      drawEdge = false;
    }

    labelNode(g2);
  }

  /**
   * Sets dimension, used for recalculating the position to resize frame
   * 
   * @param pDim
   */
  public void setDimension(Dimension pDim) {
    dim = pDim;

    onePercentX = dim.getWidth() / 100;
    onePercentY = dim.getHeight() / 100;

    this.xPosition = onePercentX * node.getXPosition() + BORDEROFFSET;
    this.yPosition = onePercentY * node.getYPosition();
  }

  /**
   * Returns x postion of node component
   * 
   * @return
   */
  public double getXPosition() {
    return xPosition;
  }

  /**
   * Returns y position of node component
   * 
   * @return
   */
  public double getYPosition() {
    return yPosition;
  }

  /**
   * Marks when child edges can be painted
   *
   */
  public void drawEdges() {
    drawEdge = true;
  }

  double getXLineOffset() {
    return xlineOffset;
  }

  double getYLineOffset() {
    return ylineOffset;
  }

  String getNodeLabel() {
    return nodeLabel;
  }

  IBinaryNode getNode() {
    return node;
  }

}
