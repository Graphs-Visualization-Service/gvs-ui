package gvs.access;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.graph.Session;
import gvs.business.logic.graph.SessionFactory;
import gvs.business.model.graph.DefaultVertex;
import gvs.business.model.graph.Edge;
import gvs.business.model.graph.Graph;
import gvs.business.model.styles.GVSColor;
import gvs.business.model.styles.GVSLineStyle;
import gvs.business.model.styles.GVSLineThickness;
import gvs.business.model.styles.GVSStyle;
import gvs.business.model.tree.TreeVertex;
import gvs.interfaces.IEdge;
import gvs.interfaces.ISession;
import gvs.interfaces.IVertex;
import gvs.util.FontAwesome.Glyph;

/**
 * Loads and saves sessions. While loading, it translates XML into business
 * POJOs. While saving, it reverses the process and saves an XML file at the
 * user designated path.
 * 
 * @author mkoller
 */
@Singleton
public class Persistor {

  // Generally
  private static final String ROOT = "Data";
  private static final String ATTRIBUTEID = "Id";
  private static final String LABEL = "Label";
  private static final String FILLCOLOR = "Fillcolor";
  private static final String ICON = "Icon";
  private static final String LINECOLOR = "Linecolor";
  private static final String LINESTYLE = "Linestyle";
  private static final String LINETHICKNESS = "Linethickness";

  // Graph
  private static final String GRAPH = "GraphSession";
  private static final String GRAPHMODEL = "GraphModel";
  private static final String VERTIZES = "Vertizes";
  private static final String RELATIVVERTEX = "RelativVertex";
  private static final String DEFAULTVERTEX = "DefaultVertex";
  private static final String XPOS = "XPos";
  private static final String YPOS = "YPos";
  private static final String EDGES = "Edges";
  private static final String EDGE = "Edge";
  private static final String ISDIRECTED = "IsDirected";
  private static final String FROMVERTEX = "FromVertex";
  private static final String TOVERTEX = "ToVertex";

  // Tree
  private static final String TREE = "TreeSession";
  private static final String TREEMODEL = "TreeModel";
  private static final String NODES = "Nodes";
  private static final String BINARYNODE = "BinaryNode";
  private static final String RIGTHCHILD = "Rigthchild";
  private static final String LEFTCHILD = "Leftchild";

  private final SessionFactory graphSessionFactory;

  private static final Logger logger = LoggerFactory.getLogger(Persistor.class);

  @Inject
  public Persistor(SessionFactory graphSessionFactory) {
    this.graphSessionFactory = graphSessionFactory;
  }

  public synchronized void saveToDisk(Session session, File file) {
    Document document = DocumentHelper.createDocument();
    Element docRoot = document.addElement(ROOT);
    if (session.isTreeSession()) {
      this.saveTreeSession(docRoot, session);
    } else {
      this.saveGraphSession(docRoot, session);
    }
    this.writeToDisk(document, session, file);
  }

  public ISession loadFile(String pPath) {
    logger.info("Load file: " + pPath);
    File input = new File(pPath);
    Document documentToRead = null;
    SAXReader reader = new SAXReader();
    ISession session = null;
    try {
      documentToRead = reader.read(input);
    } catch (DocumentException e) {
      logger.error("Unable to read file {}", pPath);
    }
    Element docRoot = documentToRead.getRootElement();
    Iterator<Element> contentIt = docRoot.elementIterator();
    while (contentIt.hasNext()) {
      Element eTag = (contentIt.next());
      if (GRAPH.equals(eTag.getName())) {
        logger.info("It's a graph");
        session = loadGraphSession(eTag);
        break;
      } else if (eTag.getName().equals(TREE)) {
        logger.info("It's a tree");
        session = loadTreeSession(eTag);
        break;
      }
    }
    return session;
  }

  private void writeToDisk(Document pDocument, ISession pSession, File output) {
    try {
      OutputFormat format = OutputFormat.createPrettyPrint();

      XMLWriter writer = new XMLWriter(new FileOutputStream(output), format);
      writer.write(pDocument);
      writer.flush();
      writer.close();
      logger.info("Session saved: " + output.getCanonicalPath());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void addIdAndLabelForSession(Element sessionElement,
      ISession sessionController) {
    sessionElement.addAttribute(ATTRIBUTEID,
        String.valueOf(sessionController.getId()));
    Element sessionNameElement = sessionElement.addElement(LABEL);
    sessionNameElement.addText(sessionController.getSessionName());
  }

  private void addIdAndLabelForGraph(Element graphElement, Graph graph) {
    graphElement.addAttribute(ATTRIBUTEID, String.valueOf(graph.getId()));
    Element descriptionElement = graphElement.addElement(LABEL);
    descriptionElement.addText(graph.getSnapshotDescription());
  }

  private void saveGraphSession(Element element, Session session) {
    Element sessionElement = element.addElement(GRAPH);
    addIdAndLabelForSession(sessionElement, session);
    session.getGraphs().forEach(model -> saveGraphModel(model, sessionElement));
  }

  private void saveTreeSession(Element element, Session session) {
    Element sessionElement = element.addElement(TREE);
    addIdAndLabelForSession(sessionElement, session);
    session.getGraphs().forEach(model -> saveTreeModel(model, sessionElement));
  }

  private void saveGraphModel(Graph graph, Element pSession) {
    Element graphElement = pSession.addElement(GRAPHMODEL);
    addIdAndLabelForGraph(graphElement, graph);

    Element vertexElements = graphElement.addElement(VERTIZES);
    graph.getVertices().forEach(v -> {
      saveDefaultVertex((DefaultVertex) v, vertexElements);
    });
    Element edgeElements = graphElement.addElement(EDGES);
    graph.getEdges().forEach(edge -> saveEdge(edge, edgeElements));
  }

  private void saveTreeModel(Graph graph, Element pSession) {
    Element treeElement = pSession.addElement(TREEMODEL);
    addIdAndLabelForGraph(pSession, graph);
    Element eNodes = treeElement.addElement(NODES);
    graph.getVertices().forEach(n -> {
      saveTreeVertex((TreeVertex) n, eNodes);
    });
  }

  private void saveDefaultVertex(DefaultVertex pVertex, Element pVertizes) {
    String vertexName = null;

    if (pVertex.isUserPositioned()) {
      vertexName = RELATIVVERTEX;
    } else {
      vertexName = DEFAULTVERTEX;
    }
    Element eVertex = pVertizes.addElement(vertexName);
    eVertex.addAttribute(ATTRIBUTEID, String.valueOf(pVertex.getId()));

    Element eLabel = eVertex.addElement(LABEL);
    String vertexLabel = pVertex.getLabel();

    eLabel.addText(vertexLabel);

    saveStyle(eVertex, pVertex.getStyle(), true);

    Element eXPos = eVertex.addElement(XPOS);
    eXPos.addText(String.valueOf(pVertex.getXPosition()));
    Element eYPos = eVertex.addElement(YPOS);
    eYPos.addText(String.valueOf(pVertex.getYPosition()));
  }

  /*
   * Creates the Edge element
   */
  private void saveEdge(IEdge pEdge, Element edgesElement) {
    Element edgeElement = edgesElement.addElement(EDGE);
    edgeElement.addAttribute(ISDIRECTED, String.valueOf(pEdge.isDirected()));

    Element eLabel = edgeElement.addElement(LABEL);
    eLabel.addText(pEdge.getLabel());

    saveStyle(edgeElement, pEdge.getStyle(), false);

    Element eFromVertex = edgeElement.addElement(FROMVERTEX);
    eFromVertex.addText(String.valueOf(pEdge.getStartVertex().getId()));
    Element eToVertex = edgeElement.addElement(TOVERTEX);
    eToVertex.addText(String.valueOf(pEdge.getEndVertex().getId()));
  }

  private void saveTreeVertex(TreeVertex pNode, Element pNodes) {
    Element eBinaryNode = pNodes.addElement(BINARYNODE);
    eBinaryNode.addAttribute(ATTRIBUTEID, String.valueOf(pNode.getId()));

    Element eLabel = eBinaryNode.addElement(LABEL);
    eLabel.addText(pNode.getLabel());

    saveStyle(eBinaryNode, pNode.getStyle(), true);

    Element eXPos = eBinaryNode.addElement(XPOS);
    eXPos.addText(String.valueOf(pNode.getXPosition()));
    Element eYPos = eBinaryNode.addElement(YPOS);
    eYPos.addText(String.valueOf(pNode.getYPosition()));

    // TODO: to truly make tree more general than binarytree: make changes here.
    List<TreeVertex> children = pNode.getChildren();
    if (children.size() > 0) {
      Element eLeftChild = eBinaryNode.addElement(LEFTCHILD);
      eLeftChild.addText(String.valueOf(children.get(0).getId()));
    } else if (children.size() > 1) {
      Element eRigthChild = eBinaryNode.addElement(RIGTHCHILD);
      eRigthChild.addText(String.valueOf(children.get(1).getId()));
    }
  }

  private void saveStyle(Element parent, GVSStyle style, boolean isVertex) {
    Element eLineColor = parent.addElement(LINECOLOR);
    eLineColor.addText(style.getLineColor().getColor());
    Element eLineStyle = parent.addElement(LINESTYLE);
    eLineStyle.addText(style.getLineStyle().getStyle());
    Element eLineThick = parent.addElement(LINETHICKNESS);
    eLineThick.addText(style.getLineThickness().getThickness());
    if (isVertex) {
      Element eFillColor = parent.addElement(FILLCOLOR);
      eFillColor.addText(style.getFillColor().getColor());
    }
  }

  private Session loadGraphSession(Element graphElements) {
    logger.info("Parsing Graph from XML.");

    long sessionId = Long.parseLong(graphElements.attributeValue(ATTRIBUTEID));
    String sessionName = graphElements.element(LABEL).getText();
    Session session = graphSessionFactory.createSession(sessionId, sessionName,
        false);

    graphElements.elements().forEach(graphElement -> {

      if (graphElement.getName().equals(GRAPHMODEL)) {
        Map<Long, IVertex> vertices = new HashMap<>();
        Element vertexElements = graphElement.element(VERTIZES);
        vertexElements.elements().forEach(vertexElement -> {
          IVertex newVertex = loadVertex(vertexElement);
          vertices.put(newVertex.getId(), newVertex);
        });

        List<IEdge> edges = new ArrayList<>();
        Element edgeElements = graphElement.element(EDGES);
        edgeElements.elements().forEach(edgeElement -> {
          edges.add(loadEdge(edgeElement, vertices));
        });

        // TODO should we set this graph id?
        int graphId = Integer
            .parseInt(graphElement.attributeValue(ATTRIBUTEID));
        String snapShotDescription = graphElement.element(LABEL).getText();
        Graph newGraph = new Graph(snapShotDescription, vertices.values(),
            edges);

        session.addGraph(newGraph);
      }
    });
    return session;
  }

  private Session loadTreeSession(Element graphElements) {
    logger.info("Parsing Tree from XML.");

    long sessionId = Long.parseLong(graphElements.attributeValue(ATTRIBUTEID));
    String sessionName = graphElements.element(LABEL).getText();
    Session session = graphSessionFactory.createSession(sessionId, sessionName,
        true);

    graphElements.elements().forEach(graphElement -> {

      if (graphElement.getName().equals(TREEMODEL)) {
        // uses a map for easy access to vertices over their id
        Map<Long, IVertex> vertexMap = buildTreeVertices(graphElement);

        Collection<IVertex> vertices = resolveChildReferences(vertexMap);
        findRoots(vertices);
        Collection<IEdge> edges = buildTreeEdges(vertices);

        // TODO should we set this graph id?
        int graphId = Integer
            .parseInt(graphElement.attributeValue(ATTRIBUTEID));
        String snapShotDescription = graphElement.element(LABEL).getText();
        Graph newGraph = new Graph(snapShotDescription, vertices, edges);
        session.addGraph(newGraph);
      }
    });
    session.layoutWholeSession(null);
    return session;
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

  private Map<Long, IVertex> buildTreeVertices(Element graphElement) {
    Element vertexElements = graphElement.element(NODES);
    Map<Long, IVertex> vertexMap = new HashMap<>();
    vertexElements.elements().forEach(e -> {
      TreeVertex newVertex = loadTreeVertex(e);
      vertexMap.put(newVertex.getId(), newVertex);
    });
    return vertexMap;
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

  private IVertex loadVertex(Element vertexElement) {
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

    GVSStyle style = loadStyle(vertexElement, true);

    Element iconElement = vertexElement.element(ICON);
    Glyph icon = null;
    if (iconElement != null) {
      icon = Glyph.valueOf(iconElement.getText());
    }
    long vertexId = Long.parseLong(vertexElement.attributeValue(ATTRIBUTEID));

    logger.info("Finish building DefaultVertex");
    return new DefaultVertex(vertexId, label, style, xPos, yPos, icon);
  }

  private GVSStyle loadStyle(Element vertexElement, boolean isVertex) {
    Element lineColorElement = vertexElement.element(LINECOLOR);
    String linecolor = lineColorElement.getText();
    Element lineStyleElement = vertexElement.element(LINESTYLE);
    String lineStyle = lineStyleElement.getText();
    Element lineThicknessElement = vertexElement.element(LINETHICKNESS);
    String lineThickness = lineThicknessElement.getText();
    String fillcolor = null;
    if (isVertex) {
      Element fillColorElement = vertexElement.element(FILLCOLOR);
      if (fillColorElement != null) {
        fillcolor = fillColorElement.getText();
      }
    }
    GVSStyle style = new GVSStyle(linecolor, lineStyle, lineThickness,
        fillcolor);
    return style;
  }

  private IEdge loadEdge(Element edgeElement, Map<Long, IVertex> vertices) {
    String isDirected = edgeElement.attributeValue(ISDIRECTED);

    Element eLabel = edgeElement.element(LABEL);
    String label = eLabel.getText();

    Element eFromVertex = edgeElement.element(FROMVERTEX);
    Element eToVertex = edgeElement.element(TOVERTEX);

    long fromVertexId = Long.parseLong(eFromVertex.getText());
    long toVertexId = Long.parseLong(eToVertex.getText());
    IVertex fromVertex = vertices.get(fromVertexId);
    IVertex toVertex = vertices.get(toVertexId);

    GVSStyle style = loadStyle(edgeElement, false);
    if (isDirected.equals("true")) {
      return new Edge(label, style, true, fromVertex, toVertex);
    } else {
      return new Edge(label, style, false, fromVertex, toVertex);
    }
  }

  private TreeVertex loadTreeVertex(Element pVertex) {
    logger.info("Loading TreeVertex...");
    long vertexId = Long.parseLong(pVertex.attributeValue(ATTRIBUTEID));
    Element eLabel = pVertex.element(LABEL);
    String label = eLabel.getText();

    GVSStyle style = loadStyle(pVertex, true);

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
    logger.info("Finished loading TreeVertex.");
    TreeVertex newVertex = new TreeVertex(vertexId, label, style, false, null);
    newVertex.addChildId(leftchildId);
    newVertex.addChildId(rigthchildId);
    return newVertex;
  }
}
