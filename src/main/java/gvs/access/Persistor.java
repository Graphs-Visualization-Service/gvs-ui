/*
 * Created on 01.12.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gvs.access;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

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

import gvs.business.logic.graph.GraphSessionFactory;
import gvs.business.logic.graph.Session;
import gvs.business.logic.tree.TreeSessionController;
import gvs.business.model.graph.DefaultVertex;
import gvs.business.model.graph.Edge;
import gvs.business.model.graph.Graph;
import gvs.business.model.graph.NodeStyle;
import gvs.business.model.tree.BinaryNode;
import gvs.business.model.tree.Tree;
import gvs.interfaces.IBinaryNode;
import gvs.interfaces.IEdge;
import gvs.interfaces.INode;
import gvs.interfaces.ISession;
import gvs.interfaces.IVertex;
import gvs.util.FontAwesome.Glyph;

/**
 * This class loads and saves data. The color, line ... objects will be
 * translated into the enum type. The data will be saved in the directory
 * DataStorage.
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
  private static final String STANDARD = "Standard";

  // Graph
  private static final String GRAPH = "GraphSession";
  private static final String GRAPHMODEL = "GraphModel";
  private static final String BACKGROUND = "Background";
  private static final String MAXLABELLENGTH = "MaxLabelLength";
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
  private static final String TREEROOTID = "TreeRootId";
  private static final String RIGTHCHILD = "Rigthchild";
  private static final String LEFTCHILD = "Leftchild";

  private Configuration configuration;
  private final GraphSessionFactory graphSessionFactory;

  private static final Logger logger = LoggerFactory.getLogger(Persistor.class);

  @Inject
  public Persistor(GraphSessionFactory graphSessionFactory) {
    this.graphSessionFactory = graphSessionFactory;
    configuration = Configuration.getInstance();
  }

  public synchronized void saveToDisk(Session session, File file) {
    Document document = DocumentHelper.createDocument();
    Element docRoot = document.addElement(ROOT);
    this.saveGraphSession(docRoot, session);
    this.writeToDisk(document, session, file);
  }

  public synchronized void saveToDisk(TreeSessionController session,
      File file) {
    Document document = DocumentHelper.createDocument();
    Element docRoot = document.addElement(ROOT);
    this.saveTreeSession(docRoot, session);
    this.writeToDisk(document, session, file);
  }

  public ISession loadFile(String pPath) {
    logger.info("Load file: " + pPath);
    File input = new File(pPath);
    Document documentToRead = null;
    SAXReader reader = new SAXReader();
    ISession sessionController = null;
    try {
      documentToRead = reader.read(input);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
    Element docRoot = documentToRead.getRootElement();
    Iterator<Element> contentIt = docRoot.elementIterator();
    while (contentIt.hasNext()) {
      Element eTag = (contentIt.next());
      if (GRAPH.equals(eTag.getName())) {
        logger.info("It's a graph");
        sessionController = loadGraphSession(eTag);
        break;
      } else if (eTag.getName().equals(TREE)) {
        logger.info("It's a tree");
        sessionController = loadTreeSession(eTag);
        break;
      }
    }
    return sessionController;
  }

  // ************************************SAVER AND
  // LOADER*************************
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

  private void addIdAndLabel(Element sessionElement,
      ISession sessionController) {
    sessionElement.addAttribute(ATTRIBUTEID,
        String.valueOf(sessionController.getSessionId()));
    Element sessionNameElement = sessionElement.addElement(LABEL);
    sessionNameElement.addText(sessionController.getSessionName());
  }

  private void saveGraphSession(Element element, Session sessionController) {
    Element sessionElement = element.addElement(GRAPH);
    addIdAndLabel(sessionElement, sessionController);
    sessionController.getGraphs()
        .forEach(model -> saveGraphModel(model, sessionElement));
  }

  private void saveTreeSession(Element element,
      TreeSessionController sessionController) {
    Element sessionElement = element.addElement(TREE);
    addIdAndLabel(sessionElement, sessionController);
    sessionController.getMyGraphModels()
        .forEach(model -> saveTreeModel(model, sessionElement));
  }

  private void saveGraphModel(Graph graph, Element pSession) {
    Element eGraphModel = pSession.addElement(GRAPHMODEL);
    eGraphModel.addAttribute(ATTRIBUTEID, String.valueOf(graph.getId()));
    Element eGraphLabel = eGraphModel.addElement(LABEL);
    eGraphLabel.addText(graph.getSnapshotDescription());
    Element eBackground = eGraphModel.addElement(BACKGROUND);
    String backgroundName = null;

    // TODO check background support
    // if (graph.isHasBackgroundImage()) {
    // Image tempImage = graph.getBackgroundImage();
    // backgroundName = configuration.getBackgroundName(tempImage);
    // } else {
    // Color tempColor = graph.getBackgroundColor();
    // backgroundName = configuration.getColorName(tempColor);
    // }
    if (backgroundName == null || backgroundName == "") {
      backgroundName = STANDARD;
    }
    eBackground.addText(backgroundName);
    Element eMaxLabelLength = eGraphModel.addElement(MAXLABELLENGTH);
    eMaxLabelLength.addText(String.valueOf(graph.getMaxLabelLength()));

    Element eVertizes = eGraphModel.addElement(VERTIZES);
    graph.getVertices().forEach(v -> {
      saveDefaultVertex((DefaultVertex) v, eVertizes);
    });
    Element eEdges = eGraphModel.addElement(EDGES);
    graph.getEdges().forEach(e -> saveEdge(e, eEdges));

  }

  private void saveTreeModel(Tree pModel, Element pSession) {
    Element eTreeModel = pSession.addElement(TREEMODEL);
    eTreeModel.addAttribute(ATTRIBUTEID, String.valueOf(pModel.getModelId()));
    Element eTreeLabel = eTreeModel.addElement(LABEL);
    eTreeLabel.addText(pModel.getTreeLabel());
    Element eMaxLabelLength = eTreeModel.addElement(MAXLABELLENGTH);
    eMaxLabelLength.addText(String.valueOf(pModel.getMaxLabelLength()));
    INode rootNode = pModel.getRootNode();
    if (rootNode != null) {
      Element eRootNode = eTreeModel.addElement(TREEROOTID);
      eRootNode.addText(String.valueOf(rootNode.getNodeId()));
    }
    Element eNodes = eTreeModel.addElement(NODES);
    pModel.getNodes().forEach(n -> {
      if (n.getClass() == BinaryNode.class) {
        saveBinaryNode((BinaryNode) n, eNodes);
      }
    });
  }

  private void saveDefaultVertex(DefaultVertex pVertex, Element pVertizes) {
    String vertexName = null;

    if (pVertex.isRelative()) {
      vertexName = RELATIVVERTEX;
    } else {
      vertexName = DEFAULTVERTEX;
    }
    Element eVertex = pVertizes.addElement(vertexName);
    eVertex.addAttribute(ATTRIBUTEID, String.valueOf(pVertex.getId()));

    Element eLabel = eVertex.addElement(LABEL);
    String vertexLabel = pVertex.getLabel();

    eLabel.addText(vertexLabel);

    Element eLineColor = eVertex.addElement(LINECOLOR);
    eLineColor.addText(pVertex.getStyle().getLineColor().getColor());
    Element eLineStyle = eVertex.addElement(LINESTYLE);
    eLineStyle.addText(pVertex.getStyle().getLineStyle().getStyle());
    Element eLineThick = eVertex.addElement(LINETHICKNESS);
    eLineThick.addText(pVertex.getStyle().getLineThickness().getThickness());
    Element eFillColor = eVertex.addElement(FILLCOLOR);
    eFillColor.addText(pVertex.getStyle().getFillColor().getColor());
    Element eXPos = eVertex.addElement(XPOS);
    eXPos.addText(String.valueOf(pVertex.getXPosition()));
    Element eYPos = eVertex.addElement(YPOS);
    eYPos.addText(String.valueOf(pVertex.getYPosition()));
  }

  /*
   * Creates the Edge element
   */
  private void saveEdge(IEdge pEdge, Element pEdges) {
    Element eEdge = pEdges.addElement(EDGE);
    eEdge.addAttribute(ISDIRECTED, String.valueOf(pEdge.isDirected()));

    Element eLabel = eEdge.addElement(LABEL);
    eLabel.addText(pEdge.getLabel());

    Element eLineColor = eEdge.addElement(LINECOLOR);
    eLineColor.addText(pEdge.getStyle().getLineColor().getColor());

    Element eLineStyle = eEdge.addElement(LINESTYLE);
    eLineStyle.addText(pEdge.getStyle().getLineStyle().getStyle());
    Element eLineThick = eEdge.addElement(LINETHICKNESS);
    eLineThick.addText(pEdge.getStyle().getLineThickness().getThickness());

    Element eFromVertex = eEdge.addElement(FROMVERTEX);
    eFromVertex.addText(String.valueOf(pEdge.getStartVertex().getId()));
    Element eToVertex = eEdge.addElement(TOVERTEX);
    eToVertex.addText(String.valueOf(pEdge.getEndVertex().getId()));
  }

  private void saveBinaryNode(BinaryNode pNode, Element pNodes) {
    Element eBinaryNode = pNodes.addElement(BINARYNODE);
    eBinaryNode.addAttribute(ATTRIBUTEID, String.valueOf(pNode.getNodeId()));

    Element eLabel = eBinaryNode.addElement(LABEL);
    eLabel.addText(pNode.getNodeLabel());

    Element eLineColor = eBinaryNode.addElement(LINECOLOR);
    eLineColor.addText(configuration.getColorName(pNode.getLineColor()));

    BasicStroke stroke = (BasicStroke) pNode.getLineStroke();
    Element eLineStyle = eBinaryNode.addElement(LINESTYLE);
    eLineStyle.addText(configuration.getLineStyleName(stroke.getDashArray()));
    Element eLineThick = eBinaryNode.addElement(LINETHICKNESS);
    eLineThick.addText(
        configuration.getLineThicknessName((int) stroke.getLineWidth()));

    Element eFillColor = eBinaryNode.addElement(FILLCOLOR);
    eFillColor.addText(configuration.getColorName(pNode.getFillColor()));

    Element eXPos = eBinaryNode.addElement(XPOS);
    eXPos.addText(String.valueOf(pNode.getXPosition()));
    Element eYPos = eBinaryNode.addElement(YPOS);
    eYPos.addText(String.valueOf(pNode.getYPosition()));

    IBinaryNode leftChild = pNode.getLeftChild();
    IBinaryNode rigthChild = pNode.getRightChild();
    if (leftChild != null) {
      Element eLeftChild = eBinaryNode.addElement(LEFTCHILD);
      eLeftChild.addText(String.valueOf(leftChild.getNodeId()));
    }
    if (rigthChild != null) {
      Element eRigthChild = eBinaryNode.addElement(RIGTHCHILD);
      eRigthChild.addText(String.valueOf(rigthChild.getNodeId()));
    }
  }

  private Session loadGraphSession(Element pGraphSession) {
    logger.info("Parsing Graph from XML.");
    Vector<Graph> graphs = new Vector<>();
    Element eSessionName = pGraphSession.element(LABEL);
    String sessionName = eSessionName.getText();
    long sessionId = Long.parseLong(pGraphSession.attributeValue(ATTRIBUTEID));

    Iterator<Element> modelIt = pGraphSession.elementIterator();
    while (modelIt.hasNext()) {
      Element eGraphModel = (Element) modelIt.next();
      if (eGraphModel.getName().equals(GRAPHMODEL)) {
        int graphId = Integer.parseInt(eGraphModel.attributeValue(ATTRIBUTEID));
        Element eBackground = eGraphModel.element(BACKGROUND);
        String graphBackground = eBackground.getText();
        Element eMaxLabelLength = eGraphModel.element(MAXLABELLENGTH);
        String maxLabelLengthString = eMaxLabelLength.getText();

        Vector<IVertex> vertizes = new Vector<IVertex>();
        Element eVertizes = eGraphModel.element(VERTIZES);
        Iterator<Element> vertizesIt = eVertizes.elementIterator();
        while (vertizesIt.hasNext()) {
          Element eVertex = (vertizesIt.next());
          if (eVertex.getName().equals(DEFAULTVERTEX)) {
            vertizes.add(loadDefaultVertex(eVertex));
          } else if (eVertex.getName().equals(RELATIVVERTEX)) {
            vertizes.add(loadRelativVertex(eVertex));
          }
        }

        Vector<IEdge> edges = new Vector<IEdge>();
        Element eEdges = eGraphModel.element(EDGES);
        Iterator<Element> edgesIt = eEdges.elementIterator();
        while (edgesIt.hasNext()) {
          Element eEdge = (edgesIt.next());
          edges.add(loadEdge(eEdge, vertizes));
        }

        int maxLabelLength = Integer.parseInt(maxLabelLengthString);
        Graph newGraph = new Graph(vertizes, edges);
        newGraph.setMaxLabelLength(maxLabelLength);

        // TODO background image support?
        // Image graphImage = typs.getBackgroundImage(graphBackground);
        // if (graphImage == null) {
        // Color defaultColor = configuration.getColor(graphBackground, true);
        // gm = new Graph(graphLabel, defaultColor, vertizes, edges,
        // Integer.parseInt(maxLabelLength));
        // } else {
        // gm = new Graph(graphLabel, graphImage, vertizes, edges,
        // Integer.parseInt(maxLabelLength));
        // }

        graphs.add(newGraph);
      }
    }
    return graphSessionFactory.create(sessionId, sessionName, graphs);
  }

  private TreeSessionController loadTreeSession(Element pTreeSession) {
    logger.info("Parsing Tree from XML.");
    Vector<Tree> treeModels = new Vector<Tree>();
    Element eSessionName = pTreeSession.element(LABEL);
    String sessionName = eSessionName.getText();
    long sessionId = Long.parseLong(pTreeSession.attributeValue(ATTRIBUTEID));

    Iterator<Element> modelIt = pTreeSession.elementIterator();
    while (modelIt.hasNext()) {
      Element eTreeModel = (Element) modelIt.next();
      if (TREEMODEL.equals(eTreeModel.getName())) {
        int modelId = Integer.parseInt(eTreeModel.attributeValue(ATTRIBUTEID));
        String treeLabel = eTreeModel.getText();
        Element eMaxLabelLength = eTreeModel.element(MAXLABELLENGTH);
        String maxLabelLength = eMaxLabelLength.getText();

        Vector<INode> nodes = new Vector<INode>();
        Element eNodes = eTreeModel.element(NODES);
        Iterator<Element> nodeIt = eNodes.elementIterator();
        while (nodeIt.hasNext()) {
          Element eNode = (nodeIt.next());
          if (eNode.getName().equals(BINARYNODE)) {
            nodes.add(loadBinaryNode(eNode));
          }
        }

        nodes.forEach(n -> {
          if (n.getClass() == BinaryNode.class) {
            BinaryNode current = (BinaryNode) n;
            nodes.forEach(m -> {
              BinaryNode child = (BinaryNode) m;
              if (current.getLeftChildId() == child.getNodeId()) {
                current.setLeftChild(child);
              } else if (current.getRightChildId() == child.getNodeId()) {
                current.setRigthChild(child);
              }
            });
          }
        });
        /////////////////////////////////////////////////
        Element eRootNode = eTreeModel.element(TREEROOTID);
        long rootNodeId = -1;
        if (eRootNode != null) {
          Long.parseLong(eRootNode.getText());

        }
        IBinaryNode rootNode = null;
        Iterator<INode> rootNodeModelIt = nodes.iterator();
        while (rootNodeModelIt.hasNext()) {
          BinaryNode child = (BinaryNode) (rootNodeModelIt.next());
          if (rootNodeId == child.getNodeId()) {
            rootNode = child;
          }
        }

        treeModels.add(new Tree(treeLabel, modelId,
            Integer.parseInt(maxLabelLength), Color.WHITE, rootNode, nodes));
      }
    }

    return new TreeSessionController(sessionId, sessionName, treeModels);
  }

  private IVertex loadDefaultVertex(Element pVertex) {
    long vertexId = Long.parseLong(pVertex.attributeValue(ATTRIBUTEID));
    Element eLabel = pVertex.element(LABEL);
    Element eLineColor = pVertex.element(LINECOLOR);
    Element eLineStyle = pVertex.element(LINESTYLE);
    Element eLineThickness = pVertex.element(LINETHICKNESS);
    Element eFillcolor = pVertex.element(FILLCOLOR);
    Element eIcon = pVertex.element(ICON);
    Element eXpos = pVertex.element(XPOS);
    Element eYpos = pVertex.element(YPOS);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    double xpos = Double.parseDouble(eXpos.getText());
    double ypos = Double.parseDouble(eYpos.getText());
    String fillcolor = eFillcolor.getText();
    Glyph icon = null;
    if (eIcon != null) {
      icon = Glyph.valueOf(eIcon.getText());
    }
    NodeStyle style = new NodeStyle(linecolor, linestyle, linethickness,
        fillcolor);
    return new DefaultVertex(vertexId, label, style, xpos, ypos, false, icon);
  }

  private IVertex loadRelativVertex(Element pVertex) {
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
    String fillcolor = eFillcolor.getText();
    Glyph icon = null;
    if (eIcon != null) {
      icon = Glyph.valueOf(eIcon.getText());
    }
    NodeStyle style = new NodeStyle(linecolor, linestyle, linethickness,
        fillcolor);
    return new DefaultVertex(vertexId, label, style, xPos, yPos, icon);
  }

  private IEdge loadEdge(Element pEdge, Vector<IVertex> pVertizes) {
    String isDirected = pEdge.attributeValue(ISDIRECTED);
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

    long fromVertexId = Long.parseLong(eFromVertex.getText());
    long toVertexId = Long.parseLong(eToVertex.getText());
    IVertex fromVertex = null;
    IVertex toVertex = null;

    Iterator<IVertex> searchVertex = pVertizes.iterator();
    while (searchVertex.hasNext()) {
      IVertex tmp = (searchVertex.next());
      if (tmp.getId() == fromVertexId) {
        fromVertex = tmp;
      }
      if (tmp.getId() == toVertexId) {
        toVertex = tmp;
      }
    }
    NodeStyle style = new NodeStyle(linecolor, linestyle, linethickness, null);
    if (isDirected.equals("true")) {
      return new Edge(label, style, true, fromVertex, toVertex);
    } else {
      return new Edge(label, style, false, fromVertex, toVertex);
    }

  }

  private INode loadBinaryNode(Element pNode) {
    long nodeId = Long.parseLong(pNode.attributeValue(ATTRIBUTEID));
    Element eLabel = pNode.element(LABEL);
    Element eLineColor = pNode.element(LINECOLOR);
    Element eLineStyle = pNode.element(LINESTYLE);
    Element eLineThickness = pNode.element(LINETHICKNESS);
    Element eFillcolor = pNode.element(FILLCOLOR);
    Element eXPos = pNode.element(XPOS);
    Element eYPos = pNode.element(YPOS);

    Element eLeftChild = pNode.element(LEFTCHILD);
    Element eRigthChild = pNode.element(RIGTHCHILD);

    String label = eLabel.getText();
    String linecolor = eLineColor.getText();
    Color lineColor = configuration.getColor(linecolor, false);
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    BasicStroke lineStroke = configuration.getLineObject(linestyle,
        linethickness);
    String fillcolor = eFillcolor.getText();
    Color fillColor = configuration.getColor(fillcolor, false);

    long leftchildId = -1;
    long rigthchildId = -1;
    if (eLeftChild != null) {
      leftchildId = Long.parseLong(eLeftChild.getText());
    }
    if (eRigthChild != null) {
      rigthchildId = Long.parseLong(eRigthChild.getText());
    }

    double xPos = Double.parseDouble(eXPos.getText());
    double yPos = Double.parseDouble(eYPos.getText());

    return new BinaryNode(nodeId, label, lineColor, lineStroke, fillColor,
        leftchildId, rigthchildId, xPos, yPos);
  }
}
