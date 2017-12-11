package gvs.ui.model;

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

import gvs.business.model.Graph;
import gvs.business.model.GraphHolder;
import gvs.business.model.IEdge;
import gvs.business.model.IVertex;
import gvs.ui.logic.session.SessionViewModel;
import gvs.ui.view.ScalableScrollPane;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

/**
 * Represents one snapshot of a graph visualization. Observes the
 * CurrentGraphHolder.
 * 
 * @author mtrentin
 *
 */
@Singleton
public class GraphViewModel extends Observable implements Observer {

  private GraphHolder graphHolder;
  private ScalableScrollPane graphPane;

  private final StringProperty snapshotDescriptionProperty;

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

    this.snapshotDescriptionProperty = new SimpleStringProperty();
    this.snapshotDescriptionProperty
        .addListener(this::snapshotDescriptionListener);

    currentGraphHolder.addObserver(this);
  }

  private void snapshotDescriptionListener(
      ObservableValue<? extends String> observable, String oldValue,
      String newValue) {

    if (newValue != null && oldValue != null) {
      if (!oldValue.equals(newValue)) {
        Graph currentGraph = graphHolder.getCurrentGraph();
        currentGraph.setSnapshotDescription(newValue);
      }
    }
  }

  /**
   * Redraw Graph when CurrenGraphHolder changes its Graph.
   */
  @Override
  public void update(Observable o, Object arg) {
    // Hand updates over to JavaFX Thread
    Platform.runLater(() -> {

      logger.info("Current graph changed...");
      graphHolder = (GraphHolder) o;

      // don't start drawing process, if graphPane hasn't already been set by
      // the SessionView.
      if (graphPane != null) {
        Graph currentGraph = graphHolder.getCurrentGraph();
        draw(currentGraph);
        sessionViewModel.updateButtonStates();
      } else {
        logger.warn("GraphPane has not been set before drawing!");
      }
    });

  }

  public void draw(Graph graph) {
    logger.info("Drawing graph...");
    vertexViewModels.clear();
    edgeViewModels.clear();
    graphPane.clear();

    snapshotDescriptionProperty.set(graph.getSnapshotDescription());

    drawVertices(graph.getVertices());
    drawEdges(graph.getEdges());
    correctZOrder();
  }

  private void correctZOrder() {
    vertexViewModels.values().forEach(v -> v.toFront());
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
  public void setPane(ScalableScrollPane newGraphPane) {
    this.graphPane = newGraphPane;
  }

  public StringProperty snapshotDescriptionProperty() {
    return snapshotDescriptionProperty;
  }

}
