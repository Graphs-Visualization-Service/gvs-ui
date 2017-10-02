package gvs.ui.graph.view;

import gvs.interfaces.IIconVertex;
import gvs.interfaces.IIconVertexComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Stroke;
import java.awt.image.ImageObserver;

import javax.swing.JComponent;

/**
 * Component for displaying vertices with an icon as foreground
 * 
 * @author aegli
 *
 */
public class IconVertexComponent extends JComponent implements IIconVertexComponent {

	private static final long serialVersionUID = 1L;
	private final int ICONHEIGHT = 50;
	private final int ICONWIDTH = 50;
	private final double HUNDREDPERCENT = 100;
	private int BOUNDARYXOFFSET = 30;
	private int BOUNDARYYOFFSET = 25;

	private IIconVertex vertex = null;
	private String vertexLabel = null;
	private Color lineColor = null;
	private Stroke lineStroke = null;
	private Image vertexIcon = null;
	private boolean isRelative = false;
	private double xPosition = 0;
	private double yPosition = 0;

	private Dimension dimension = null;
	private boolean isActivVertex = false;
	private Font font = null;

	@SuppressWarnings("unused")
	private ImageObserver imageObserver = null;
	private MediaTracker mediaTracker = null;

	private double onePercentX = 0;
	private double onePercentY = 0;

	private int labelLength = 0;
	private String displayLabel = null;

	/**
	 * Builds an instance of an icon vertex component
	 * 
	 * @param pVertex
	 * @param pDim
	 * @param pLabelLength
	 * @param pMaxPixel
	 */
	public IconVertexComponent(IIconVertex pVertex, Dimension pDim, int pLabelLength, int pMaxPixel) {
		super();
		this.dimension = pDim;
		this.vertex = pVertex;
		this.vertexLabel = vertex.getLabel();
		this.lineColor = vertex.getLineColor();
		this.lineStroke = vertex.getLineStroke();
		this.vertexIcon = vertex.getIcon();
		this.isRelative = vertex.isRelative();
		this.xPosition = vertex.getXPosition() * (double) dimension.width;
		this.yPosition = vertex.getYPosition() * (double) dimension.height;

		mediaTracker = new MediaTracker(this);
		imageObserver = new ImageObserver() {
			public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
				return false;
			}
		};
		;

		font = new Font("Arial", Font.BOLD, 12);
		this.labelLength = pLabelLength;

		if (isRelative) {
			BOUNDARYXOFFSET = 0;
			BOUNDARYYOFFSET = 0;
		}
		displayLabel();
	}

	// Calculates label in oder of maximal allowed label length
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

	// Draw icon vertex component
	private void drawStandartImage(Graphics2D g2) {

		mediaTracker.addImage(vertexIcon, 0);

		try {
			mediaTracker.waitForID(0);
		} catch (InterruptedException ie) {
			System.err.println(ie);
			System.exit(1);
		}

		int xpos = ((int) xPosition - ICONHEIGHT / 2);
		int yPos = ((int) yPosition - ICONWIDTH / 2);

		g2.setColor(lineColor);
		g2.setStroke(lineStroke);

		g2.drawRect(xpos + BOUNDARYXOFFSET - 5, yPos + BOUNDARYYOFFSET - 5, ICONWIDTH + 10, ICONHEIGHT + 10);
		g2.drawImage(vertexIcon, xpos + BOUNDARYXOFFSET, yPos + BOUNDARYYOFFSET, ICONHEIGHT, ICONWIDTH, null);
	}

	// Set new vertex position when user dragged component
	private void setVertexCoordinate(double pXPosition, double pYPosition) {
		vertex.setXPosition(pXPosition / onePercentX);
		vertex.setYPosition(pYPosition / onePercentY);
	}

	// Write icon label
	private void labelVertex(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(font);

		FontMetrics fm = getFontMetrics(font);

		int xstart = ((int) xPosition - (fm.stringWidth(displayLabel)) / 2);
		int ystart = (int) yPosition + ICONHEIGHT;

		g2.setColor(Color.BLACK);
		g2.drawString(displayLabel, xstart + BOUNDARYXOFFSET, ystart + BOUNDARYYOFFSET);
	}

	/**
	 * Sets new dimension in order to resize frame
	 */
	public void setDimension(Dimension pDim) {
		dimension = pDim;

		onePercentX = dimension.getWidth() / HUNDREDPERCENT;
		onePercentY = dimension.getHeight() / HUNDREDPERCENT;

		this.xPosition = onePercentX * vertex.getXPosition();
		this.yPosition = onePercentY * vertex.getYPosition();
	}

	/**
	 * Marks component as active if clicked by user
	 */
	public void setActive(boolean pState) {
		isActivVertex = pState;
		vertex.setFixedPosition(true);
	}

	/**
	 * Returns x position of component
	 */
	public double getXPosition() {
		return xPosition + BOUNDARYXOFFSET;
	}

	/**
	 * Returns y position of component
	 */
	public double getYPosition() {
		return yPosition + BOUNDARYYOFFSET;
	}

	/**
	 * Changes xy position of vertex model. Happens when user draggs component
	 */
	public void moveBy(int pDx, int pDy) {
		xPosition += pDx;
		yPosition += pDy;
		setVertexCoordinate(xPosition, yPosition);
	}

	/**
	 * Paints icon vertex component
	 */
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;

		if (isActivVertex) {
			drawStandartImage(g2);
		} else {
			drawStandartImage(g2);
		}
		labelVertex(g);
	}
}