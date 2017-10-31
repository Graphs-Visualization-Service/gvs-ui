package gvs.ui.model.graph;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import gvs.business.model.graph.Graph;
import gvs.business.model.tree.Tree;
import gvs.interfaces.IVertex;
import gvs.ui.logic.session.SessionViewModel;

/**
 * Represents one snapshot of a graph visualization.
 * 
 * @author Michi
 *
 */
@Singleton
public class GraphViewModel extends Observable {

  private Set<VertexViewModel> vertexViewModels;
  private Set<EdgeViewModel> edgeViewModels;

  private static final Logger logger = LoggerFactory
      .getLogger(GraphViewModel.class);

  public GraphViewModel() {
    this.vertexViewModels = new HashSet<>();
    this.edgeViewModels = new HashSet<>();
  }

  public void transformTreeModel(Tree tree) {

  }

  /**
   * Load all graph properties.
   * 
   * @param graph
   *          business layer graph
   */
  public void transformGraphModel(Graph graph) {
    logger.info("Import new graph to graph view model");
    importGraph(graph);

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
    logger.info("Import edges to graph view model");
    graph.getEdges().forEach(e -> {
      EdgeViewModel edgeViewModel = new EdgeViewModel(e);
      this.edgeViewModels.add(edgeViewModel);

      IVertex startVertex = e.getStartVertex();
      VertexViewModel vertexViewModel = new VertexViewModel(startVertex);

      this.vertexViewModels.add(vertexViewModel);
    });
  }

  public Set<VertexViewModel> getVertexViewModels() {
    return vertexViewModels;
  }

  public void setVertexViewModels(Set<VertexViewModel> vertexViewModels) {
    this.vertexViewModels = vertexViewModels;
  }

  public Set<EdgeViewModel> getEdgeViewModels() {
    return edgeViewModels;
  }

  public void setEdgeViewModels(Set<EdgeViewModel> edgeViewModels) {
    this.edgeViewModels = edgeViewModels;
  }

}
