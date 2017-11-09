package gvs.access;

import java.io.File;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Reads input XML of a {@link XmlWriter} and passes the document to the
 * {@link ModelBuilder}.
 * 
 * @author Michi
 */
public class XmlReader {

  private final String fileName;
  private final SAXReader xmlReader;

  private static final String SCHEMA = "gvs.xsd";
  private static final String VALIDATION_SCHEMA = "http://apache.org/"
      + "xml/features/validation/schema";
  private static final String NO_NAMESPACE_SCHEMA_LOCATION = "http://apache.org"
      + "/xml/properties/schema/external-noNamespaceSchemaLocation";
  private static final Logger logger = LoggerFactory.getLogger(XmlReader.class);

  @Inject
  public XmlReader(SAXReader reader, @Assisted String fileName) {

    this.xmlReader = reader;
    this.fileName = fileName;

    applySchema();
  }

  /**
   * Apply the custom XML schema from classpath to the {@link SAXReader}.
   */
  private void applySchema() {
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
   * @return parsed document
   */
  public Document read() {
    File inputFile = new File(fileName);
    try {
      return xmlReader.read(inputFile);
    } catch (Exception e) {
      logger.error("Cannot read file {}", inputFile.getName(), e);
      return null;
    }
  }
}
