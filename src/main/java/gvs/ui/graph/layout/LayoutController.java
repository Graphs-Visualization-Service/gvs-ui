package gvs.ui.graph.layout;

import gvs.common.Configuration;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;
import gvs.ui.graph.layout.helpers.Area;
import gvs.ui.graph.layout.helpers.AreaDimension;
import gvs.ui.graph.layout.helpers.AreaPoint;
import gvs.ui.graph.layout.helpers.Particle;
import gvs.ui.graph.layout.rules.Traction;
import gvs.ui.graph.layout.ticker.AreaTicker;
import gvs.ui.graph.layout.ticker.Tickable;

import java.awt.Point;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and prepares the elements which have to be layouted
 * 
 * @author aegli
 *
 */
public class LayoutController extends Observable implements Observer, Tickable {

  private final int setLayoutStableAfterTime = 10000;
  private Logger graphContLogger = null;
  private Area area = null;
  private AreaTicker ticker = null;
  private Particle particle = null;
  private Vector vertizes = null;
  private Vector edges = null;
  private boolean doSoftLayout = false;
  private Random random = null;

  /**
   * Starts layout engine
   *
   */
  public LayoutController() {
    // TODO: check replacement of Logger Instance
    // this.graphContLogger=gvs.common.Logger.getInstance().getGraphControllerLogger();
    this.graphContLogger = LoggerFactory.getLogger(LayoutController.class);
    vertizes = new Vector();
    edges = new Vector();
    area = new Area(new AreaDimension(950, 900));
    ticker = new AreaTicker(this, 40);
    ticker.start();
    graphContLogger.info("Starting graph layout controller");
    graphContLogger.debug("Starting layout guard");
    Timer guard = new Timer();
    LayoutGuard layoutGuard = new LayoutGuard(area);
    guard.schedule(layoutGuard, setLayoutStableAfterTime);
    random = new Random();
    random.setSeed(4000);
  }

  public void update(Observable o, Object arg) {
  }

  /**
   * Checks if particles in area are stable. If true, stops layouting engine,
   * waits 1500ms and displays with correct components
   */
  public void tick(double rate, double rateRatio, boolean drop, long iteration,
      long time) {
    if (area.getAreaState()) {
      ticker.shutdown();
      try {
        Thread.sleep(Configuration.getInstance().getLayoutDelay());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      setChanged();
      notifyObservers("TRUE");
    } else {
      setChanged();
      notifyObservers("FALSE");
      area.updateAll();
    }

  }

  /**
   * Receives vertices which have to be layouted
   * 
   * @param vertices
   * @param edges
   * @param doSoftLayout
   */
  public void setElements(Vector vertices, Vector edges, boolean doSoftLayout) {

    graphContLogger.info(
        "LayoutController has new elements detected, start layouting procedure");
    this.doSoftLayout = doSoftLayout;
    this.vertizes = vertices;
    this.edges = edges;
    ticker.startTicking();
    createVertexParticles();
    createEdgeTractions();

    area.setAreaState(false);
  }

  /**
   * Creates a particle for each vertex
   *
   */
  public void createVertexParticles() {
    Point p = new Point();
    Iterator it = vertizes.iterator();
    while (it.hasNext()) {
      IVertex myVertex = (IVertex) it.next();

      if (myVertex.isFixedPosition() == false) {
        if (doSoftLayout == true) {
          p = generateSoftPoints(myVertex);
        } else {
          p = generateRandomPoints(myVertex);
        }
      } else {
        p = generateFixedPoints(myVertex);
      }

      particle = new Particle(new AreaPoint(p), myVertex.getId(), myVertex,
          myVertex.isFixedPosition(), 50/* masse */, 40/* radius */);
      area.addParticles(particle);

    }
  }

  // Use random coordinates as input for engine
  private Point generateRandomPoints(IVertex vertex) {
    Point randomPoint = new Point();
    randomPoint.x = (int) ((double) (area.getUniverseDimension()
        .dimensionWidth()) * Math.random());
    randomPoint.y = (int) ((double) (area.getUniverseDimension()
        .dimensionHeight()) * Math.random());
    return randomPoint;

  }

  // Use soft random coordinates as input for engine
  private Point generateSoftPoints(IVertex vertex) {
    Point softPoint = new Point();
    softPoint.x = (int) (random.nextDouble() * 100);
    softPoint.y = (int) (random.nextDouble() * 100);
    System.err.println(softPoint.y);

    return softPoint;
  }

  // Use existing vertex coordinates as input for engine
  private Point generateFixedPoints(IVertex vertex) {
    Point fixedPoint = new Point();
    fixedPoint.x = (int) ((double) vertex.getXPosition() * 10);
    fixedPoint.y = (int) ((double) vertex.getYPosition() * 10);
    return fixedPoint;

  }

  /**
   * Creates tractions between related vertices
   *
   */
  public void createEdgeTractions() {
    Iterator it1 = edges.iterator();
    while (it1.hasNext()) {
      IEdge edge = (IEdge) it1.next();
      IVertex vertexFrom = edge.getStartVertex();
      IVertex vertexTo = edge.getEndVertex();

      Traction t = new Traction(area.getParticleWithID(vertexFrom.getId()),
          area.getParticleWithID(vertexTo.getId()), 10, 70);

      area.addTraction(t);
    }
  }

  /**
   * Returns layoutig area
   * 
   * @return
   */
  public Area getUniverse() {
    return area;
  }
}
