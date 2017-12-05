
package gvs;

import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The Configuration class loads and takes the values from XML.
 * 
 * @author mkoller
 */
@Singleton
public class Configuration {

  private static final int WINDOW_WIDTH = 1000;
  private static final int WINDOW_HEIGHT = 700;
  private static final int CONTENT_PANE_HEIGHT = 500;
  private static final int ICON_FONT_SIZE = 32;
  private static final int AVG_PIXEL_PER_LETTER = 7;
  private static final int MAX_LABEL_LENGTH_TREE = 180;
  private static final int MAX_LABEL_LENGTH_GRAPH = 320;

  // Server
  private static final String SERVER = "Server";
  private static final String STARTPORT = "StartPort";
  private static final String WATCHDOG = "Watchdog";

  // Communication
  private String startPort = "";

  // Logger
  private static final Logger logger = LoggerFactory
      .getLogger(Configuration.class);

  /**
   * Loads the values from the config-file.
   *
   */
  public Configuration() {
    logger.info("Load config from classpath");
    SAXReader reader = new SAXReader();
    InputStream config = Configuration.class.getClassLoader()
        .getResourceAsStream("config.xml");

    try {
      logger.info("Build configuration");

      Document configurationXML = reader.read(config);
      Element docRoot = configurationXML.getRootElement();
      Element eServer = docRoot.element(SERVER);

      loadStartPort(eServer);
    } catch (DocumentException e) {
      logger.error("No configuration found. System exit");
      System.exit(0);
    }
  }

  /**
   * Load Start Port.
   * 
   * @param pServer
   *          server
   */
  private void loadStartPort(Element pServer) {
    Element eStartPort = pServer.element(STARTPORT);
    if (eStartPort != null) {
      try {
        startPort = eStartPort.getText();
      } catch (Exception ex) {
        logger.warn("Load Startport failed. Set default: 3000");
        startPort = "3000";
      }
    } else {
      startPort = "3000";
      logger.warn("No Startport. Set default: 3000");
    }
  }

  /**
   * Returns the port.
   * 
   * @return startPort
   */
  public String getStartPort() {
    return startPort;
  }

  public static int getContentPaneHeight() {
    return CONTENT_PANE_HEIGHT;
  }

  public static int getWindowWidth() {
    return WINDOW_WIDTH;
  }

  public static int getWindowHeight() {
    return WINDOW_HEIGHT;
  }

  public static String getWatchdog() {
    return WATCHDOG;
  }

  /**
   * 
   * @return the maximum length of a tree label
   */
  public static int getMaxLabelLengthForTree() {
    return MAX_LABEL_LENGTH_TREE;
  }

  /**
   * 
   * @return the maximum length of a tree label
   */
  public static int getMaxLabelLengthForGraph() {
    return MAX_LABEL_LENGTH_GRAPH;
  }

  public static int getIconFontSize() {
    return ICON_FONT_SIZE;
  }

  public static int getAvgPixelPerLetter() {
    return AVG_PIXEL_PER_LETTER;
  }
}
