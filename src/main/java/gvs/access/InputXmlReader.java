package gvs.access;

import java.io.File;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.server.ModelBuilder;

/**
 * Reads input XML of a {@link InputXmlWriter} and passes the document to the
 * {@link ModelBuilder}.
 * 
 * @author Michi
 */
@Singleton
public class InputXmlReader {

  private SAXReader xmlReader;
  private ModelBuilder modelBuilder;

  private static final String DEFAULT_FILE_NAME = "input.xml";

  private static final String SCHEMA = "gvs.xsd";
  private static final String VALIDATION_SCHEMA = "http://apache.org/"
      + "xml/features/validation/schema";
  private static final String NO_NAMESPACE_SCHEMA_LOCATION = "http://apache.org"
      + "/xml/properties/schema/external-noNamespaceSchemaLocation";
  private static final Logger logger = LoggerFactory
      .getLogger(InputXmlReader.class);

  @Inject
  public InputXmlReader(SAXReader reader, ModelBuilder modelBuilder) {
    this.xmlReader = reader;
    this.modelBuilder = modelBuilder;

    setupXMLReader();
  }

  /**
   * Apply the custom XML scheme from classpath to the {@link SAXReader}.
   */
  private void setupXMLReader() {
    URL schemaURL = getClass().getClassLoader().getResource(SCHEMA);
    try {
      String schemaString = schemaURL.toURI().toString();
      xmlReader.setFeature(VALIDATION_SCHEMA, true);
      xmlReader.setProperty(NO_NAMESPACE_SCHEMA_LOCATION, schemaString);
      xmlReader.setValidation(true);
    } catch (Exception e) {
      logger.error("Cannot apply xml schema to SAX Reader", e);
    }
  }

  /**
   * Read the input file and pass the XML document to the {@link ModelBuilder}.
   * 
   */
  public synchronized void read() {
    File inputFile = new File(DEFAULT_FILE_NAME);
    try {
      Document document = xmlReader.read(inputFile);
      modelBuilder.buildModelFromXML(document);
    } catch (Exception e) {
      logger.error("Cannot read file {}", inputFile.getName(), e);
    }
  }
}
