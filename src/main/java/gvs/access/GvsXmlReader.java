package gvs.access;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.sql.rowset.spi.XmlWriter;

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
 * Uses the GVS specific xml schema for custom tags.
 * 
 * @author Michi
 */
public class GvsXmlReader {

  private final String fileName;
  private final SAXReader xmlReader;

  private static final String SCHEMA = "gvs.xsd";
  private static final String VALIDATION_SCHEMA = "http://apache.org/"
      + "xml/features/validation/schema";
  private static final String NO_NAMESPACE_SCHEMA_LOCATION = "http://apache.org"
      + "/xml/properties/schema/external-noNamespaceSchemaLocation";
  private static final Logger logger = LoggerFactory
      .getLogger(GvsXmlReader.class);

  @Inject
  public GvsXmlReader(SAXReader reader, @Assisted String fileName) {

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
   * @param input 
   * 
   * @return parsed document
   */
  public Document read(InputStream input) {
    try {
      return xmlReader.read(input);
    } catch (Exception e) {
      logger.error("Cannot read input stream", e);
      return null;
    }
  }
}
