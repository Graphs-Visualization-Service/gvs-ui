package gvs.access;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * 
 * @author Michi
 */
@Singleton
public class InputXmlWriter {

  private static final String DEFAULT_FILE_NAME = "input.xml";
  private static final Logger logger = LoggerFactory
      .getLogger(InputXmlWriter.class);

  public void write(String line) {
    File inputXml = new File(DEFAULT_FILE_NAME);
    try (FileWriter fileWriter = new FileWriter(inputXml)) {
      fileWriter.write(line);
      fileWriter.flush();
    } catch (IOException e) {
      logger.error("Unable to write input.xml", e);
    }
  }
}
