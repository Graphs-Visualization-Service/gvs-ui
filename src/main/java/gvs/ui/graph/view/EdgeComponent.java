package gvs.ui.graph.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import gvs.interfaces.IDefaultVertex;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Default component for an edge
 * 
 * @author aegli
 *
 */
public class EdgeComponent extends JComponent {

  private static final long serialVersionUID = 1L;
  private final double ELLIPSEHEIGHT = 24;
  private final int HUNDREDPERCENT = 100;
  private final int NINETYDEGREES = 90;
  private int BOUNDARYXOFFSET = 30;
  private int BOUNDARYYOFFSET = 25;

  private Font font = null;
  private Color labelColor = null;
  private IEdge edge = null;
  private boolean isRelative = false;
  private double ellipseOffset = 0;

  private String edgeLabel = null;
  private Color lineColor = null;
  private Stroke lineStroke = null;
  private boolean isDirected = false;
  private IVertex startVertex = null;
  private IVertex endVertex = null;

  private double startVertexXPos = 0;
  private double startVertexYPos = 0;
  private double endVertexXPos = 0;
  private double endVertexYPos = 0;

  private double onePercentX = 0;
  private double onePercentY = 0;

  private LabelConflictCheck check = null;
  private double labelXPos = 0;
  private double labelYPos = 0;
  private boolean isAdded = false;
  private double oppositeLeg = 0;
  private double adjacentLeg = 0;

  private boolean wasDragged = false;

  /**
   * Builds an instance of an edge component
   * 
   * @param pEdge
   * @param pDim
   * @param panelColor
   * @param check
   * @param pPixelLength
   */
  public EdgeComponent(IEdge pEdge, Dimension pDim, Color panelColor,
      LabelConflictCheck check, int pPixelLength) {
    super();

    this.edge = pEdge;
    this.check = check;
    this.onePercentX = pDim.getWidth() / HUNDREDPERCENT;
    this.onePercentY = pDim.getHeight() / HUNDREDPERCENT;

    this.font = new Font("Arial", Font.BOLD, 10);
    labelColor = panelColor;

    if (pPixelLength > ELLIPSEHEIGHT) {
      this.ellipseOffset = pPixelLength / 2;
    } else {
      this.ellipseOffset = ELLIPSEHEIGHT;
    }

    this.edgeLabel = edge.getLabel();
    this.lineColor = edge.getLineColor();
    this.setLineStroke(edge.getLineStroke());
    this.isDirected = edge.isDirected();
    this.startVertex = edge.getStartVertex();
    this.endVertex = edge.getEndVertex();
    this.isRelative = startVertex.isRelative();
    this.startVertexXPos = startVertex.getXPosition() * (double) onePercentX
        + BOUNDARYXOFFSET;
    this.startVertexYPos = startVertex.getYPosition() * (double) onePercentY
        + BOUNDARYYOFFSET;
    this.endVertexXPos = endVertex.getXPosition() * (double) onePercentX
        + BOUNDARYXOFFSET;
    this.endVertexYPos = endVertex.getYPosition() * (double) onePercentY
        + BOUNDARYYOFFSET;

    if (isRelative) {
      BOUNDARYXOFFSET = 0;
      BOUNDARYYOFFSET = 0;
    }
  }

  // Draws arrow when edge type is directed
  private static void drawArrow(Graphics2D g2d, int xCenter, int yCenter, int x,
      int y, float stroke) {
    double aDir = Math.atan2(xCenter - x, yCenter - y);
    g2d.drawLine(x, y, xCenter, yCenter);
    Polygon tmpPoly = new Polygon();

    int i1 = 12 + (int) (stroke * 2);
    int i2 = 6 + (int) stroke; // make the arrow head the same size
    // regardless of the length length
    tmpPoly.addPoint(x, y); // arrow tip
    tmpPoly.addPoint(x + xCor(i1, aDir + .5), y + yCor(i1, aDir + .5));
    tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
    tmpPoly.addPoint(x + xCor(i1, aDir - .5), y + yCor(i1, aDir - .5));
    tmpPoly.addPoint(x, y); // arrow tip
    g2d.drawPolygon(tmpPoly);
    g2d.fillPolygon(tmpPoly); // remove this line to leave arrow head
    // unpainted
  }

  // Calculate arrow
  private static int yCor(int len, double dir) {
    return (int) (len * Math.cos(dir));
  }

  // Calculate arrow
  private static int xCor(int len, double dir) {
    return (int) (len * Math.sin(dir));
  }

  // Write default label when possible into middle between two vertizes
  private void labelEdge(Graphics2D g2) {
    FontMetrics fm = getFontMetrics(font);
    double labelLenght = fm.stringWidth(edgeLabel);
    double labelHeight = fm.getAscent();

    if (startVertex != endVertex) {
      labelXPos = ((startVertexXPos + endVertexXPos) / 2) - (labelLenght / 2);
      labelYPos = ((startVertexYPos + endVertexYPos) / 2) - (labelHeight / 2);
    } else {
      labelXPos = (endVertexXPos + ellipseOffset * 2) - (labelLenght / 2);
      labelYPos = (endVertexYPos - ELLIPSEHEIGHT) - (labelHeight / 2);
    }

    if (wasDragged || isRelative) {
      if (!isAdded) {
        check.addLabelPos(this);
        isAdded = true;
      } else {
        check.checkAgainstAll(this);
      }
    }

    g2.setPaint(Color.WHITE);

    g2.setColor(labelColor);
    g2.fill(new RoundRectangle2D.Double(labelXPos, labelYPos, labelLenght,
        labelHeight, 0.0, 0.0));

    g2.setColor(Color.BLACK);
    g2.drawString(edgeLabel, (int) labelXPos, (int) (labelYPos + labelHeight));
  }

  // Calculate start and end position of edge.
  private void calculateLength() {
    oppositeLeg = endVertexXPos - startVertexXPos;
    adjacentLeg = endVertexYPos - startVertexYPos;

    double alpha = Math.atan(oppositeLeg / adjacentLeg);
    double alphaDegree = Math.toDegrees(Math.atan2(oppositeLeg, (adjacentLeg)));
    double y = (ELLIPSEHEIGHT * ellipseOffset)
        / Math.sqrt(ellipseOffset * ellipseOffset + ELLIPSEHEIGHT
            * ELLIPSEHEIGHT * Math.tan(alpha) * Math.tan(alpha));
    double x = Math.tan(alpha) * y;

    Object vertex = startVertex;
    @SuppressWarnings("rawtypes")
    Class[] interfaces = vertex.getClass().getInterfaces();
    for (int i = 0; i < interfaces.length; i++) {
      if (interfaces[i] == IDefaultVertex.class) {
        if ((alphaDegree) < -NINETYDEGREES || (alphaDegree) > NINETYDEGREES) {
          startVertexXPos -= x;
          startVertexYPos -= y;

          endVertexXPos += x;
          endVertexYPos += y;
        } else {
          startVertexXPos += x;
          startVertexYPos += y;

          endVertexXPos -= x;
          endVertexYPos -= y;
        }
      } else {
        if ((alphaDegree) < -NINETYDEGREES || (alphaDegree) > NINETYDEGREES) {
          startVertexXPos -= x;
          startVertexYPos -= y;

          endVertexXPos += x;
          endVertexYPos += y;
        } else {
          startVertexXPos += x;
          startVertexYPos += y;

          endVertexXPos -= x;
          endVertexYPos -= y;
        }
      }
    }
  }

  /**
   * Returns x position of edge label
   * 
   * @return
   */
  public double getLabelXPos() {
    return labelXPos;
  }

  /**
   * Sets x postion of edge label
   * 
   * @param pLabelXPos
   */
  public void setLabelXPos(double pLabelXPos) {
    this.labelXPos = pLabelXPos;
  }

  /**
   * Returns y postion of edge label
   * 
   * @return
   */
  public double getLabelYPos() {
    return labelYPos;
  }

  /**
   * Sets y position of edge label
   * 
   * @param pLabelYPos
   */
  public void setLabelYPos(double pLabelYPos) {
    this.labelYPos = pLabelYPos;
  }

  /**
   * Moves label away, if label edges are overlapping.
   *
   */
  public void moveAlongEdge() {
    double xDist = oppositeLeg * 0.1;
    double yDist = adjacentLeg * 0.1;

    labelXPos += xDist;
    labelYPos += yDist;
  }

  /**
   * Notifies when start or end vertex was dragged
   * 
   * @param checkDragging
   */
  public void vertexDragged(boolean checkDragging) {
    wasDragged = checkDragging;
  }

  /**
   * Sets new Dimension in order to resize frame
   * 
   * @param pDim
   */
  public void setDimension(Dimension pDim) {
    onePercentX = pDim.getWidth() / HUNDREDPERCENT;
    onePercentY = pDim.getHeight() / HUNDREDPERCENT;
  }

  /**
   * Paints vertex component
   */
  public void paint(Graphics g) {
    super.paint(g);

    this.startVertexXPos = startVertex.getXPosition() * (double) onePercentX
        + BOUNDARYXOFFSET;
    this.startVertexYPos = startVertex.getYPosition() * (double) onePercentY
        + BOUNDARYYOFFSET;
    this.endVertexXPos = endVertex.getXPosition() * (double) onePercentX
        + BOUNDARYXOFFSET;
    this.endVertexYPos = endVertex.getYPosition() * (double) onePercentY
        + BOUNDARYYOFFSET;

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(lineColor);
    g2.setStroke(getLineStroke());
    if (isDirected) {
      if (startVertex != endVertex) {
        calculateLength();
        drawArrow(g2, (int) startVertexXPos, (int) startVertexYPos,
            (int) endVertexXPos, (int) endVertexYPos, 1.0f);
      } else {
        g2.draw(
            new Line2D.Double(startVertexXPos, startVertexYPos - ELLIPSEHEIGHT,
                endVertexXPos, endVertexYPos - ELLIPSEHEIGHT * 2));
        g2.draw(new Line2D.Double(startVertexXPos,
            startVertexYPos - ELLIPSEHEIGHT * 2,
            endVertexXPos + ellipseOffset * 2,
            endVertexYPos - ELLIPSEHEIGHT * 2));
        g2.draw(new Line2D.Double(startVertexXPos + ellipseOffset * 2,
            startVertexYPos - ELLIPSEHEIGHT * 2,
            endVertexXPos + ellipseOffset * 2, endVertexYPos));
        drawArrow(g2, (int) (startVertexXPos + ellipseOffset * 2),
            (int) startVertexYPos, (int) (endVertexXPos + ellipseOffset),
            (int) endVertexYPos, 1.0f);
      }
    } else {
      g2.setStroke(getLineStroke());
      if (startVertex != endVertex) {
        calculateLength();
        g2.draw(new Line2D.Double(startVertexXPos, startVertexYPos,
            endVertexXPos, endVertexYPos));
      } else {
        g2.draw(
            new Line2D.Double(startVertexXPos, startVertexYPos - ELLIPSEHEIGHT,
                endVertexXPos, endVertexYPos - ELLIPSEHEIGHT * 2));
        g2.draw(new Line2D.Double(startVertexXPos,
            startVertexYPos - ELLIPSEHEIGHT * 2,
            endVertexXPos + ellipseOffset * 2,
            endVertexYPos - ELLIPSEHEIGHT * 2));
        g2.draw(new Line2D.Double(startVertexXPos + ellipseOffset * 2,
            startVertexYPos - ELLIPSEHEIGHT * 2,
            endVertexXPos + ellipseOffset * 2, endVertexYPos));
        g2.draw(new Line2D.Double(startVertexXPos + ellipseOffset * 2,
            startVertexYPos, endVertexXPos + ellipseOffset, endVertexYPos));
      }
    }
    labelEdge(g2);
    wasDragged = false;
  }

  public Stroke getLineStroke() {
    return lineStroke;
  }

  public void setLineStroke(Stroke lineStroke) {
    this.lineStroke = lineStroke;
  }
}
