/*
 * Created on 01.12.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gvs.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import gvs.interfaces.IBinaryNode;
import gvs.interfaces.IEdge;
import gvs.interfaces.IGraphSessionController;
import gvs.interfaces.INode;
import gvs.interfaces.ISessionController;
import gvs.interfaces.ITreeSessionController;
import gvs.interfaces.IVertex;
import gvs.ui.graph.controller.GraphSessionController;
import gvs.ui.graph.model.DefaultVertex;
import gvs.ui.graph.model.Edge;
import gvs.ui.graph.model.GraphModel;
import gvs.ui.graph.model.IconVertex;
import gvs.ui.tree.controller.TreeSessionController;
import gvs.ui.tree.model.BinaryNode;
import gvs.ui.tree.model.DefaultNode;
import gvs.ui.tree.model.TreeModel;

/**
 * This class loads and saves data. The color, line ... objects will be
 * translated into the enum typ. The data will be saved in the directory
 * DataStorage
 * 
 * @author mkoller
 */
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
  private static final String DEFAULTNODE = "DefaultNode";
  private static final String BINARYNODE = "BinaryNode";
  private static final String TREEROOTID = "TreeRootId";
  private static final String RIGTHCHILD = "Rigthchild";
  private static final String LEFTCHILD = "Leftchild";

  // TODO fix save path
  // Datas
  private String path = "DataStorage\\";
  private File output = null;
  private Configuration configuration = null;

  // Logger
  private Logger commonLogger = null;

  public Persistor() {
    configuration = Configuration.getInstance();
    // TODO check logger replacement
    // commonLogger = gvs.common.Logger.getInstance().getCommenLogger();
    commonLogger = LoggerFactory.getLogger(Persistor.class);
  }

  /**
   * Saves the Sessions to the disk
   * 
   * @param pSessions
   */
  public void saveToDisk(Vector<ISessionController> pSessions) {
    commonLogger.info("Start saving ...");
    Iterator<ISessionController> sessionIt = pSessions.iterator();
    while (sessionIt.hasNext()) {
      Object tmp = sessionIt.next();

      @SuppressWarnings("rawtypes")
      Class[] interfaces = tmp.getClass().getInterfaces();
      for (int count = 0; count < interfaces.length; count++) {
        Document document = DocumentHelper.createDocument();
        Element docRoot = document.addElement(ROOT);
        if (interfaces[count] == IGraphSessionController.class) {
          GraphSessionController session = (GraphSessionController) tmp;
          this.saveGraphSession(docRoot, session);
          this.writeToDisk(document, session);
          break;
        } else if (interfaces[count] == ITreeSessionController.class) {
          TreeSessionController session = (TreeSessionController) tmp;
          this.saveTreeSession(docRoot, session);
          this.writeToDisk(document, session);
          break;
        }
      }
    }
    commonLogger.info("Finish saving");
  }

  /**
   * Load a file
   * 
   * @param pPath
   *          the file to load
   * @return the SessionController
   */
  public ISessionController loadFile(String pPath) {
    commonLogger.info("Load file: " + pPath);
    File input = new File(pPath);
    Document documentToRead = null;
    SAXReader reader = new SAXReader();
    ISessionController sessionController = null;
    try {
      documentToRead = reader.read(input);
    } catch (DocumentException e) {
      e.printStackTrace();
    }
    Element docRoot = documentToRead.getRootElement();
    Iterator<Element> contentIt = docRoot.elementIterator();
    while (contentIt.hasNext()) {
      Element eTag = (contentIt.next());
      if (eTag.getName().equals(GRAPH)) {
        commonLogger.info("It's a graph");
        sessionController = loadGraphSession(eTag);
        break;
      } else if (eTag.getName().equals(TREE)) {
        commonLogger.info("It's a tree");
        sessionController = loadTreeSession(eTag);
        break;
      }
    }
    return sessionController;
  }

  // ************************************SAVER AND
  // LOADER*************************
  private void writeToDisk(Document pDocument, ISessionController pSession) {
    try {
      String sessionName = pSession.getSessionName();
      long sessionId = pSession.getSessionId();

      Date date = new Date(sessionId);

      SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmssS");

      String dateForName = dateformat.format(date);
      // TODO fix save path for unix
      output = new File(path + sessionName + "_" + dateForName + ".gvs");

      OutputFormat format = OutputFormat.createPrettyPrint();

      XMLWriter writer = new XMLWriter(new FileOutputStream(output), format);
      writer.write(pDocument);
      writer.flush();
      writer.close();
      commonLogger.info("Session saved: " + output.getCanonicalPath());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void saveGraphSession(Element pElement,
      GraphSessionController pSessionController) {
    Element eSession = pElement.addElement(GRAPH);
    eSession.addAttribute(ATTRIBUTEID,
        String.valueOf(pSessionController.getSessionId()));
    Element eSessionName = eSession.addElement(LABEL);
    eSessionName.addText(pSessionController.getSessionName());
    Iterator<GraphModel> modelIt = pSessionController.getMyGraphModels()
        .iterator();
    while (modelIt.hasNext()) {
      GraphModel tmp = modelIt.next();
      saveGraphModel(tmp, eSession);
    }
  }

  private void saveTreeSession(Element pElement,
      TreeSessionController pSessionController) {
    Element eSession = pElement.addElement(TREE);
    eSession.addAttribute(ATTRIBUTEID,
        String.valueOf(pSessionController.getSessionId()));
    Element eSessionName = eSession.addElement(LABEL);
    eSessionName.addText(pSessionController.getSessionName());

    Iterator<TreeModel> modelIt = pSessionController.getMyGraphModels()
        .iterator();
    while (modelIt.hasNext()) {
      TreeModel tmp = modelIt.next();
      saveTreeModel(tmp, eSession);
    }
  }

  private void saveGraphModel(GraphModel pModel, Element pSession) {
    Element eGraphModel = pSession.addElement(GRAPHMODEL);
    eGraphModel.addAttribute(ATTRIBUTEID, String.valueOf(pModel.getModelId()));
    Element eGraphLabel = eGraphModel.addElement(LABEL);
    eGraphLabel.addText(pModel.getGraphLabel());
    Element eBackground = eGraphModel.addElement(BACKGROUND);
    String backgroundName = null;

    if (pModel.isHasBackgroundImage()) {
      Image tempImage = pModel.getBackgroundImage();
      backgroundName = configuration.getBackgroundName(tempImage);
    } else {
      Color tempColor = pModel.getBackgroundColor();
      backgroundName = configuration.getColorName(tempColor);
    }
    if (backgroundName == null || backgroundName == "") {
      backgroundName = STANDARD;
    }

    eBackground.addText(backgroundName);
    Element eMaxLabelLength = eGraphModel.addElement(MAXLABELLENGTH);
    eMaxLabelLength.addText(String.valueOf(pModel.getMaxLabelLength()));

    Element eVertizes = eGraphModel.addElement(VERTIZES);

    Iterator<IVertex> vertexIt = pModel.getVertizes().iterator();
    while (vertexIt.hasNext()) {
      Object temp = vertexIt.next();
      if (temp.getClass() == DefaultVertex.class) {
        saveDefaultVertex((DefaultVertex) temp, eVertizes);
      } else if (temp.getClass() == IconVertex.class) {
        saveIconVertex((IconVertex) temp, eVertizes);
      }
    }

    Element eEdges = eGraphModel.addElement(EDGES);
    Iterator<IEdge> edgeIt = pModel.getEdges().iterator();
    while (edgeIt.hasNext()) {
      Edge temp = (Edge) edgeIt.next();
      saveEdge(temp, eEdges);
    }

  }

  private void saveTreeModel(TreeModel pModel, Element pSession) {
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
    Iterator<INode> nodeIt = pModel.getNodes().iterator();
    while (nodeIt.hasNext()) {
      Object temp = nodeIt.next();
      if (temp.getClass() == DefaultNode.class) {
        // TODO check if still needed
        // saveDefaultNode((DefaultNode)temp,eNodes);
      } else if (temp.getClass() == BinaryNode.class) {
        saveBinaryNode((BinaryNode) temp, eNodes);
      }
    }
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
    eLineColor.addText(configuration.getColorName(pVertex.getLineColor()));
    Element eLineStyle = eVertex.addElement(LINESTYLE);

    BasicStroke stroke = (BasicStroke) pVertex.getLineStroke();
    eLineStyle.addText(configuration.getLineStyleName(stroke.getDashArray()));
    Element eLineThick = eVertex.addElement(LINETHICKNESS);
    eLineThick.addText(
        configuration.getLineThicknessName((int) stroke.getLineWidth()));

    Element eFillColor = eVertex.addElement(FILLCOLOR);
    eFillColor.addText(configuration.getColorName(pVertex.getFillColor()));
    Element eXPos = eVertex.addElement(XPOS);
    eXPos.addText(String.valueOf(pVertex.getXPosition()));
    Element eYPos = eVertex.addElement(YPOS);
    eYPos.addText(String.valueOf(pVertex.getYPosition()));
  }

  private void saveIconVertex(IconVertex pVertex, Element pVertizes) {
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
    eLineColor.addText(configuration.getColorName(pVertex.getLineColor()));

    BasicStroke stroke = (BasicStroke) pVertex.getLineStroke();
    Element eLineStyle = eVertex.addElement(LINESTYLE);
    eLineStyle.addText(configuration.getLineStyleName(stroke.getDashArray()));
    Element eLineThick = eVertex.addElement(LINETHICKNESS);
    eLineThick.addText(
        configuration.getLineThicknessName((int) stroke.getLineWidth()));

    Element eIcon = eVertex.addElement(ICON);
    eIcon.addText(configuration.getIconName(pVertex.getIcon()));

    Element eXPos = eVertex.addElement(XPOS);
    eXPos.addText(String.valueOf(pVertex.getXPosition()));

    Element eYPos = eVertex.addElement(YPOS);
    eYPos.addText(String.valueOf(pVertex.getYPosition()));
  }

  /*
   * Creates the Edge element
   */
  private void saveEdge(Edge pEdge, Element pEdges) {
    Element eEdge = pEdges.addElement(EDGE);
    eEdge.addAttribute(ISDIRECTED, String.valueOf(pEdge.isDirected()));

    Element eLabel = eEdge.addElement(LABEL);
    eLabel.addText(pEdge.getLabel());

    Element eLineColor = eEdge.addElement(LINECOLOR);
    eLineColor.addText(configuration.getColorName(pEdge.getLineColor()));

    BasicStroke stroke = (BasicStroke) pEdge.getLineStroke();
    Element eLineStyle = eEdge.addElement(LINESTYLE);
    eLineStyle.addText(configuration.getLineStyleName(stroke.getDashArray()));
    Element eLineThick = eEdge.addElement(LINETHICKNESS);
    eLineThick.addText(
        configuration.getLineThicknessName((int) stroke.getLineWidth()));

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

  private GraphSessionController loadGraphSession(Element pGraphSession) {
    Vector<GraphModel> graphModels = new Vector<GraphModel>();
    Element eSessionName = pGraphSession.element(LABEL);
    String sessionName = eSessionName.getText();
    long sessionId = Long.parseLong(pGraphSession.attributeValue(ATTRIBUTEID));

    Iterator<Element> modelIt = pGraphSession.elementIterator();
    while (modelIt.hasNext()) {
      Element eGraphModel = (Element) modelIt.next();
      if (eGraphModel.getName() == GRAPHMODEL) {
        int modelId = Integer.parseInt(eGraphModel.attributeValue(ATTRIBUTEID));
        String graphLabel = eGraphModel.getText();
        Element eBackground = eGraphModel.element(BACKGROUND);
        String graphBackground = eBackground.getText();
        Element eMaxLabelLength = eGraphModel.element(MAXLABELLENGTH);
        String maxLabelLength = eMaxLabelLength.getText();

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

        GraphModel gm;
        Image graphImage = configuration.getBackgroundImage(graphBackground);
        if (graphImage == null) {
          Color defaultColor = configuration.getColor(graphBackground, true);
          gm = new GraphModel(graphLabel, defaultColor, vertizes, edges,
              Integer.parseInt(maxLabelLength));
        } else {

          gm = new GraphModel(graphLabel, graphImage, vertizes, edges,
              Integer.parseInt(maxLabelLength));
        }
        gm.setModelId(modelId);
        graphModels.add(gm);
      }
    }

    return new GraphSessionController(sessionId, sessionName, graphModels);
  }

  private TreeSessionController loadTreeSession(Element pTreeSession) {
    Vector<TreeModel> treeModels = new Vector<TreeModel>();
    Element eSessionName = pTreeSession.element(LABEL);
    String sessionName = eSessionName.getText();
    long sessionId = Long.parseLong(pTreeSession.attributeValue(ATTRIBUTEID));

    Iterator<Element> modelIt = pTreeSession.elementIterator();
    while (modelIt.hasNext()) {
      Element eTreeModel = (Element) modelIt.next();
      if (eTreeModel.getName() == TREEMODEL) {
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
          } else if (eNode.getName().equals(DEFAULTNODE)) {
            // TODO check if still needed
            // nodes.add();
          }
        }

        Iterator<INode> nodesModelIt = nodes.iterator();
        while (nodesModelIt.hasNext()) {
          Object tmp = nodesModelIt.next();
          // TODO refactor -> empty else
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
          } else {
            // TODO check if still needed
            // DEFAULT NODES
          }
        }
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

        treeModels.add(new TreeModel(treeLabel, modelId,
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
    Color lineColor = configuration.getColor(linecolor, false);
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    double xpos = Double.parseDouble(eXpos.getText());
    double ypos = Double.parseDouble(eYpos.getText());
    BasicStroke lineStroke = configuration.getLineObject(linestyle,
        linethickness);

    if (eIcon != null) {
      String icon = eIcon.getText();
      Image theIcon = configuration.getIcon(icon);
      return new IconVertex(vertexId, label, lineColor, lineStroke, theIcon,
          xpos, ypos);
    } else if (eFillcolor != null) {
      String fillcolor = eFillcolor.getText();
      Color fillColor = configuration.getColor(fillcolor, false);
      return new DefaultVertex(vertexId, label, lineColor, lineStroke,
          fillColor, xpos, ypos, false);
    } else {
      return null;
    }
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
    Color lineColor = configuration.getColor(linecolor, false);
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    BasicStroke lineStroke = configuration.getLineObject(linestyle,
        linethickness);
    double xPos = Double.parseDouble(eXPos.getText());
    double yPos = Double.parseDouble(eYPos.getText());

    if (eIcon != null) {
      String icon = eIcon.getText();
      Image theIcon = configuration.getIcon(icon);
      return new IconVertex(vertexId, label, lineColor, lineStroke, theIcon,
          xPos, yPos);
    } else if (eFillcolor != null) {
      String fillcolor = eFillcolor.getText();
      Color fillColor = configuration.getColor(fillcolor, false);
      return new DefaultVertex(vertexId, label, lineColor, lineStroke,
          fillColor, xPos, yPos);
    } else {
      return null;
    }
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
    Color lineColor = configuration.getColor(linecolor, false);
    String linestyle = eLineStyle.getText();
    String linethickness = eLineThickness.getText();
    BasicStroke lineStroke = configuration.getLineObject(linestyle,
        linethickness);

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

    if (isDirected.equals("true")) {
      return new Edge(label, lineColor, lineStroke, true, fromVertex, toVertex);
    } else {
      return new Edge(label, lineColor, lineStroke, false, fromVertex,
          toVertex);
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
