package gvs.business.logic;

import java.awt.Point;
import java.util.Collection;
import java.util.Random;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.physics.helpers.Area;
import gvs.business.logic.physics.helpers.AreaDimension;
import gvs.business.logic.physics.helpers.AreaPoint;
import gvs.business.logic.physics.helpers.Particle;
import gvs.business.logic.physics.rules.Traction;
import gvs.business.logic.physics.ticker.AreaTicker;
import gvs.business.logic.physics.ticker.AreaTickerFactory;
import gvs.business.logic.physics.ticker.Tickable;
import gvs.business.model.graph.Graph;
import gvs.interfaces.Action;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;

/**
 * Creates and prepares the elements which need to be layouted.
 * 
 * Executes a separate ticker thread which calls the LayoutController in a given
 * interval.
 * 
 * @author mwieland
 *
 */
@Singleton
public class Layouter implements Tickable {

  private Action completionCallback;

  private volatile AreaTicker currentTicker;

  private final AreaTickerFactory tickerFactory;
  private final Area area;
  private final Random random;

  private static final int FACTOR = 3;

  private static final int PARTICLE_WEIGHT = 10;
  private static final int TRACTION_DISTANCE = 70 * FACTOR;
  private static final int TRACTION_IMPACT = 10;

  private static final int SOFT_MULTIPLIER = 100;
  private static final int FIXED_MULTIPLIER = 10;

  private static final int TICK_RATE_PER_SEC = 10;
  private static final int MAX_LAYOUT_DURATION_MS = 10_000;

  private static final int DEFAULT_AREA_HEIGHT = 600 * FACTOR;
  private static final int DEFAULT_AREA_WIDTH = 800 * FACTOR;

  private static final int DEFAULT_SEED = 4000;

  private static final Logger logger = LoggerFactory.getLogger(Layouter.class);

  @Inject
  public Layouter(AreaTickerFactory tickerFactory) {

    this.tickerFactory = tickerFactory;
    this.area = new Area(
        new AreaDimension(DEFAULT_AREA_WIDTH, DEFAULT_AREA_HEIGHT));
    this.random = new Random(DEFAULT_SEED);
  }

  /**
   * Layout the received vertices
   * 
   * @param graph
   *          graph with vertices and edges
   * @param useSoftPoints
   *          use soft layout
   */
  public void layoutGraph(Graph graph, boolean useSoftPoints,
      Action completionCallback) {
    logger.info("Received new data to layout");

    if (graph.isLayoutable()) {

      this.completionCallback = completionCallback;

      initializeLayoutGuard();
      handleTickerThread();

      resetArea();
      calculatLayout(graph, useSoftPoints);
    }
  }

  /**
   * Initializes the guard, which protects the layouter from running endlessly.
   */
  private void initializeLayoutGuard() {
    Timer guard = new Timer();
    LayoutGuard layoutGuard = new LayoutGuard(area);
    guard.schedule(layoutGuard, MAX_LAYOUT_DURATION_MS);
  }

  private void resetArea() {
    this.area.setIsStable(false);
    this.area.resetArea();
  }

  private void calculatLayout(Graph graph, boolean useSoftPoints) {
    createVertexParticles(graph.getVertices(), useSoftPoints);
    createEdgeTractions(graph.getEdges());
  }

  /**
   * Only one ticker thread is allowed to be active.
   * 
   * If the autolayout mechanism is executed, the ticker thread executes the
   * tick method in a defined interval.
   * 
   * As soon as the area is stable, the ticker thread terminates.
   */
  private void handleTickerThread() {
    if (currentTicker != null) {
      try {
        logger.debug("Wait for current AreaTicker thread to terminate.");
        currentTicker.join();
        logger.debug("AreaTicker thread successfully stopped.");
      } catch (InterruptedException e) {
        logger.error("Unable to join Area Ticker thread", e);
      }
    }

    currentTicker = tickerFactory.create(this, TICK_RATE_PER_SEC);
    logger.debug("Starting thread: {}", currentTicker.getName());
    currentTicker.start();
    logger.debug("Background process successfully started.");
  }

  /**
   * Check if particles in area are stable. If stable, stop ticking, otherwise
   * update positions and continue with the next iteration.
   */
  public void tick() {
    logger.info("Layout engine iteration completed.");

    if (area.isStable()) {
      logger.info("Layouting completed. Graph is stable. Stop layout engine.");
      currentTicker.terminate();

      if (completionCallback != null) {
        completionCallback.execute();
      }
    } else {
      logger.info("Continue layouting...");
      area.updateAll();
    }
  }

  /**
   * Creates a particle for each vertex.
   *
   */
  private void createVertexParticles(Collection<IVertex> vertices,
      boolean generateSoftPoints) {

    vertices.forEach(vertex -> {
      Point point = new Point();

      if (!vertex.isStable()) {
        if (generateSoftPoints) {
          point = generateSoftPoints(vertex);
        } else {
          point = generateRandomPoints(vertex);
        }
      } else {
        point = generateFixedPoints(vertex);
      }

      AreaPoint position = new AreaPoint(point);
      Particle newParticle = new Particle(position, vertex, PARTICLE_WEIGHT);

      area.addParticles(newParticle);
    });
  }

  /**
   * Create edge tractions between related vertices.
   *
   */
  private void createEdgeTractions(Collection<IEdge> edges) {
    edges.forEach(e -> {
      IVertex vertexFrom = e.getStartVertex();
      IVertex vertexTo = e.getEndVertex();

      Particle fromParticle = area.getParticleByVertexId(vertexFrom.getId());
      Particle toParticle = area.getParticleByVertexId(vertexTo.getId());

      Traction t = new Traction(fromParticle, toParticle, TRACTION_IMPACT,
          TRACTION_DISTANCE);

      area.addTraction(t);
    });
  }

  /**
   * Use random coordinates as input for engine.
   * 
   * @param vertex
   *          calculation base
   * @return random point
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
   *          calculation base
   * @return soft point
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
   *          calculation base
   * @return fixed point
   */
  private Point generateFixedPoints(IVertex vertex) {
    Point fixedPoint = new Point();
    fixedPoint.x = (int) ((double) vertex.getXPosition() * FIXED_MULTIPLIER);
    fixedPoint.y = (int) ((double) vertex.getYPosition() * FIXED_MULTIPLIER);
    return fixedPoint;
  }

}
