package gvs.ui.model.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.model.graph.GraphHolder;
import gvs.business.model.graph.Graph;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;
import gvs.ui.logic.session.SessionViewModel;
import javafx.application.Platform;
import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 * Represents one snapshot of a graph visualization. Observes the
 * CurrentGraphHolder.
 * 
 * @author mtrentin
 *
 */
@Singleton
public class GraphViewModel extends Observable implements Observer {
  private ScalableContentPane graphPane;

  private final Map<Long, VertexViewModel> vertexViewModels;
  private final Set<EdgeViewModel> edgeViewModels;
  private final SessionViewModel sessionViewModel;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphViewModel.class);

  @Inject
  public GraphViewModel(GraphHolder currentGraphHolder,
      SessionViewModel sessionViewModel) {
    logger.info("Initializing GraphViewModel.");
    this.sessionViewModel = sessionViewModel;
    this.vertexViewModels = new HashMap<>();
    this.edgeViewModels = new HashSet<>();

    currentGraphHolder.addObserver(this);
  }

  /**
   * Redraw Graph when CurrenGraphHolder changes its Graph.
   */
  @Override
  public void update(Observable o, Object arg) {
    // Hand updates over to JavaFX Thread
    Platform.runLater(() -> {
      logger.info("Current graph changed...");
      GraphHolder currentGraphHolder = (GraphHolder) o;
      // don't start drawing process, if graphPane hasn't already been set by
      // SessionView
      if (graphPane != null) {
        draw(currentGraphHolder.getCurrentGraph());
        sessionViewModel.updateStepProperties();
      } else {
        logger.warn("GraphPane has not been set before drawing!");
      }
    });

  }

  public void draw(Graph graph) {
    logger.info("Drawing graph...");
    vertexViewModels.clear();
    edgeViewModels.clear();
    graphPane.getContentPane().getChildren().clear();

    drawVertices(graph.getVertices());
    drawEdges(graph.getEdges());
    correctZOrder();

    graphPane.requestScale();
  }

  private void correctZOrder() {
    vertexViewModels.values().forEach(v -> v.getNode().toFront());
  }

  private void drawVertices(Collection<IVertex> vertices) {
    logger.info("Drawing vertices...");
    vertices.forEach(v -> {
      VertexViewModel vertexViewModel = new VertexViewModel(v);
      vertexViewModel.draw(graphPane);
      vertexViewModels.put(v.getId(), vertexViewModel);
    });
  }

  private void drawEdges(Collection<IEdge> edges) {
    logger.info("Drawing edges...");
    edges.forEach(e -> {
      VertexViewModel startVertex = vertexViewModels
          .get(e.getStartVertex().getId());
      VertexViewModel endVertex = vertexViewModels
          .get(e.getEndVertex().getId());

      EdgeViewModel edgeViewModel = new EdgeViewModel(e, startVertex,
          endVertex);
      edgeViewModel.draw(graphPane);
      edgeViewModels.add(edgeViewModel);
    });
  }

  public Collection<VertexViewModel> getVertexViewModels() {
    return this.vertexViewModels.values();
  }

  public Set<EdgeViewModel> getEdgeViewModels() {
    return this.edgeViewModels;
  }

  /**
   * Set pane on which all graph elements are drawn.
   * 
   * @param newGraphPane
   *          the pane on which all graph elements are drawn.
   */
  public void setPane(ScalableContentPane newGraphPane) {
    this.graphPane = newGraphPane;
  }

}
