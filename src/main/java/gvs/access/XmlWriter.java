package gvs.access;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * @author Michi
 */
public class XmlWriter {

  private final String fileName;
  private static final Logger logger = LoggerFactory.getLogger(XmlWriter.class);

  @Inject
  public XmlWriter(String fileName) {
    this.fileName = fileName;
  }

  public void write(String line) {
    File inputXml = new File(fileName);
    try (FileWriter fileWriter = new FileWriter(inputXml)) {
      fileWriter.write(line);
      fileWriter.flush();
    } catch (IOException e) {
      logger.error("Unable to write input.xml", e);
    }
  }
}
