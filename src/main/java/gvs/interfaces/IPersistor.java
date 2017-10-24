package gvs.interfaces;

import gvs.ui.graph.controller.GraphSessionController;
import gvs.ui.tree.controller.TreeSessionController;

public interface IPersistor {

  void saveToDisk(GraphSessionController session);

  void saveToDisk(TreeSessionController session);

  /**
   * Load a file
   * 
   * @param pPath
   *          the file to load
   * @return the SessionController
   */
  ISessionController loadFile(String pPath);

}
