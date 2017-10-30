package gvs.interfaces;

import java.io.File;

import gvs.business.logic.graph.GraphSessionController;
import gvs.business.logic.tree.TreeSessionController;

public interface IPersistor {

  void saveToDisk(GraphSessionController session, File file);

  void saveToDisk(TreeSessionController session, File file);

  /**
   * Load a file
   * 
   * @param pPath
   *          the file to load
   * @return the SessionController
   */
  ISessionController loadFile(String pPath);

}
