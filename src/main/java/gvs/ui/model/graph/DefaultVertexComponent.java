package gvs.ui.model.graph;

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

import gvs.interfaces.IDefaultVertex;
import gvs.interfaces.IDefaultVertexComponent;

/**
 * Swing representation of a graph vertex.
 * 
 * @author aegli
 *
 */
public class DefaultVertexComponent extends JComponent
    implements IDefaultVertexComponent {

  private static final long serialVersionUID = 1L;
  private final int DEFAULTHIGHT = 40;
  private int BOUNDARYXOFFSET = 30;
  private int BOUNDARYYOFFSET = 25;

  private IDefaultVertex vertex = null;
  private String vertexLabel = null;

  private Color lineColor = null;
  private BasicStroke lineStroke = null;
  private Color fillColor = null;
  private double xPosition = 0;
  private double yPosition = 0;
  private boolean isRelative = false;

  private Dimension dim = null;
  private boolean activVertex = false;
  private Font font = null;

  private double onePercentX = 0;
  private double onePercentY = 0;

  private int normLabelPixel = 0;
  private int labelLength = 0;
  private String displayLabel = null;
  private FontMetrics fm = null;

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

  /* Marked Ellipse */
  private int additionalOffsetMarkedEllipse;
  private int markedRectWidth;
  private int markedRectHeight;
  private int xMarkedEllipseOffset;
  private int yMarkedEllipseOffset;

  /* Marked EllipseLine */
  private int markedRectWidthLine;
  private int markedRectHeightLine;
  private int xMarkedlineOffset;
  private int yMarkedlineOffset;

  /**
   * Builds an instance of a vertex component. Each vertex will be displayed as
   * a vertex component
   * 
   * @param pVertex
   * @param pDim
   * @param pLabelLength
   * @param pMaxPixel
   */
  public DefaultVertexComponent(IDefaultVertex pVertex, Dimension pDim,
      int pLabelLength, int pMaxPixel) {
    super();
    this.vertex = pVertex;
    this.dim = pDim;

    this.onePercentX = pDim.getWidth() / 100;
    this.onePercentY = pDim.getHeight() / 100;

    this.vertexLabel = vertex.getLabel();
    this.lineColor = vertex.getLineColor();
    this.lineStroke = (BasicStroke) vertex.getLineStroke();
    this.fillColor = vertex.getFillColor();
    this.xPosition = vertex.getXPosition() * (double) dim.width;
    this.yPosition = vertex.getYPosition() * (double) dim.height;
    this.isRelative = vertex.isRelative();
    // this.font = new Font("Arial", Font.BOLD,10);
    this.font = new Font("Arial", Font.PLAIN, 20);
    this.lineOffset = (int) lineStroke.getLineWidth();

    this.labelLength = pLabelLength;
    this.normLabelPixel = pMaxPixel;

    setVertexCoordinates();
    displayLabel();
  }

  // Calculate width and height of a vertex component in order of the
  // maximale label length
  private void setVertexCoordinates() {
    if (normLabelPixel < DEFAULTHIGHT) {
      rectWidth = DEFAULTHIGHT;
    } else {
      rectWidth = normLabelPixel;
    }

    if (isRelative) {
      BOUNDARYXOFFSET = 0;
      BOUNDARYYOFFSET = 0;
    }

    rectHeight = DEFAULTHIGHT;
    xEllipseOffset = rectWidth / 2;
    yEllipseOffset = rectHeight / 2;

    /* Unmarked EllipseLine */
    rectWidthLine = rectWidth + lineOffset;
    rectHeightLine = rectHeight + lineOffset;
    xlineOffset = xEllipseOffset + lineOffset / 2;
    ylineOffset = yEllipseOffset + lineOffset / 2;

    /* Marked Ellipse */
    additionalOffsetMarkedEllipse = 20;
    markedRectWidth = rectWidth + additionalOffsetMarkedEllipse;
    markedRectHeight = rectHeight + additionalOffsetMarkedEllipse;
    xMarkedEllipseOffset = markedRectWidth / 2;
    yMarkedEllipseOffset = markedRectHeight / 2;

    /* Marked EllipseLine */
    markedRectWidthLine = markedRectWidth + lineOffset;
    markedRectHeightLine = markedRectHeight + lineOffset;
    xMarkedlineOffset = xMarkedEllipseOffset + lineOffset / 2;
    yMarkedlineOffset = yMarkedEllipseOffset + lineOffset / 2;
  }

  // Write new position from component into vertex model.
  private void setVertexCoordinate(double pXPosition, double pYPosition) {
    vertex.setXPosition(pXPosition / onePercentX);
    vertex.setYPosition(pYPosition / onePercentY);
  }

  // Calculate vertex label length in order of maximal label length
  // set in graph model
  private void displayLabel() {
    if (vertexLabel.length() > labelLength) {
      char[] temp = new char[labelLength];
      for (int i = 0; i < labelLength; i++) {
        temp[i] = vertexLabel.charAt(i);
      }

      displayLabel = new String(temp);
      displayLabel = displayLabel + "..";
    } else {
      displayLabel = vertexLabel;
    }
  }

  // Draw a mouse pressed vertex component
  private void drawActiveVertex(Graphics2D g2) {
    GradientPaint redtowhite = new GradientPaint((int) xPosition,
        (int) yPosition, Color.RED, (int) xPosition + rectWidth,
        (int) yPosition, Color.WHITE);

    g2.draw(
        new Ellipse2D.Double(xPosition - xMarkedlineOffset + BOUNDARYXOFFSET,
            yPosition - yMarkedlineOffset + BOUNDARYYOFFSET,
            markedRectWidthLine, markedRectHeightLine));

    g2.setPaint(redtowhite);
    g2.fill(
        new Ellipse2D.Double(xPosition - xMarkedEllipseOffset + BOUNDARYXOFFSET,
            yPosition - yMarkedEllipseOffset + BOUNDARYYOFFSET, markedRectWidth,
            markedRectHeight));

  }

  // Draw a standard vertex component
  private void drawStandardVertex(Graphics2D g2) {
    GradientPaint vertexFillColor = new GradientPaint((int) xPosition,
        (int) yPosition, fillColor, (int) xPosition + rectWidth,
        (int) yPosition, Color.WHITE);

    g2.draw(new Ellipse2D.Double(xPosition - xlineOffset + BOUNDARYXOFFSET,
        yPosition - ylineOffset + BOUNDARYYOFFSET, rectWidthLine,
        rectHeightLine));

    g2.setPaint(vertexFillColor);
    g2.fill(new Ellipse2D.Double(xPosition - xEllipseOffset + BOUNDARYXOFFSET,
        yPosition - yEllipseOffset + BOUNDARYYOFFSET, rectWidth, rectHeight));
  }

  // Write label into vertex component
  private void labelVertex(Graphics2D g2) {
    fm = getFontMetrics(font);
    g2.setFont(font);
    g2.setPaint(Color.BLACK);

    int xstart = ((int) xPosition - (fm.stringWidth(displayLabel)) / 2);
    int ystart = (int) yPosition + ((fm.getAscent()) / 2);

    g2.drawString(displayLabel, xstart + BOUNDARYXOFFSET,
        ystart + BOUNDARYYOFFSET);
  }

  /**
   * Updates position when user is dragging vertex component
   */
  public void moveBy(int pDx, int pDy) {
    xPosition += pDx;
    yPosition += pDy;
    setVertexCoordinate(xPosition, yPosition);
  }

  /**
   * Paints component
   */
  public void paint(Graphics g) {
    super.paint(g);
    Graphics2D g2 = (Graphics2D) g;

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(lineColor);
    g2.setStroke(lineStroke);

    if (activVertex) {
      drawActiveVertex(g2);
    } else {
      drawStandardVertex(g2);
    }
    labelVertex(g2);
  }

  /**
   * Sets dimension, used for recalculating the position in order to resize
   * frame
   */
  public void setDimension(Dimension pDim) {
    dim = pDim;

    onePercentX = dim.getWidth() / 100;
    onePercentY = dim.getHeight() / 100;

    this.xPosition = onePercentX * vertex.getXPosition();
    this.yPosition = onePercentY * vertex.getYPosition();
  }

  /**
   * When user clicked vertex component, it is set to active.
   */
  public void setActive(boolean pState) {
    activVertex = pState;
    // TODO whats the difference between relative and fixed??? when are they
    // used?
    vertex.setFixedPosition(true);
  }

  /**
   * Returns x position
   */
  public double getXPosition() {
    return (xPosition + BOUNDARYXOFFSET);
  }

  /**
   * Returns y position
   */
  public double getYPosition() {
    return (yPosition + BOUNDARYYOFFSET);
  }

}
