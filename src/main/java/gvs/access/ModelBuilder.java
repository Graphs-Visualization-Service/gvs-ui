/*
 * Created on 23.11.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gvs.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.ApplicationController;
import gvs.business.model.graph.DefaultVertex;
import gvs.business.model.graph.Edge;
import gvs.business.model.graph.Graph;
import gvs.business.model.styles.GVSColor;
import gvs.business.model.styles.GVSLineStyle;
import gvs.business.model.styles.GVSLineThickness;
import gvs.business.model.styles.GVSStyle;
import gvs.business.model.tree.TreeVertex;
import gvs.interfaces.IEdge;
import gvs.interfaces.IVertex;
import gvs.util.FontAwesome.Glyph;

/**
 * This class builds the model which is required for the visualization. The
 * types (Line, Background ...) will be translated into Java-Objects
 * 
 * @author mkoller
 */
@Singleton
public class ModelBuilder {

  // Visualization-Service
  private ApplicationController applicationController;

  // Generally
  private static final String ATTRIBUTEID = "Id";
  private static final String LABEL = "Label";
  private static final String FILLCOLOR = "Fillcolor";
  private static final String ICON = "Icon";
  private static final String LINECOLOR = "Linecolor";
  private static final String LINESTYLE = "Linestyle";
  private static final String LINETHICKNESS = "Linethickness";

  // Graph
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

  // Tree
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
  public ModelBuilder(ApplicationController appController) {
    this.applicationController = appController;
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

    Collection<IVertex> vertizes = new HashSet<IVertex>();
    Element verticesElement = pDocRoot.element(VERTIZES);
    verticesElement.elements().forEach(vertexElement -> {
      IVertex newVertex = buildVertex(vertexElement);
      vertizes.add(newVertex);
    });

    Collection<IEdge> edges = new HashSet<IEdge>();
    Element edgesElement = pDocRoot.element(EDGES);
    edgesElement.elements().forEach(edgeElement -> {
      if (edgeElement.attributeValue(ISDIRECTED).equals("true")) {
        edges.add(buildDirectedEdge(edgeElement, vertizes));
      } else if (edgeElement.attributeValue(ISDIRECTED).equals("false")) {
        edges.add(buildUndirectedEdge(edgeElement, vertizes));
      }
    });

    String snapShotDescription = graphElement.element(LABEL).getText();
    Graph newGraph = new Graph(snapShotDescription, vertizes, edges);

    logger.debug("Finish build graph from XML");
    long sessionId = Long.parseLong(graphElement.attributeValue(ATTRIBUTEID));
    String sessionName = graphElement.element(LABEL).getText();
    applicationController.addGraphToSession(newGraph, sessionId, sessionName,
        false);
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

    String snapShotDescription = treeElement.element(LABEL).getText();
    Graph tree = new Graph(snapShotDescription, vertices, edges);
    logger.info("Finished build tree from XML.");
    applicationController.addGraphToSession(tree, sessionId, sessionName, true);
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
        }
      });
      setChildRelations(current);
    });
    return vertices;
  }

  /**
   * Set child relations for use in TreeLayouter
   * 
   * @param vertex
   *          parent vertex
   */
  private void setChildRelations(TreeVertex vertex) {
    vertex.getChildren().forEach(c -> c.setParent(vertex));
  }

  private Collection<IEdge> buildTreeEdges(Collection<IVertex> vertices) {
    List<IEdge> edges = new ArrayList<>();
    vertices.forEach(v -> {
      TreeVertex current = (TreeVertex) v;
      current.getChildren().forEach(child -> {
        // TODO: what to do with label and style
        GVSStyle standardStyle = new GVSStyle(GVSColor.STANDARD,
            GVSLineStyle.THROUGH, GVSLineThickness.STANDARD, null);
        edges.add(new Edge("", standardStyle, false, current, child));
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
    return new DefaultVertex(vertexId, label, style, xPos, yPos, icon);
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
  private IEdge buildDirectedEdge(Element pEdge,
      Collection<IVertex> pVertizes) {
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

    Iterator<IVertex> searchVertex = pVertizes.iterator();
    while (searchVertex.hasNext()) {
      IVertex tmp = (IVertex) (searchVertex.next());
      if (tmp.getId() == fromVertexId) {
        fromVertex = tmp;
      }
      if (tmp.getId() == toVertexId) {
        toVertex = tmp;
      }
    }
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
      Collection<IVertex> pVertizes) {
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

    // TODO: put vertex into map -> access easy over key ID -> no iteration
    // needed
    Iterator<IVertex> searchVertex = pVertizes.iterator();
    while (searchVertex.hasNext()) {
      IVertex tmp = (IVertex) (searchVertex.next());
      if (tmp.getId() == fromVertexId) {
        fromVertex = tmp;
      }
      if (tmp.getId() == toVertexId) {
        toVertex = tmp;
      }
    }
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
