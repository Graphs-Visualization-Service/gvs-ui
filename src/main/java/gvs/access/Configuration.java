
package gvs.access;

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

  // Document
  private static final String LAYOUTDELAY = "LayoutDelay";

  // Server
  private static final String SERVER = "Server";
  private static final String STARTPORT = "StartPort";
  private static final String COMMUFILE = "CommunicationFile";

  // Communication
  private String commFilePath = "";
  private String startPort = "";

  // Layouter
  private int layoutDelay = DEFAULT_LAYOUT_DELAY;
  private static final int DEFAULT_LAYOUT_DELAY = 1500;

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

      loadLayoutDelay(docRoot);

      loadCommFilePath(eServer);
      loadStartPort(eServer);
    } catch (DocumentException e) {
      logger.error("No configuration found. System exit");
      System.exit(0);
    }
  }
  

  /**
   * Load Layout Delay.
   * 
   * @param pRoot
   *          root element
   */
  private void loadLayoutDelay(Element pRoot) {
    Element eLayoutDelay = pRoot.element(LAYOUTDELAY);
    if (eLayoutDelay != null) {
      try {
        layoutDelay = Integer.parseInt(eLayoutDelay.getText());
        logger.debug("Delay loaded " + layoutDelay);
      } catch (Exception ex) {
        logger.warn("Load LayoutDelay failed. Set default: 1500ms");
      }
    } else {
      logger.warn("Load LayoutDelay failed. Set default: 1500ms");
    }
  }

  /**
   * Load communication file path.
   * 
   * @param pServer
   *          sever element
   */
  private void loadCommFilePath(Element pServer) {

    Element ePortFile = pServer.element(COMMUFILE);
    if (ePortFile != null) {
      commFilePath = ePortFile.getText();
    } else {
      commFilePath = "GVSComm.xml";
      logger.warn("Load communication file from path failed");
      logger.info("Write Communication file to current directory");
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
   * Returns the path, where the communicationfile have been written.
   * 
   * @return commFilePath
   */
  public String getCommFilePath() {
    return commFilePath;
  }

  /**
   * Returns the port.
   * 
   * @return startPort
   */
  public String getStartPort() {
    return startPort;
  }

  /**
   * 
   * @return the layoutdelay
   */
  public int getLayoutDelay() {
    return layoutDelay;
  }
}
