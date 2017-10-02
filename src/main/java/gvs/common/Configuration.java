
package gvs.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * The Configuration class loads and takes the values from XML.
 * 
 * @author mkoller
 */
public class Configuration {

  // Singelton
  private static Configuration typs = null;

  // Configuration
  private Document configDocument = null;

  // Config file
  private File config = null;
  private String CONFIGFILEPROPERTIE = "GVSConfig";

  // Document
  private final String TYP = "Typ";
  private final String SERVER = "Server";
  private final String MAXLABELLENGTH = "MaxLabelLength";
  private final String LOGGING = "Logging";
  private final String LAYOUTDELAY = "LayoutDelay";

  // Logging
  private final String WRITECONSOLE = "WriteConsole";
  private final String WRITEFILE = "WriteFile";
  private final String REPLACEFILE = "replaceFile";
  private final String LOGGERS = "Loggers";

  // Server
  private final String STARTPORT = "StartPort";
  private final String COMMUFILE = "CommunicationFile";
  private final String SERVERTYP = "ServerTyp";
  private final String CORBATIMEOUT = "CorbaTimeout";
  public static final String SOCKET = "Socket";
  public static final String CORBA = "Corba";

  // Colors
  private final String COLORS = "Colors";
  private final String R = "r";
  private final String G = "g";
  private final String B = "b";

  // Linestyle
  private final String LINESTYLE = "Linestyle";
  private final String DRAW = "draw";
  private final String SPACE = "space";

  // Linewidth
  private final String LINETHICKNESS = "Linethickness";
  private final String WIDTH = "width";

  // Icons
  private final String ICONS = "Icons";
  private final String PATH = "path";

  // Backgrounds
  private final String BACKGROUND = "Backgroundimages";

  // Datas for mapping enumnames to objects
  private HashMap<String, Color> colors = null;
  private HashMap<String, float[]> linestyle = null;
  private HashMap<String, Integer> linethickness = null;
  private HashMap<String, Image> backgroundimage = null;
  private HashMap<String, Image> icons = null;

  // Datas for mapping objects to enumnames
  private HashMap<Color, String> colorsReversal = null;
  private HashMap<float[], String> linestyleReversal = null;
  private HashMap<Integer, String> linethicknessReversal = null;
  private HashMap<Image, String> backgroundimageReversal = null;
  private HashMap<Image, String> iconsReversal = null;

  // Communication
  private String commFilePath = "";
  private String startPort = "";
  private String serverTyp = "";
  private int corbaTimeout = 9000;

  // Loggers
  private boolean writeToConsole = false;
  private boolean appendToFile = true;
  private String traceFilePath = "GVSTrace.txt";
  private HashMap<String, String> loggers = null;

  // The absolut maxlabellength
  private int maxLabelLength = 8;
  private int layoutDelay = 1500;

  // Defautlvalues
  private Color defaultColor = Color.ORANGE;
  private BasicStroke defaultStroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
      BasicStroke.JOIN_BEVEL, 1, new float[] { 1, 0 }, 0);
  private Color defaultBackgroundColor = Color.WHITE;

  // Logger
  private Logger commonLogger = null;

  /**
   * Loads the values from the config-file.
   *
   */
  private Configuration() {
    // TODO: check usage of gvs.common.logger
    // commonLogger=gvs.common.Logger.getInstance().getCommenLogger();
    commonLogger = LoggerFactory.getLogger(Configuration.class);
    colors = new HashMap<String, Color>();
    linestyle = new HashMap<String, float[]>();
    linethickness = new HashMap<String, Integer>();
    backgroundimage = new HashMap<String, Image>();
    icons = new HashMap<String, Image>();
    loggers = new HashMap<String, String>();

    colorsReversal = new HashMap<Color, String>();
    linestyleReversal = new HashMap<float[], String>();
    linethicknessReversal = new HashMap<Integer, String>();
    backgroundimageReversal = new HashMap<Image, String>();
    iconsReversal = new HashMap<Image, String>();

    // ******************GO!!!!!*********************
    commonLogger.info("Load configuration...");
    SAXReader reader = new SAXReader();
    String propConfig = System.getProperty(CONFIGFILEPROPERTIE);
    if (propConfig != null) {
      commonLogger.info("Configfile from properties " + propConfig);
      config = new File(propConfig);
    } else {
      commonLogger.info("Load config from currentdirectory.");
      config = new File("config.xml");
    }

    try {
      commonLogger.info("Build configuration");
      // Build configuration
      configDocument = reader.read(config);
      Element docRoot = configDocument.getRootElement();
      Element eTyp = docRoot.element(TYP);
      Element eServer = docRoot.element(SERVER);
      Element eLogging = docRoot.element(LOGGING);

      loadColors(eTyp);
      loadLineStyles(eTyp);
      loadLineThickness(eTyp);
      loadIcons(eTyp);
      loadBackgrounds(eTyp);
      loadMaxLabelLength(docRoot);
      loadLayoutDelay(docRoot);

      loadCommFilePath(eServer);
      loadStartPort(eServer);
      loadServerTyp(eServer);

      loadConsoleOutput(eLogging);
      loadFileOutput(eLogging);
      loadLoggers(eLogging);

    } catch (DocumentException e) {
      // TODO: check alternativ for 'fatal' level ->
      // https://www.slf4j.org/faq.html#fatal
      // commonLogger.fatal("No configuration found. System exit");
      Marker fatal = MarkerFactory.getMarker("FATAL");
      commonLogger.error(fatal, "No configuration found. System exit");
      System.exit(0);
    }
  }

  public synchronized static Configuration getInstance() {
    if (typs == null) {
      typs = new Configuration();
    }
    return typs;
  }

  /**
   * Returns the colorobject
   * 
   * @param pName
   *          colorname
   * @param backgroundfailed
   *          has you try to load a background?
   * @return the color or default
   */
  public synchronized Color getColor(String pName, boolean backgroundfailed) {
    Color theColor = (Color) colors.get(pName);
    if (theColor == null) {
      if (backgroundfailed) {
        theColor = defaultBackgroundColor;
        commonLogger.warn("Background not found. Set defaultcolor");
      } else {
        theColor = defaultColor;
        commonLogger.warn("Color not found. Set defaultcolor");
      }
    }
    return theColor;
  }

  /**
   * Returns the Strokeobject
   * 
   * @param pLinestyle
   * @param pLineThickness
   * @return the strokeobject
   */
  public synchronized BasicStroke getLineObject(String pLinestyle,
      String pLineThickness) {
    float[] dash = linestyle.get(pLinestyle);
    Integer width = linethickness.get(pLineThickness);
    if (dash == null || width == null) {
      commonLogger.warn("Linestroke not found. Set defaultstroke");
      return defaultStroke;
    } else {
      return new BasicStroke(width, BasicStroke.CAP_BUTT,
          BasicStroke.JOIN_BEVEL, 1, dash, 0);
    }
  }

  /**
   * Returns the backgorund
   * 
   * @param pBackground
   *          name of the background
   * @return backgroundimage
   */
  public synchronized Image getBackgroundImage(String pBackground) {
    Image background = backgroundimage.get(pBackground);
    if (background == null) {
      commonLogger.warn("Backgroundimage not found. Set standardimage");
      background = backgroundimage.get("standard");
    }
    return background;
  }

  /**
   * Returns the name of the icon
   * 
   * @param pIcon
   * @return name
   */
  public synchronized Image getIcon(String pIcon) {
    Image icon = icons.get(pIcon);
    if (icon == null) {
      commonLogger.warn("Iconimage not found. Set standardimage");
      icon = icons.get("standard");
    }
    return icon;
  }

  /**
   * Returns the name of the icon
   * 
   * @param pIcon
   * @return name
   */
  public synchronized String getIconName(Image pIcon) {
    String name = iconsReversal.get(pIcon);
    return name;
  }

  /**
   * Returns teh name of the background
   * 
   * @param pBackground
   * @return name
   */
  public synchronized String getBackgroundName(Image pBackground) {
    String name = backgroundimageReversal.get(pBackground);
    return name;
  }

  /**
   * Returns the name of the Color
   * 
   * @param pColor
   * @return name
   */
  public synchronized String getColorName(Color pColor) {
    String name = colorsReversal.get(pColor);
    return name;
  }

  /**
   * Returns the name of the linestyle where dash==pDash
   * 
   * @param pDash
   * @return name
   */
  public synchronized String getLineStyleName(float[] pDash) {
    String name = null;
    Collection<String> tmp = linestyleReversal.values();
    Iterator todo = tmp.iterator();
    while (todo.hasNext()) {
      String key = (String) todo.next();
      float tempfloat[] = linestyle.get(key);
      if (tempfloat[0] == pDash[0] || tempfloat[1] == pDash[1]) {
        name = key;
        break;
      }
    }
    return name;
  }

  /**
   * Returns the name of the Linethickness with thickness==width
   * 
   * @param pWidth
   * @return name
   */
  public synchronized String getLineThicknessName(int pWidth) {
    String name = linethicknessReversal.get(pWidth);
    return name;
  }

  /**
   * Returns the path, where the communicationfile have been written.
   * 
   * @return commFilePath
   */
  public synchronized String getCommFilePath() {
    return commFilePath;
  }

  /**
   * Returns the port.
   * 
   * @return startPort
   */
  public synchronized String getStartPort() {
    return startPort;
  }

  /**
   * Returns the servertyp
   * 
   * @return Corba/Socket
   */
  public synchronized String getServerTyp() {
    return serverTyp;
  }

  /**
   * Returns the absolut max label length of vertex and node
   * 
   * @return maylabellength
   */
  public synchronized int getMaxLabelLength() {
    return maxLabelLength;
  }

  /**
   * Returns how long a registerd client could be inactiv
   * 
   * @return timeout
   */
  public synchronized int getCorbaTimeout() {
    return corbaTimeout;
  }

  /**
   * Indicates whether the trace/file have to be removed.
   * 
   * @return append trace
   */
  public synchronized boolean isAppendToFile() {
    return appendToFile;
  }

  /**
   * Returns the path of the tracefile
   * 
   * @return traceFilePath
   */
  public synchronized String getTraceFilePath() {
    return traceFilePath;
  }

  /**
   * Return if the trace have been written to the console
   * 
   * @return writeToconsole
   */
  public synchronized boolean isWriteToConsole() {
    return writeToConsole;
  }

  /**
   * Returns the Logglevel
   * 
   * @param pLoggername
   * @return loglevel
   */
  public synchronized String getLogLevel(String pLoggername) {
    String level = loggers.get(pLoggername);
    if (level != null) {
      return level;
    } else {
      commonLogger.warn("Loglevel not found. Set standarlevel:INFO");
      return "INFO";
    }
  }

  /**
   * Save the new loggerconfiguration to the configfile
   * 
   * @param pLoggers
   */
  public synchronized void saveLoggerConfiguration(Vector pLoggers) {
    // TODO: check if remove of this method is permitted. slf4j does not
    // support changing log level at runtime
    // https://stackoverflow.com/questions/13442967/how-to-dynamically-change-log-level-in-slf4j-or-log4j
  }

  /**
   * 
   * @return the layoutdelay
   */
  public synchronized int getLayoutDelay() {
    return layoutDelay;
  }

  // ****************************LOADERS**********************************
  private void loadColors(Element pTyp) {
    Element eColors = pTyp.element(COLORS);
    Iterator colorIt = eColors.elementIterator();
    while (colorIt.hasNext()) {
      Element eColor = (Element) (colorIt.next());
      if (eColor != null) {
        String name = eColor.getName();
        Element eR = eColor.element(R);
        Element eG = eColor.element(G);
        Element eB = eColor.element(B);

        if (eR != null && eG != null && eB != null) {
          int r = Integer.parseInt(eR.getText());
          int g = Integer.parseInt(eG.getText());
          int b = Integer.parseInt(eB.getText());
          colors.put(name, new Color(r, g, b));
          colorsReversal.put(new Color(r, g, b), name);
          commonLogger
              .debug("New Color: " + name + " : " + r + "/" + g + "/" + b);
        } else {
          commonLogger.warn("Load Color RGB failed");
        }
      } else {
        commonLogger.warn("Load color failed");
      }
    }
  }

  private void loadLineStyles(Element pTyp) {
    Element eLineStyles = pTyp.element(LINESTYLE);
    Iterator styleIt = eLineStyles.elementIterator();
    while (styleIt.hasNext()) {
      Element eStyle = (Element) (styleIt.next());
      if (eStyle != null) {
        try {
          String name = eStyle.getName();
          float draw = java.lang.Float.parseFloat(eStyle.elementText(DRAW));
          float space = java.lang.Float.parseFloat(eStyle.elementText(SPACE));
          float[] dash = { draw, space };
          linestyle.put(name, dash);

          linestyleReversal.put(dash, name);
        } catch (Exception ex) {
          commonLogger.warn("Load Linestyle space/draw failed");
        }
      } else {
        commonLogger.warn("Load Linestyle failed");
      }
    }
  }

  private void loadLineThickness(Element pTyp) {
    Element eLineThickness = pTyp.element(LINETHICKNESS);
    Iterator thickIt = eLineThickness.elementIterator();
    while (thickIt.hasNext()) {
      Element eThick = (Element) (thickIt.next());
      if (eThick != null) {
        try {
          String name = eThick.getName();
          int width = Integer.parseInt(eThick.elementText(WIDTH));
          linethickness.put(name, new Integer(width));
          linethicknessReversal.put(new Integer(width), name);
        } catch (Exception ex) {
          commonLogger.warn("Load Linethickkness width failed");
        }
      } else {
        commonLogger.warn("Load Linethickkness failed");
      }
    }
  }

  private void loadIcons(Element pTyp) {
    Element eIcons = pTyp.element(ICONS);
    Iterator iconIt = eIcons.elementIterator();
    while (iconIt.hasNext()) {
      Element eIcon = (Element) (iconIt.next());
      if (eIcon != null) {
        try {
          String name = eIcon.getName();
          String path = eIcon.elementText(PATH);
          Toolkit toolkit = Toolkit.getDefaultToolkit();
          Image icon = toolkit.getImage(path);
          icons.put(name, icon);
          iconsReversal.put(icon, name);

        } catch (Exception ex) {
          commonLogger.warn("Load Icon name/path failed");
        }
      } else {
        commonLogger.warn("Load Icon failed");
      }
    }
  }

  private void loadBackgrounds(Element pTyp) {
    Element eBackground = pTyp.element(BACKGROUND);
    Iterator backgroundIt = eBackground.elementIterator();
    while (backgroundIt.hasNext()) {
      Element eBack = (Element) (backgroundIt.next());
      if (eBack != null) {
        try {
          String name = eBack.getName();
          String path = eBack.elementText(PATH);
          Toolkit toolkit = Toolkit.getDefaultToolkit();
          Image back = toolkit.getImage(path);
          backgroundimage.put(name, back);
          backgroundimageReversal.put(back, name);

        } catch (Exception ex) {
          commonLogger.warn("Load Background name/path failed");
        }
      } else {
        commonLogger.warn("Load Background failed");
      }
    }
  }

  private void loadCommFilePath(Element pServer) {

    Element ePortFile = pServer.element(COMMUFILE);
    if (ePortFile != null) {
      commFilePath = ePortFile.getText();
    } else {
      commFilePath = "GVSComm.xml";
      commonLogger.warn("Load Communicationfilepath failed");
      commonLogger.warn("Write Communicationfile to cuuren directory");
    }
  }

  private void loadStartPort(Element pServer) {
    Element eStartPort = pServer.element(STARTPORT);
    if (eStartPort != null) {
      try {
        startPort = eStartPort.getText();
      } catch (Exception ex) {
        commonLogger.warn("Load Startport failed. Set default: 3000");
        startPort = "3000";
      }
    } else {
      startPort = "3000";
      commonLogger.warn("No Startport. Set default: 3000");
    }
  }

  private void loadServerTyp(Element pServer) {
    Element eServerTyp = pServer.element(SERVERTYP);
    if (eServerTyp != null) {
      try {
        serverTyp = eServerTyp.getText();
      } catch (Exception ex) {
        commonLogger.warn("Load Servertyp failed. Set default: Socket");
        serverTyp = SOCKET;
      }
    } else {
      commonLogger.warn("No Servertyp. Set default: Socket");
      serverTyp = SOCKET;
    }
  }

  private void loadMaxLabelLength(Element pRoot) {
    Element eMaxLabelLength = pRoot.element(MAXLABELLENGTH);
    if (eMaxLabelLength != null) {
      try {
        maxLabelLength = Integer.parseInt(eMaxLabelLength.getText());
      } catch (Exception ex) {
        commonLogger.warn("Load MaxlabelLength failed. Set default: 8");
      }
    } else {
      commonLogger.warn("No MaxlabelLength. Set default: 8");
    }
  }

  private void loadConsoleOutput(Element pLogging) {
    Element eWriteConsole = pLogging.element(WRITECONSOLE);
    if (eWriteConsole != null) {
      try {
        String writeToConsole = eWriteConsole.getText();
        if (writeToConsole.equals("true") || writeToConsole.equals("True")) {
          this.writeToConsole = true;
        }
      } catch (Exception ex) {
        commonLogger.warn("Load writeToConsole failed. Set default: false");
      }
    } else {
      commonLogger.warn("No writeToConsole. Set default: false");
    }
  }

  private void loadFileOutput(Element pLogging) {
    Element eWriteFile = pLogging.element(WRITEFILE);
    Element ePath = eWriteFile.element(PATH);
    Element eReplaceFile = eWriteFile.element(REPLACEFILE);
    try {
      if (ePath != null) {
        traceFilePath = ePath.getText();
      }
      if (eReplaceFile != null) {
        String replaceFile = eReplaceFile.getText();
        if (replaceFile.equals("false") || replaceFile.equals("False")) {
          appendToFile = true;
        }
      }

    } catch (Exception ex) {
    }
  }

  private void loadLoggers(Element pLogging) {
    Element eLoggers = pLogging.element(LOGGERS);
    Iterator loggerIt = eLoggers.elementIterator();
    while (loggerIt.hasNext()) {
      Element eLogger = (Element) (loggerIt.next());
      if (eLogger != null) {
        try {
          String name = eLogger.getName();
          String level = eLogger.getText();
          loggers.put(name, level);
        } catch (Exception ex) {
          commonLogger.warn("Load Loggers failed.");
        }
      } else {
        commonLogger.warn("No Loggers.");
      }
    }
  }

  private void loadLayoutDelay(Element pRoot) {
    Element eLayoutDelay = pRoot.element(LAYOUTDELAY);
    if (eLayoutDelay != null) {
      try {
        layoutDelay = Integer.parseInt(eLayoutDelay.getText());
        commonLogger.debug("Delay loaded " + layoutDelay);
      } catch (Exception ex) {
        commonLogger.warn("Load LayoutDelay failed. Set default: 1500ms");
      }
    } else {
      commonLogger.warn("Load LayoutDelay failed. Set default: 1500ms");
    }
  }
}
