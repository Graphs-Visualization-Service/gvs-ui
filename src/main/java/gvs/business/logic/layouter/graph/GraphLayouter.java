package gvs.business.logic.layouter.graph;

import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.Configuration;
import gvs.business.logic.Session;
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

  private static final int PARTICLE_WEIGHT = 50;
  private static final int TRACTION_IMPACT = 50;
  private static final int TRACTION_DISTANCE = 150;

  private static final int TICK_RATE_PER_SEC = 26;
  private static final int MAX_LAYOUT_DURATION_MS = 10_000;

  private static final int LAYOUT_AREA_FACTOR = 3;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphLayouter.class);

  @Inject
  public GraphLayouter(AreaTickerFactory tickerFactory) {
    this.tickerFactory = tickerFactory;

    double layouterWidth = Configuration.getWindowWidth() * LAYOUT_AREA_FACTOR;
    double layouterHeight = Configuration.getContentPaneHeight()
        * LAYOUT_AREA_FACTOR;

    AreaDimension dimension = new AreaDimension(layouterWidth, layouterHeight);
    this.area = new Area(dimension);
  }

  @Override
  public synchronized void layout(Session session, Action callback) {

    Graph firstGraph = session.getGraphs().get(0);
    if (firstGraph != null) {

      layout(firstGraph, callback);

      while (currentTicker != null) {
        try {
          wait();
        } catch (InterruptedException e) {
          logger.error("Unable to passivate thread", e);
        }
      }

      firstGraph.setLayouted(true);

      session.getGraphs().stream().filter(g -> g.isLayoutable()).forEach(g -> {
        takeOverVertexPositions(firstGraph, g);
      });
    }
  }

  /**
   * Layout the received vertices
   * 
   * @param graph
   *          graph with vertices and edges
   * @param callback
   *          callback function
   */
  @Override
  public synchronized void layout(Graph graph, Action callback) {

    if (graph.isLayoutable()) {

      logger.info("Compute layout for graph {}", graph.getId());

      while (currentTicker != null) {
        try {
          wait();
        } catch (InterruptedException e) {
          logger.error("Unable to passivate thread", e);
        }
      }

      this.completionCallback = callback;

      // reset
      graph.getVertices().forEach(v -> {
        GraphVertex graphVertex = (GraphVertex) v;
        if (!graphVertex.isUserPositioned()) {
          graphVertex.setStable(false);
        }
      });

      calculateLayout(graph);

      initializeLayoutGuard();

      startTickerThread();

    } else if (callback != null) {
      callback.execute();
    }
  }

  /**
   * Reuse vertex coordinates of former graph.
   * 
   * @param sourceGraph
   *          source graph
   * @param targetGraph
   *          target graph
   */
  @Override
  public synchronized void takeOverVertexPositions(Graph sourceGraph,
      Graph targetGraph) {

    logger.info("Take over vertex position from graph {} to {}",
        sourceGraph.getId(), targetGraph.getId());

    Map<Long, IVertex> formerVertices = sourceGraph.getVertices().stream()
        .collect(Collectors.toMap(IVertex::getId, Function.identity()));

    for (IVertex currentVertex : targetGraph.getVertices()) {
      IVertex formerVertex = formerVertices.get(currentVertex.getId());
      if (formerVertex != null) {
        currentVertex.setXPosition(formerVertex.getXPosition());
        currentVertex.setYPosition(formerVertex.getYPosition());
        currentVertex.setUserPositioned(formerVertex.isUserPositioned());
      }
    }
  }

  /**
   * Create particles and tractions.
   * 
   * @param graph
   *          graph to layout
   */
  private void calculateLayout(Graph graph) {
    // reset
    area.setIsStable(false);
    area.resetArea();

    createVertexParticles(graph.getVertices());
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
  private void startTickerThread() {
    currentTicker = tickerFactory.create(this, TICK_RATE_PER_SEC);
    logger.debug("Starting thread: {}", currentTicker.getName());
    currentTicker.start();
  }

  /**
   * The layoutguard will stop layouting process after 10s.
   */
  private void initializeLayoutGuard() {
    GraphLayoutGuard layoutGuard = new GraphLayoutGuard(area);
    guard = new Timer();
    guard.schedule(layoutGuard, MAX_LAYOUT_DURATION_MS);
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
   */
  private void createVertexParticles(Collection<IVertex> vertices) {

    vertices.forEach(vertex -> {

      GraphVertex graphVertex = (GraphVertex) vertex;

      if (!graphVertex.isUserPositioned() && !graphVertex.isStable()) {

        AreaPoint position = generateRandomPoints();
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
      GraphVertex vertexFrom = (GraphVertex) e.getStartVertex();
      GraphVertex vertexTo = (GraphVertex) e.getEndVertex();

      if (!vertexFrom.isUserPositioned() && !vertexTo.isUserPositioned()
          && !vertexFrom.isStable() && !vertexTo.isStable()) {

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
}
