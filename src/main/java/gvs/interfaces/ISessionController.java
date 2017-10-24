package gvs.interfaces;

import gvs.ui.application.view.ControlPanel;

public interface ISessionController {

  ControlPanel getControlPanel();

  String getSessionName();

  long getSessionId();

  void setVisualModel();

  void getFirstModel();

  void getPreviousModel();

  void getNextModel();

  void getLastModel();

  void replay();

  void speed(int picsPerSecond);

  void autoLayout();

  boolean validateNavigation(long requestedModelId);
  
  void saveSession();

}
