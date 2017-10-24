package gvs.ui.model.graph;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.graph.Graph;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;
import gvs.ui.graph.model.DefaultVertex;

/**
 * Represents one snapshot of a graph visualization.
 * 
 * @author Michi
 *
 */
public class GraphViewModel {

  private List<VertexViewModel> vertexViewModels;
  private List<EdgeViewModel> edgeViewModels;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphViewModel.class);

  /**
   * Creates a new graph view model.
   * 
   * @param graph
   *          corresponding graph instance
   */
  public GraphViewModel(Graph graph) {
    this.vertexViewModels = new ArrayList<>();
    this.edgeViewModels = new ArrayList<>();

    loadGraph(graph);
  }

  /**
   * Load all graph properties.
   * 
   * @param graph
   *          business layer graph
   */
  private void loadGraph(Graph graph) {
    logger.info("Import new graph to graph view model");
    loadVertices(graph.getVertices());
    loadEdges(graph.getEdges());
  }

  /**
   * Load all vertex properties.
   * 
   * @param vertices
   *          business layer vertices
   */
  private void loadVertices(List<IVertex> vertices) {
    logger.info("Import vertices to graph view model");
    vertices.forEach(v -> {
      VertexViewModel vertexViewModel = new VertexViewModel(v);

      vertexViewModel.getXProperty().set(v.getXPosition());
      vertexViewModel.getYProperty().set(v.getYPosition());
      this.vertexViewModels.add(vertexViewModel);
    });
  }

  /**
   * Load all edge properties.
   * 
   * @param edges
   *          business layer edges
   */
  private void loadEdges(List<IEdge> edges) {
    logger.info("Import edges to graph view model");
    edges.forEach(e -> {
      EdgeViewModel edgeViewModel = new EdgeViewModel(e);
      this.edgeViewModels.add(edgeViewModel);
    });
  }
}
