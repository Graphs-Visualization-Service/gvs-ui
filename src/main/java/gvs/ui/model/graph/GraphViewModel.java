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

import gvs.business.model.graph.CurrentGraphHolder;
import gvs.business.model.graph.Graph;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;
import gvs.ui.logic.session.SessionViewModel;

/**
 * Represents one snapshot of a graph visualization. Observes the CurrentGraphHolder.
 * 
 * @author Michi
 *
 */
@Singleton
public class GraphViewModel extends Observable implements Observer {

  private final Map<Long, VertexViewModel> vertexViewModels;
  private final Set<EdgeViewModel> edgeViewModels;

  private final SessionViewModel sessionViewModel;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphViewModel.class);

  @Inject
  public GraphViewModel(CurrentGraphHolder currentGraphHolder,
      SessionViewModel sessionViewModel) {
    this.sessionViewModel = sessionViewModel;
    this.vertexViewModels = new HashMap<>();
    this.edgeViewModels = new HashSet<>();

    currentGraphHolder.addObserver(this);
  }

  @Override
  public void update(Observable o, Object arg) {
    CurrentGraphHolder currentGraphHolder = (CurrentGraphHolder) o;
    transformGraphModel(currentGraphHolder.getCurrentGraph());
    sessionViewModel.updateStepProperties();
  }

  /**
   * Load all graph properties.
   * 
   * @param graph
   *          business layer graph
   */
  public void transformGraphModel(Graph graph) {
    logger.info("Import new graph to graph view model.");
    vertexViewModels.clear();
    edgeViewModels.clear();

    importGraph(graph);

    logger.info("Graph imported. Notify session view.");
    setChanged();
    notifyObservers();
  }

  /**
   * Import vertices and edges of a graph.
   * 
   * @param graph
   *          business layer graph
   */
  private void importGraph(Graph graph) {
    importVertices(graph.getVertices());
    importEdges(graph.getEdges());
  }

  private void importVertices(Collection<IVertex> vertices) {
    logger.info("Import vertices to graph view model.");
    vertices.forEach(v -> {
      VertexViewModel vertexViewModel = new VertexViewModel(v);
      vertexViewModels.put(v.getId(), vertexViewModel);
    });
  }

  private void importEdges(Collection<IEdge> edges) {
    logger.info("Import edges to graph view model.");
    edges.forEach(e -> {
      VertexViewModel startVertex = vertexViewModels
          .get(e.getStartVertex().getId());
      VertexViewModel endVertex = vertexViewModels
          .get(e.getEndVertex().getId());

      EdgeViewModel edgeViewModel = new EdgeViewModel(e, startVertex,
          endVertex);
      edgeViewModels.add(edgeViewModel);
    });
  }

  public Collection<VertexViewModel> getVertexViewModels() {
    return this.vertexViewModels.values();
  }

  public Set<EdgeViewModel> getEdgeViewModels() {
    return this.edgeViewModels;
  }

}
