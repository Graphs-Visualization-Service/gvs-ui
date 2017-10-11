package gvs.util;

/**
 * Provides constants for common resource locations.
 * 
 * @author Michi
 */
//TODO: delete this class, replace usage in SessionController with 'getResource'
public class ResourceUtil {

  private static final String RESOURCES_PATH = "src/main/resources/";
  private static final String IMAGES_PATH = "images/";

  public static final String getResourcesPath() {
    return RESOURCES_PATH;
  }

  public static final String getImagesPath() {
    return getResourcesPath() + IMAGES_PATH;
  }

  /**
   * Returns the path to the given image.
   * 
   * @param name
   *          Image name
   * @return path to source
   */
  public static final String getImagePath(String name) {
    return getImagesPath() + name;
  }
}
