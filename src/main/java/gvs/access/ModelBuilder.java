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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import gvs.business.model.tree.DefaultNode;
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
  private ApplicationController applicationController = null;
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
  private static final String DEFAULTNODE = "DefaultNode";
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
   * Builds a model from the recieved XML.
   * 
   * @param document
   *          Document
   */
  public synchronized void buildModelFromXML(Document document) {
    logger.debug("Model will be built from XML");
    Element docRoot = document.getRootElement();
    Iterator<Element> contentIt = docRoot.elementIterator();
    while (contentIt.hasNext()) {
      Element eTag = (Element) (contentIt.next());
      if (eTag.getName().equals(GRAPH)) {
        logger.debug("It is a graph");
        buildGraph(docRoot);
      } else if (eTag.getName().equals(TREE)) {
        logger.debug("It is a tree");
        buildTree(docRoot);
      } else {
        break;
      }
    }
  }

  // ***************************BUILDERS
  // XML**************************************

  /**
   * Graph Builder.
   * 
   * @param pDocRoot
   *          documentRoot
   */
  private void buildGraph(Element pDocRoot) {
    logger.debug("Build graph from XML");
    Element eGraph = pDocRoot.element(GRAPH);
    long sessionId = Long.parseLong(eGraph.attributeValue(ATTRIBUTEID));
    String graphLabel = eGraph.element(LABEL).getText();
    String graphBackground = eGraph.element(BACKGROUND).getText();
    String maxLabelLengthString = eGraph.element(MAXLABELLENGTH).getText();

    Collection<IVertex> vertizes = new HashSet<IVertex>();
    Collection<IEdge> edges = new HashSet<IEdge>();

    Element eVertizes = pDocRoot.element(VERTIZES);
    Iterator<Element> vertizesIt = eVertizes.elementIterator();
    while (vertizesIt.hasNext()) {
      Element eVertex = (Element) (vertizesIt.next());
      if (eVertex.getName().equals(DEFAULTVERTEX)) {
        vertizes.add(buildDefaultVertex(eVertex));
      } else if (eVertex.getName().equals(RELATIVVERTEX)) {
        vertizes.add(buildRelativVertex(eVertex));
      }
    }
    Element eEdges = pDocRoot.element(EDGES);
    Iterator<Element> edgesIt = eEdges.elementIterator();
    while (edgesIt.hasNext()) {
      Element eEdge = (Element) (edgesIt.next());
      if (eEdge.attributeValue(ISDIRECTED).equals("true")) {
        edges.add(buildDirectedEdge(eEdge, vertizes));
      } else if (eEdge.attributeValue(ISDIRECTED).equals("false")) {
        edges.add(buildUndirectedEdge(eEdge, vertizes));
      }
    }

    int maxLabelLength = Integer.parseInt(maxLabelLengthString);
    Graph newGraph = new Graph(vertizes, edges);
    newGraph.setMaxLabelLength(maxLabelLength);

    // TODO background image support?
    // Image graphImage = typs.getBackgroundImage(graphBackground);
    // if (graphImage == null) {
    // Color defaultColor = typs.getColor(graphBackground, true);
    // gm = new Graph(graphLabel, defaultColor, vertizes, edges,
    // Integer.parseInt(maxLabelLength));
    // } else {
    // gm = new Graph(graphLabel, graphImage, vertizes, edges,
    // Integer.parseInt(maxLabelLength));
    // }

    logger.debug("Finish build graph from XML");
    applicationController.addGraphToSession(newGraph, sessionId, graphLabel);
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

    Vector<INode> nodes = new Vector<>();
    Iterator<Element> nodesIt = eNodes.elementIterator();
    while (nodesIt.hasNext()) {
      Element eNode = (Element) (nodesIt.next());
      if (eNode.getName().equals(DEFAULTNODE)) {
        nodes.add(buildDefaultNode(eNode));
      } else if (eNode.getName().equals(BINARYNODE)) {
        nodes.add(buildBinaryNode(eNode));
      }

    }

    Iterator<INode> nodesModelIt = nodes.iterator();
    while (nodesModelIt.hasNext()) {
      Object tmp = nodesModelIt.next();
      if (tmp.getClass() == BinaryNode.class) {
        BinaryNode actual = (BinaryNode) tmp;
        Iterator<INode> nodesModelIt2 = nodes.iterator();
        while (nodesModelIt2.hasNext()) {
          BinaryNode child = (BinaryNode) (nodesModelIt2.next());
          if (actual.getLeftChildId() == child.getNodeId()) {
            actual.setLeftChild(child);
          } else if (actual.getRightChildId() == child.getNodeId()) {
            actual.setRigthChild(child);
          }
        }
      } else if (tmp.getClass() == DefaultNode.class) {
        DefaultNode actual = (DefaultNode) tmp;
        Iterator<INode> nodesModelIt2 = nodes.iterator();
        while (nodesModelIt2.hasNext()) {
          DefaultNode child = (DefaultNode) (nodesModelIt2.next());
          long[] allchilds = actual.getChildIds();
          for (int count = 0; count < allchilds.length; count++) {
            if (child.getNodeId() == allchilds[count]) {
              actual.addChild(child);
            }
          }
        }
      }
    }

    INode rootNode = null;
    if (eRoot != null) {

      long rootId = Long.parseLong(eRoot.getText());
      Iterator<INode> nodeIt = nodes.iterator();
      while (nodeIt.hasNext()) {
        INode tmp = (INode) (nodeIt.next());
        if (tmp.getNodeId() == rootId) {
          rootNode = tmp;
          break;
        }
      }
    }

    logger.debug("Finish build tree from XML");
    Tree tm = new Tree(treeLabel, Integer.parseInt(maxLabelLength), Color.WHITE,
        rootNode, nodes);
    applicationController.addTreeToSession(tm, treeId, treeLabel);
  }

  /**
   * Default Node Builder.
   * 
   * @param pNode
   *          node
   * @return Node
   */
  private INode buildDefaultNode(Element pNode) {
    logger.debug("Build DefaultNode XML");
    long nodeId = Long.parseLong(pNode.attributeValue(ATTRIBUTEID));
    Element eLabel = pNode.element(LABEL);
    Element eLineColor = pNode.element(LINECOLOR);
    Element eLineStyle = pNode.element(LINESTYLE);
    Element eLineThickness = pNode.element(LINETHICKNESS);
    Element eFillcolor = pNode.element(FILLCOLOR);
    List<Element> childIds = pNode.elements(CHILDID);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    Color lineColor = typs.getColor(linecolor, false);
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    BasicStroke lineStroke = typs.getLineObject(linestyle, linethickness);
    String fillcolor = eFillcolor.getText();
    Color fillColor = typs.getColor(fillcolor, false);
    long[] childs = new long[childIds.size()];

    Iterator<Element> childIt = childIds.iterator();
    int counter = 0;
    while (childIt.hasNext()) {
      Element childIdTmp = (Element) (childIt.next());
      long childID = Long.parseLong(childIdTmp.getText());
      childs[counter] = childID;
      counter++;
    }
    logger.debug("Finihs build DefaultNode XML");
    return new DefaultNode(nodeId, label, lineColor, lineStroke, fillColor,
        childs);
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
   * @param pVertex
   *          vertex
   * @return vertex
   */
  private IVertex buildDefaultVertex(Element pVertex) {
    logger.debug("Build DefaultVertex XML");
    long vertexId = Long.parseLong(pVertex.attributeValue(ATTRIBUTEID));
    Element eLabel = pVertex.element(LABEL);
    Element eLineColor = pVertex.element(LINECOLOR);
    Element eLineStyle = pVertex.element(LINESTYLE);
    Element eLineThickness = pVertex.element(LINETHICKNESS);
    Element eFillcolor = pVertex.element(FILLCOLOR);
    Element eIcon = pVertex.element(ICON);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    Glyph icon = null;
    if (eIcon != null) {
      icon = Glyph.valueOf(eIcon.getText());
    }
    String fillcolor = null;
    if (eFillcolor != null) {
     fillcolor = eFillcolor.getText();
    }
    NodeStyle style = new NodeStyle(linecolor, linestyle, linethickness,
        fillcolor);
    logger.info("Finish building DefaultVertex");
    return new DefaultVertex(vertexId, label, style, icon);
  }

  /**
   * Relative Vertex Builder.
   * 
   * @param pVertex
   *          vertex
   * @return vertex
   */
  private IVertex buildRelativVertex(Element pVertex) {
    logger.debug("Build RelativVertex XML");
    long vertexId = Long.parseLong(pVertex.attributeValue(ATTRIBUTEID));

    Element eLabel = pVertex.element(LABEL);
    Element eLineColor = pVertex.element(LINECOLOR);
    Element eLineStyle = pVertex.element(LINESTYLE);
    Element eLineThickness = pVertex.element(LINETHICKNESS);
    Element eFillcolor = pVertex.element(FILLCOLOR);
    Element eIcon = pVertex.element(ICON);
    Element eXPos = pVertex.element(XPOS);
    Element eYPos = pVertex.element(YPOS);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    double xPos = Double.parseDouble(eXPos.getText());
    double yPos = Double.parseDouble(eYPos.getText());

    Glyph icon = null;
    if (eIcon != null) {
      icon = Glyph.valueOf(eIcon.getText());
    }
    String fillcolor = null;
    if (eFillcolor != null) {
     fillcolor = eFillcolor.getText();
    }
    logger.debug("Finish building RelativVertex");
    NodeStyle style = new NodeStyle(linecolor, linestyle, linethickness,
        fillcolor);
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
