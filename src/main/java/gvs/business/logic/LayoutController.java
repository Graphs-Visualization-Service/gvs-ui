package gvs.business.logic;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Timer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.access.Configuration;
import gvs.business.logic.physics.helpers.Area;
import gvs.business.logic.physics.helpers.AreaDimension;
import gvs.business.logic.physics.helpers.AreaPoint;
import gvs.business.logic.physics.helpers.Particle;
import gvs.business.logic.physics.rules.Traction;
import gvs.business.logic.physics.ticker.AreaTicker;
import gvs.business.logic.physics.ticker.Tickable;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Creates and prepares the elements which have to be layouted.
 * 
 * @author aegli
 *
 */
public class LayoutController extends Observable implements Tickable {

  private static final int DEFAULT_RADIUS = 40/* radius */;
  private static final int DEFAULT_MASS = 50/* masse */;
  private static final int SOFT_MULTIPLIER = 100;
  private static final int FIXED_MULTIPLIER = 10;
  private static final int DEFAULT_DISTANCE = 70;
  private static final int DEFAULT_IMPACT = 10;
  private static final int DEFAULT_AREA_Y = 900;
  private static final int DEFAULT_AREA_X = 950;
  private static final int DEFAULT_SEED = 4000;
  private static final int DEFAULT_RATE = 40;
  private final int setLayoutStableAfterTime = 10000;
  private Logger graphContLogger = null;
  private Area area = null;
  private AreaTicker ticker = null;
  private Particle particle = null;
  private List<IVertex> vertizes = null;
  private List<IEdge> edges = null;
  private boolean doSoftLayout = false;
  private Random random = null;

  /**
   * Starts layout engine.
   *
   */
  public LayoutController() {
    // TODO check replacement of Logger Instance
    // this.graphContLogger =
    // gvs.common.Logger.getInstance().getGraphControllerLogger();
    this.graphContLogger = LoggerFactory.getLogger(LayoutController.class);
    vertizes = new Vector<>();
    edges = new Vector<>();
    area = new Area(new AreaDimension(DEFAULT_AREA_X, DEFAULT_AREA_Y));
    ticker = new AreaTicker(this, DEFAULT_RATE);
    ticker.start();
    graphContLogger.info("Starting graph layout controller");
    graphContLogger.debug("Starting layout guard");
    Timer guard = new Timer();
    LayoutGuard layoutGuard = new LayoutGuard(area);
    guard.schedule(layoutGuard, setLayoutStableAfterTime);
    random = new Random();
    random.setSeed(DEFAULT_SEED);
  }

  /**
   * Checks if particles in area are stable. If true, stops layouting engine,
   * waits 1500ms and displays with correct components.
   * 
   * @param rate
   *          rate
   * @param rateRatio
   *          ratio
   * @param drop
   *          drop
   * @param iteration
   *          iteration
   * @param time
   *          time
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
   * Receives vertices which have to be layouted.
   * 
   * @param vertices
   *          vertices
   * @param edges
   *          vector of edges
   * @param doSoftLayout
   *          soft layout
   */
  public void setElements(List<IVertex> vertices, List<IEdge> edges,
      boolean doSoftLayout) {

    graphContLogger.info("LayoutController has new elements detected, "
        + "start layouting procedure");
    this.doSoftLayout = doSoftLayout;
    this.vertizes = vertices;
    this.edges = edges;
    ticker.startTicking();
    createVertexParticles();
    createEdgeTractions();

    area.setAreaState(false);
  }

  /**
   * Creates a particle for each vertex.
   *
   */
  public void createVertexParticles() {
    Point p = new Point();
    Iterator<IVertex> it = vertizes.iterator();
    while (it.hasNext()) {
      IVertex myVertex = (IVertex) it.next();

      if (!myVertex.isFixedPosition()) {
        if (doSoftLayout) {
          p = generateSoftPoints(myVertex);
        } else {
          p = generateRandomPoints(myVertex);
        }
      } else {
        p = generateFixedPoints(myVertex);
      }

      particle = new Particle(new AreaPoint(p), myVertex.getId(), myVertex,
          myVertex.isFixedPosition(), DEFAULT_MASS, DEFAULT_RADIUS);
      area.addParticles(particle);

    }
  }

  /**
   * Use random coordinates as input for engine.
   * 
   * @param vertex
   *          vertex
   * @return point
   */
  private Point generateRandomPoints(IVertex vertex) {
    Point randomPoint = new Point();
    randomPoint.x = (int) ((double) (area.getUniverseDimension()
        .dimensionWidth()) * Math.random());
    randomPoint.y = (int) ((double) (area.getUniverseDimension()
        .dimensionHeight()) * Math.random());
    return randomPoint;

  }

  /**
   * Use soft random coordinates as input for engine.
   * 
   * @param vertex
   *          vertex
   * @return point
   */
  private Point generateSoftPoints(IVertex vertex) {
    Point softPoint = new Point();
    softPoint.x = (int) (random.nextDouble() * SOFT_MULTIPLIER);
    softPoint.y = (int) (random.nextDouble() * SOFT_MULTIPLIER);
    System.err.println(softPoint.y);

    return softPoint;
  }

  /**
   * Use existing vertex coordinates as input for engine.
   * 
   * @param vertex
   *          vertex
   * @return Point
   */
  private Point generateFixedPoints(IVertex vertex) {
    Point fixedPoint = new Point();
    fixedPoint.x = (int) ((double) vertex.getXPosition() * FIXED_MULTIPLIER);
    fixedPoint.y = (int) ((double) vertex.getYPosition() * FIXED_MULTIPLIER);
    return fixedPoint;

  }

  /**
   * Creates tractions between related vertices.
   *
   */
  public void createEdgeTractions() {
    Iterator<IEdge> it1 = edges.iterator();
    while (it1.hasNext()) {
      IEdge edge = (IEdge) it1.next();
      IVertex vertexFrom = edge.getStartVertex();
      IVertex vertexTo = edge.getEndVertex();

      Traction t = new Traction(area.getParticleWithID(vertexFrom.getId()),
          area.getParticleWithID(vertexTo.getId()), DEFAULT_IMPACT,
          DEFAULT_DISTANCE);

      area.addTraction(t);
    }
  }

  /**
   * Returns layoutig area.
   * 
   * @return area
   */
  public Area getUniverse() {
    return area;
  }
}
