/*
 * Created on 23.11.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gvs.access;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import gvs.business.model.graph.NodeStyle;
import gvs.business.model.tree.BinaryNode;
import gvs.business.model.tree.Tree;
import gvs.interfaces.IBinaryNode;
import gvs.interfaces.IEdge;
import gvs.interfaces.INode;
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
  private Configuration typs = null;

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
  private static final String BACKGROUND = "Background";
  private static final String MAXLABELLENGTH = "MaxLabelLength";
  private static final String VERTIZES = "Vertizes";
  private static final String RELATIVVERTEX = "RelativVertex";
  private static final String DEFAULTVERTEX = "DefaultVertex";
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
  private static final String BINARYNODE = "BinaryNode";
  private static final String TREEROOTID = "TreeRootId";
  private static final String CHILDID = "Childid";
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
    typs = Configuration.getInstance();
  }

  /**
   * Builds a model from the received XML.
   * 
   * @param document
   *          Document
   */
  public void buildModelFromXML(Document document) {
    logger.debug("Model will be built from XML");
    Element documentRootElement = document.getRootElement();

    for (Element rootElement : documentRootElement.elements()) {
      if (rootElement.getName().equals(GRAPH)) {
        logger.debug("It is a graph");
        buildGraph(documentRootElement);
      } else if (rootElement.getName().equals(TREE)) {
        logger.debug("It is a tree");
        buildTree(documentRootElement);
      } else {
        logger.debug("Unknown structure detected. Import aborted.");
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

    Graph newGraph = new Graph(vertizes, edges);

    logger.debug("Finish build graph from XML");
    long sessionId = Long.parseLong(graphElement.attributeValue(ATTRIBUTEID));
    String sessionName = graphElement.element(LABEL).getText();
    applicationController.addGraphToSession(newGraph, sessionId, sessionName);
  }

  /**
   * TreeBuilder.
   * 
   * @param pDocRoot
   *          documentRoot
   */
  private void buildTree(Element pDocRoot) {
    logger.debug("Build tree from XML");
    Element eTree = pDocRoot.element(TREE);
    Element eNodes = pDocRoot.element(NODES);
    Element eRoot = eTree.element(TREEROOTID);

    long treeId = Long.parseLong(eTree.attributeValue(ATTRIBUTEID));
    String treeLabel = eTree.element(LABEL).getText();
    String maxLabelLength = eTree.element(MAXLABELLENGTH).getText();

    // build nodes
    Map<Long, INode> nodeMap = new HashMap<>();
    Iterator<Element> nodesIt = eNodes.elementIterator();
    while (nodesIt.hasNext()) {
      IBinaryNode newNode = buildBinaryNode(nodesIt.next());
      nodeMap.put(newNode.getNodeId(), newNode);
    }

    // set child nodes
    nodeMap.values().forEach(n -> {
      BinaryNode currentNode = (BinaryNode) n;
      currentNode
          .setLeftChild((BinaryNode) nodeMap.get(currentNode.getLeftChildId()));
      currentNode.setRightChild(
          (BinaryNode) nodeMap.get(currentNode.getRightChildId()));
    });

    INode rootNode = null;
    if (eRoot != null) {
      long rootId = Long.parseLong(eRoot.getText());
      rootNode = nodeMap.get(rootId);
    }

    logger.debug("Finish build tree from XML");
    Tree tm = new Tree(treeLabel, Integer.parseInt(maxLabelLength), Color.WHITE,
        rootNode, nodeMap.values());
    applicationController.addTreeToSession(tm, treeId, treeLabel);
  }

  /**
   * Binary Node Builder.
   * 
   * @param pNode
   *          node
   * @return binaryNode
   */
  private IBinaryNode buildBinaryNode(Element pNode) {
    logger.debug("Build BinaryNode XML");
    long nodeId = Long.parseLong(pNode.attributeValue(ATTRIBUTEID));
    Element eLabel = pNode.element(LABEL);
    Element eLineColor = pNode.element(LINECOLOR);
    Element eLineStyle = pNode.element(LINESTYLE);
    Element eLineThickness = pNode.element(LINETHICKNESS);
    Element eFillcolor = pNode.element(FILLCOLOR);
    Element eRigthChild = pNode.element(RIGTHCHILD);
    Element eLeftChild = pNode.element(LEFTCHILD);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    Color lineColor = typs.getColor(linecolor, false);
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    BasicStroke lineStroke = typs.getLineObject(linestyle, linethickness);
    String fillcolor = eFillcolor.getText();
    Color fillColor = typs.getColor(fillcolor, false);

    long leftchildId = -1;
    long rigthchildId = -1;
    if (eLeftChild != null) {
      leftchildId = Long.parseLong(eLeftChild.getText());
    }
    if (eRigthChild != null) {
      rigthchildId = Long.parseLong(eRigthChild.getText());
    }
    logger.debug("Finish build BinaryNode XML");
    return new BinaryNode(nodeId, label, lineColor, lineStroke, fillColor,
        leftchildId, rigthchildId);
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

    Element lineColorElement = vertexElement.element(LINECOLOR);
    String linecolor = lineColorElement.getText();

    Element lineStyleElement = vertexElement.element(LINESTYLE);
    String lineStyle = lineStyleElement.getText();

    Element lineThicknessElement = vertexElement.element(LINETHICKNESS);
    String lineThickness = lineThicknessElement.getText();

    Element iconElement = vertexElement.element(ICON);
    Glyph icon = null;
    if (iconElement != null) {
      icon = Glyph.valueOf(iconElement.getText());
    }

    Element fillColorElement = vertexElement.element(FILLCOLOR);
    String fillcolor = null;
    if (fillColorElement != null) {
      fillcolor = fillColorElement.getText();
    }

    long vertexId = Long.parseLong(vertexElement.attributeValue(ATTRIBUTEID));
    NodeStyle style = new NodeStyle(linecolor, lineStyle, lineThickness,
        fillcolor);
    logger.info("Finish building DefaultVertex");
    return new DefaultVertex(vertexId, label, style, xPos, yPos, icon);
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
    Element eLineColor = pEdge.element(LINECOLOR);
    Element eLineStyle = pEdge.element(LINESTYLE);
    Element eLineThickness = pEdge.element(LINETHICKNESS);
    Element eFromVertex = pEdge.element(FROMVERTEX);
    Element eToVertex = pEdge.element(TOVERTEX);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();

    NodeStyle style = new NodeStyle(linecolor, linestyle, linethickness, null);

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
    return new Edge(label,
        new NodeStyle(linecolor, linestyle, linethickness, null), true,
        fromVertex, toVertex);

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
    Element eLineColor = pEdge.element(LINECOLOR);
    Element eLineStyle = pEdge.element(LINESTYLE);
    Element eLineThickness = pEdge.element(LINETHICKNESS);
    Element eFromVertex = pEdge.element(FROMVERTEX);
    Element eToVertex = pEdge.element(TOVERTEX);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    NodeStyle style = new NodeStyle(linecolor, linestyle, linethickness, null);

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
