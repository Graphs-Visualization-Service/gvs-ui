package gvs.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import gvs.business.logic.ApplicationController;
import gvs.business.logic.GraphSessionType;
import gvs.business.logic.ISessionType;
import gvs.business.logic.TreeSessionType;
import gvs.business.model.Edge;
import gvs.business.model.Graph;
import gvs.business.model.IEdge;
import gvs.business.model.IVertex;
import gvs.business.model.graph.GraphVertex;
import gvs.business.model.styles.GVSStyle;
import gvs.business.model.tree.DummyTreeVertex;
import gvs.business.model.tree.TreeVertex;
import gvs.util.FontAwesome.Glyph;

/**
 * This class builds the model which is required for the visualization. The
 * types (Line, Background ...) will be translated into Java-Objects
 * 
 * @author mkoller
 */
@Singleton
public class ModelBuilder {

  private final ApplicationController applicationController;
  private final Provider<GraphSessionType> graphSessionTypeProvider;
  private final Provider<TreeSessionType> treeSessionTypeProvider;

  // XML Attributes
  private static final String ATTRIBUTEID = "Id";
  private static final String LABEL = "Label";
  private static final String FILLCOLOR = "Fillcolor";
  private static final String ICON = "Icon";
  private static final String LINECOLOR = "Linecolor";
  private static final String LINESTYLE = "Linestyle";
  private static final String LINETHICKNESS = "Linethickness";

  // Graph XML Fields
  private static final String GRAPH = "Graph";
  private static final String VERTIZES = "Vertizes";
  private static final String RELATIVVERTEX = "RelativVertex";
  private static final String XPOS = "XPos";
  private static final String YPOS = "YPos";
  private static final String EDGES = "Edges";
  private static final String ISDIRECTED = "IsDirected";
  private static final String FROMVERTEX = "FromVertex";
  private static final String TOVERTEX = "ToVertex";
  private static final String ARROWPOS = "DrawArrowOnPosition";

  // Tree XML Fields
  private static final String TREE = "Tree";
  private static final String NODES = "Nodes";
  private static final String RIGTHCHILD = "Rigthchild";
  private static final String LEFTCHILD = "Leftchild";

  // Logger
  private static final Logger logger = LoggerFactory
      .getLogger(ModelBuilder.class);

  /**
   * ModelBuilder.
   */
  @Inject
  public ModelBuilder(ApplicationController appController,
      Provider<TreeSessionType> treeSessionTypeProvider,
      Provider<GraphSessionType> graphSessionTypeProvider) {
    this.applicationController = appController;
    this.treeSessionTypeProvider = treeSessionTypeProvider;
    this.graphSessionTypeProvider = graphSessionTypeProvider;
  }

  /**
   * Builds a model from the received XML.
   * 
   * @param document
   *          Document
   */
  public void buildModelFromXML(Document document) {
    logger.info("Building model from XML...");
    Element documentRootElement = document.getRootElement();

    for (Element rootElement : documentRootElement.elements()) {
      if (rootElement.getName().equals(GRAPH)) {
        logger.info("Building graph model...");
        buildGraph(documentRootElement);
      } else if (rootElement.getName().equals(TREE)) {
        logger.info("Building tree model...");
        buildTree(documentRootElement);
      } else {
        logger.info("Unknown structure detected. Import aborted.");
        break;
      }
    }
  }

  /**
   * Graph Builder.
   * 
   * @param pDocRoot
   *          documentRoot
   */
  private void buildGraph(Element pDocRoot) {
    logger.debug("Build graph from XML");

    Element graphElement = pDocRoot.element(GRAPH);

    Map<Long, IVertex> vertices = new HashMap<>();
    Element verticesElement = pDocRoot.element(VERTIZES);
    verticesElement.elements().forEach(vertexElement -> {
      IVertex newVertex = buildVertex(vertexElement);
      vertices.put(newVertex.getId(), newVertex);
    });

    Collection<IEdge> edges = new HashSet<>();
    Element edgesElement = pDocRoot.element(EDGES);
    edgesElement.elements().forEach(edgeElement -> {
      if (edgeElement.attributeValue(ISDIRECTED).equals("true")) {
        edges.add(buildDirectedEdge(edgeElement, vertices));
      } else if (edgeElement.attributeValue(ISDIRECTED).equals("false")) {
        edges.add(buildUndirectedEdge(edgeElement, vertices));
      }
    });

    // GVS 3.0 use snapshot description of input connection
    String snapshotDescription = new String();
    Graph newGraph = new Graph(snapshotDescription, vertices.values(), edges);

    logger.debug("Finish build graph from XML");
    long sessionId = Long.parseLong(graphElement.attributeValue(ATTRIBUTEID));
    String sessionName = graphElement.element(LABEL).getText();

    ISessionType type = graphSessionTypeProvider.get();
    applicationController.addGraphToSession(newGraph, sessionId, sessionName,
        type);
  }

  /**
   * TreeBuilder.
   * 
   * @param pDocRoot
   *          documentRoot
   */
  private void buildTree(Element pDocRoot) {
    logger.info("Building tree from XML...");
    Element treeElement = pDocRoot.element(TREE);
    Element eVertices = pDocRoot.element(NODES);

    long sessionId = Long.parseLong(treeElement.attributeValue(ATTRIBUTEID));
    String sessionName = treeElement.element(LABEL).getText();

    // uses a map for easy access to vertices over their id
    Map<Long, IVertex> vertexMap = buildTreeVertices(eVertices);
    Collection<IVertex> vertices = resolveChildReferences(vertexMap);
    findRoots(vertices);

    Collection<IEdge> edges = buildTreeEdges(vertices);

    // GVS 3.0 use snapshot description of input connection
    String snapshotDescription = new String();
    Graph tree = new Graph(snapshotDescription, vertices, edges);
    logger.info("Finished build tree from XML.");

    ISessionType type = treeSessionTypeProvider.get();
    applicationController.addGraphToSession(tree, sessionId, sessionName, type);
  }

  private Map<Long, IVertex> buildTreeVertices(Element eVertices) {
    Map<Long, IVertex> vertexMap = new HashMap<>();
    eVertices.elements().forEach(e -> {
      TreeVertex newVertex = buildTreeVertex(e);
      vertexMap.put(newVertex.getId(), newVertex);
    });
    return vertexMap;
  }

  private void findRoots(Collection<IVertex> vertices) {
    List<TreeVertex> treeVertices = vertices.stream().map(v -> (TreeVertex) v)
        .collect(Collectors.toList());
    List<TreeVertex> children = new ArrayList<>();
    treeVertices.forEach(v -> children.addAll(v.getChildren()));
    treeVertices.stream().filter(v -> !children.contains(v))
        .forEach(v -> v.setRoot(true));
  }

  private Collection<IVertex> resolveChildReferences(
      Map<Long, IVertex> vertexMap) {
    Collection<IVertex> vertices = vertexMap.values();

    // set child vertices
    vertices.forEach(v -> {
      TreeVertex current = (TreeVertex) v;
      current.getChildIds().forEach(id -> {
        TreeVertex child = (TreeVertex) vertexMap.get(id);
        if (child != null) {
          current.addChild(child);
        } else {
          // this is only needed for layouting
          current.addChild(new DummyTreeVertex());
        }
      });
      setParent(current);
    });
    return vertices;
  }

  /**
   * Set child relations for use in TreeLayouter
   * 
   * @param vertex
   *          parent vertex
   */
  private void setParent(TreeVertex vertex) {
    vertex.getChildren().forEach(c -> c.setParent(vertex));
  }

  private Collection<IEdge> buildTreeEdges(Collection<IVertex> vertices) {
    List<IEdge> edges = new ArrayList<>();
    vertices.forEach(v -> {
      TreeVertex current = (TreeVertex) v;
      current.getChildren().forEach(child -> {
        // don't create edges for dummy vertices
        if (child.getId() != -1) {
          edges.add(new Edge("", child.getStyle(), false, current, child));
        }
      });
    });
    return edges;
  }

  private TreeVertex buildTreeVertex(Element pVertex) {
    logger.info("Building TreeVertex from XML...");
    long vertexId = Long.parseLong(pVertex.attributeValue(ATTRIBUTEID));
    Element eLabel = pVertex.element(LABEL);
    String label = eLabel.getText();

    GVSStyle style = buildStyle(pVertex, true);

    Element eRigthChild = pVertex.element(RIGTHCHILD);
    Element eLeftChild = pVertex.element(LEFTCHILD);
    long leftchildId = -1;
    long rigthchildId = -1;
    if (eLeftChild != null) {
      leftchildId = Long.parseLong(eLeftChild.getText());
    }
    if (eRigthChild != null) {
      rigthchildId = Long.parseLong(eRigthChild.getText());
    }
    logger.info("Finish build TreeVertex from XML.");
    TreeVertex newVertex = new TreeVertex(vertexId, label, style, false, null);
    newVertex.addChildId(leftchildId);
    newVertex.addChildId(rigthchildId);
    return newVertex;
  }

  /**
   * Default Vertex Builder.
   * 
   * @param vertexElement
   *          vertex
   * @return vertex
   */
  private IVertex buildVertex(Element vertexElement) {
    logger.debug("Build DefaultVertex XML");

    double xPos = 0;
    double yPos = 0;
    if (vertexElement.getName().equals(RELATIVVERTEX)) {
      Element xPositionElement = vertexElement.element(XPOS);
      xPos = Double.parseDouble(xPositionElement.getText());
      Element yPositionElement = vertexElement.element(YPOS);
      yPos = Double.parseDouble(yPositionElement.getText());
    }

    Element labelElement = vertexElement.element(LABEL);
    String label = labelElement.getText();

    GVSStyle style = buildStyle(vertexElement, true);

    Element iconElement = vertexElement.element(ICON);
    Glyph icon = null;
    if (iconElement != null) {
      icon = Glyph.valueOf(iconElement.getText());
    }

    long vertexId = Long.parseLong(vertexElement.attributeValue(ATTRIBUTEID));

    logger.info("Finish building DefaultVertex");
    return new GraphVertex(vertexId, label, style, xPos, yPos, icon);
  }

  private GVSStyle buildStyle(Element e, boolean isVertex) {
    Element lineColorElement = e.element(LINECOLOR);
    String linecolor = lineColorElement.getText();
    Element lineStyleElement = e.element(LINESTYLE);
    String lineStyle = lineStyleElement.getText();
    Element lineThicknessElement = e.element(LINETHICKNESS);
    String lineThickness = lineThicknessElement.getText();
    String fillcolor = null;
    if (isVertex) {
      Element fillColorElement = e.element(FILLCOLOR);
      if (fillColorElement != null) {
        fillcolor = fillColorElement.getText();
      }
    }
    return new GVSStyle(linecolor, lineStyle, lineThickness, fillcolor);
  }

  /**
   * Directed Edge Builder.
   * 
   * @param pEdge
   *          edge
   * @param pVertizes
   *          vertices
   * @return edge
   */
  private IEdge buildDirectedEdge(Element pEdge, Map<Long, IVertex> pVertizes) {
    logger.debug("Build DirectedEdge XML");
    Element eLabel = pEdge.element(LABEL);
    String label = eLabel.getText();

    GVSStyle style = buildStyle(pEdge, false);

    Element eFromVertex = pEdge.element(FROMVERTEX);
    Element eToVertex = pEdge.element(TOVERTEX);
    long fromVertexId = Long.parseLong(eFromVertex.getText());
    long toVertexId = Long.parseLong(eToVertex.getText());
    IVertex fromVertex = null;
    IVertex toVertex = null;

    fromVertex = pVertizes.get(fromVertexId);
    toVertex = pVertizes.get(toVertexId);

    logger.debug("Finish build DirectedEdge XML");
    return new Edge(label, style, true, fromVertex, toVertex);
  }

  /**
   * Unidirected Edge Builder.
   * 
   * @param pEdge
   *          edge
   * @param pVertizes
   *          vertices
   * @return edge
   */
  private IEdge buildUndirectedEdge(Element pEdge,
      Map<Long, IVertex> pVertizes) {
    logger.debug("Build UndirectedEdge XML");
    int arrowPos = Integer.parseInt(pEdge.attributeValue(ARROWPOS));

    Element eLabel = pEdge.element(LABEL);
    String label = eLabel.getText();
    GVSStyle style = buildStyle(pEdge, false);
    Element eFromVertex = pEdge.element(FROMVERTEX);
    Element eToVertex = pEdge.element(TOVERTEX);
    long fromVertexId = Long.parseLong(eFromVertex.getText());
    long toVertexId = Long.parseLong(eToVertex.getText());
    IVertex fromVertex = null;
    IVertex toVertex = null;

    fromVertex = pVertizes.get(fromVertexId);
    toVertex = pVertizes.get(toVertexId);

    if (arrowPos == 1) {
      logger.debug("Finsih build UndirectedEdge XML with arrow pos 1");
      return new Edge(label, style, true, toVertex, fromVertex);
    } else if (arrowPos == 2) {
      logger.debug("Finsih build UndirectedEdge XML with arrow pos 2");
      return new Edge(label, style, true, fromVertex, toVertex);
    } else {
      logger.debug("Finsih build UndirectedEdge XML");
      return new Edge(label, style, false, fromVertex, toVertex);
    }
  }
}
