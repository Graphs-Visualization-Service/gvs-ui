package gvs.business.logic;

import java.awt.Point;
import java.util.Random;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.logic.physics.helpers.Area;
import gvs.business.logic.physics.helpers.AreaDimension;
import gvs.business.logic.physics.helpers.AreaPoint;
import gvs.business.logic.physics.helpers.Particle;
import gvs.business.logic.physics.rules.Traction;
import gvs.business.logic.physics.ticker.AreaTicker;
import gvs.business.logic.physics.ticker.Tickable;
import gvs.business.model.graph.Graph;
import gvs.interfaces.IVertex;

/**
 * Creates and prepares the elements which have to be layouted.
 * 
 * @author aegli
 *
 */
@Singleton
public class LayoutController implements Tickable {

  private boolean generateSoftPoints = false;
  private Area area = null;
  private AreaTicker ticker = null;
  private Particle particle = null;
  private Random random = null;

  private Graph currentGraph;

  private static final int DEFAULT_MASS = 40;
  private static final int SOFT_MULTIPLIER = 10;
  private static final int FIXED_MULTIPLIER = 10;
  private static final int DEFAULT_DISTANCE = 300;
  private static final int DEFAULT_IMPACT = 10;

  private static final int MAX_LAYOUT_DURATION_MS = 10000;
  private static final int DEFAULT_TICK_RATE = 50;

  private static final int DEFAULT_AREA_HEIGHT = 1300;
  private static final int DEFAULT_AREA_WIDTH = 1600;

  private static final int DEFAULT_SEED = 4000;

  private static final Logger logger = LoggerFactory
      .getLogger(LayoutController.class);

  public LayoutController() {
    this.area = new Area(
        new AreaDimension(DEFAULT_AREA_WIDTH, DEFAULT_AREA_HEIGHT));
    this.random = new Random(DEFAULT_SEED);

    initializeLayoutGuard();
  }

  /**
   * Initializes the guard, which protects the layouter from running Infinitely.
   */
  private void initializeLayoutGuard() {
    boolean isDaemon = false;
    Timer guard = new Timer(isDaemon);
    LayoutGuard layoutGuard = new LayoutGuard(area);
    guard.schedule(layoutGuard, MAX_LAYOUT_DURATION_MS);
  }

  /**
   * Checks if particles in area are stable. If true, stop layouting engine,
   * wait 1500ms and displays with correct components.
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

    logger.info("Layout engine iteration completed.");

    if (area.isStable()) {
      logger.info("Layouting completed. Graph is stable. Stop layout engine.");
      ticker.shutdown();
    } else {
      logger.info("Continue layouting. Vertizes without positions detected");
      area.updateAll();
    }
  }

  /**
   * Receives vertices which have to be layouted.
   * 
   * @param graph
   *          graph with vertices and edges
   * @param useSoftPoints
   *          use soft layout
   */
  public void setGraphToLayout(Graph graph, boolean useSoftPoints) {
    logger.info("New data to layout detected.");

    this.currentGraph = graph;
    this.generateSoftPoints = useSoftPoints;

    this.area.setIsStable(false);
    this.area.clearParticles();

    createVertexParticles();
    createEdgeTractions();

    logger.info("Start Area Ticker");
    this.ticker = new AreaTicker(this, DEFAULT_TICK_RATE);
    ticker.activateTicker();
    ticker.start();
  }

  /**
   * Creates a particle for each vertex.
   *
   */
  private void createVertexParticles() {
    currentGraph.getVertices().forEach(v -> {

      Point point = new Point();

      if (!v.isFixedPosition()) {
        if (generateSoftPoints) {
          point = generateSoftPoints(v);
        } else {
          point = generateRandomPoints(v);
        }
      } else {
        point = generateFixedPoints(v);
      }

      AreaPoint position = new AreaPoint(point);
      long particleId = v.getId();
      boolean isFixed = v.isFixedPosition();
      particle = new Particle(position, particleId, v, isFixed, DEFAULT_MASS);

      area.addParticles(particle);
    });
  }

  /**
   * Create tractions between related vertices.
   *
   */
  private void createEdgeTractions() {
    currentGraph.getEdges().forEach(e -> {
      IVertex vertexFrom = e.getStartVertex();
      IVertex vertexTo = e.getEndVertex();

      Particle fromParticle = area.getParticleWithID(vertexFrom.getId());
      Particle toParticle = area.getParticleWithID(vertexTo.getId());

      Traction t = new Traction(fromParticle, toParticle, DEFAULT_IMPACT,
          DEFAULT_DISTANCE);

      area.addTraction(t);
    });
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
}
