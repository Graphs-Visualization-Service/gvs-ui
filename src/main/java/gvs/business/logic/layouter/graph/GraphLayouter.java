package gvs.business.logic.layouter.graph;

import java.util.Collection;
import java.util.Random;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.layouter.ILayouter;
import gvs.business.model.Graph;
import gvs.business.model.IEdge;
import gvs.business.model.IVertex;
import gvs.business.model.graph.GraphVertex;
import gvs.util.Action;

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
public class GraphLayouter implements Tickable, ILayouter {

  private Action completionCallback;

  private AreaTicker currentTicker;
  private Timer guard;

  private final AreaTickerFactory tickerFactory;
  private final Area area;

  private static final int PARTICLE_WEIGHT = 10;
  private static final int TRACTION_DISTANCE = 40;
  private static final int TRACTION_IMPACT = 5;

  private static final int SEEDED_MULTIPLIER = 100;

  private static final int TICK_RATE_PER_SEC = 26;
  private static final int MAX_LAYOUT_DURATION_MS = 10_000;

  private static final int DEFAULT_AREA_HEIGHT = 450;
  private static final int DEFAULT_AREA_WIDTH = 800;

  private static final int SEED = 4000;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphLayouter.class);

  @Inject
  public GraphLayouter(AreaTickerFactory tickerFactory) {
    this.tickerFactory = tickerFactory;
    AreaDimension dimension = new AreaDimension(DEFAULT_AREA_WIDTH,
        DEFAULT_AREA_HEIGHT);
    this.area = new Area(dimension);
  }

  /**
   * Layout the received vertices
   * 
   * @param graph
   *          graph with vertices and edges
   * @param useRandomLayout
   *          use random points
   * @param callback
   *          callback function
   */
  public void layoutGraph(Graph graph, boolean useRandomLayout,
      Action callback) {
    logger.info("Received new data to layout");

    if (graph.isLayoutable()) {

      graph.getVertices().forEach(v -> {
        GraphVertex graphVertex = (GraphVertex) v;
        if (!graphVertex.isUserPositioned()) {
          graphVertex.setStable(false);
        }
      });

      this.completionCallback = callback;

      // guard will stop layouting process after 10s
      GraphLayoutGuard layoutGuard = new GraphLayoutGuard(area);
      guard = new Timer();
      guard.schedule(layoutGuard, MAX_LAYOUT_DURATION_MS);

      try {
        startTickerThread();
      } catch (InterruptedException e) {
        logger.error("Unable to start area ticker");
      }

      calculatLayout(graph, useRandomLayout);

    } else if (callback != null) {
      callback.execute();
    }
  }

  /**
   * Create particles and tractions.
   * 
   * @param graph
   *          graph to layout
   * @param useRandomLayout
   *          use random points
   */
  private void calculatLayout(Graph graph, boolean useRandomLayout) {
    // reset
    area.setIsStable(false);
    area.resetArea();

    createVertexParticles(graph.getVertices(), useRandomLayout);
    createEdgeTractions(graph.getEdges());
  }

  /**
   * Only one ticker thread is allowed to be active.
   * 
   * If the autolayout mechanism is executed, the ticker thread executes the
   * tick method in a defined interval.
   * 
   * As soon as the area is stable, the ticker thread terminates.
   * 
   * @throws InterruptedException
   */
  private synchronized void startTickerThread() throws InterruptedException {
    while (currentTicker != null) {
      wait();
    }

    currentTicker = tickerFactory.create(this, TICK_RATE_PER_SEC);
    logger.debug("Starting thread: {}", currentTicker.getName());
    currentTicker.start();
  }

  /**
   * Check if particles in area are stable. If stable, stop ticking, otherwise
   * update positions and continue with the next iteration.
   */
  public synchronized void tick() {
    logger.info("Layout engine iteration completed.");

    if (!area.isStable()) {
      logger.info("Continue layouting...");
      area.updateAll();

    } else {
      try {
        logger
            .info("Layouting completed. Graph is stable. Stop layout engine.");
        guard.cancel();
        guard = null;
        currentTicker.terminate();
        currentTicker = null;
        notify();
      } finally {
        if (completionCallback != null) {
          completionCallback.execute();
        }
      }
    }
  }

  /**
   * Creates a particle for each vertex.
   *
   * @param vertices
   *          related vertices
   * @param useRandomLayout
   *          use random points
   */
  private void createVertexParticles(Collection<IVertex> vertices,
      boolean useRandomLayout) {

    // initialize seeded random every layout iteration
    Random seededRandom = new Random(SEED);

    vertices.forEach(vertex -> {
      GraphVertex graphVertex = (GraphVertex) vertex;
      if (!graphVertex.isUserPositioned()) {

        AreaPoint position = null;
        if (!graphVertex.isStable()) {
          if (useRandomLayout) {
            position = generateRandomPoints();
          } else {
            position = generateSeededRandomPoints(seededRandom);
          }
        }

        Particle newParticle = new Particle(position, graphVertex,
            PARTICLE_WEIGHT);
        area.addParticles(newParticle);
      }
    });
  }

  /**
   * Create edge tractions between related vertices.
   *
   * @param edges
   *          related edges
   */
  private void createEdgeTractions(Collection<IEdge> edges) {
    edges.forEach(e -> {
      IVertex vertexFrom = e.getStartVertex();
      IVertex vertexTo = e.getEndVertex();

      if (!vertexFrom.isUserPositioned() && !vertexTo.isUserPositioned()) {
        Particle fromParticle = area.getParticleByVertexId(vertexFrom.getId());
        Particle toParticle = area.getParticleByVertexId(vertexTo.getId());

        Traction traction = new Traction(fromParticle, toParticle,
            TRACTION_IMPACT, TRACTION_DISTANCE);
        area.addTraction(traction);
      }
    });
  }

  /**
   * Use random coordinates as input for engine.
   * 
   * @return random point
   */
  private AreaPoint generateRandomPoints() {
    double randomX = area.getUniverseDimension().dimensionWidth()
        * Math.random();
    double randomY = area.getUniverseDimension().dimensionHeight()
        * Math.random();

    return new AreaPoint(randomX, randomY);
  }

  /**
   * Use always the same random coordinates as input for engine.
   * 
   * The seeded Random class generates always the same sequence of randoms.
   * 
   * @param seededRandom
   *          pseudo random object, that returns always the same randoms
   * @return seeded random point
   */
  private AreaPoint generateSeededRandomPoints(Random seededRandom) {
    double randomX = seededRandom.nextDouble() * SEEDED_MULTIPLIER;
    double randomY = seededRandom.nextDouble() * SEEDED_MULTIPLIER;

    return new AreaPoint(randomX, randomY);
  }

}
