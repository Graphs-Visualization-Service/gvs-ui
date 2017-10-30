package gvs.interfaces;

import gvs.ui.application.view.ControlPanel;

public interface ISessionController{

  ControlPanel getControlPanel();

  String getSessionName();

  long getSessionId();

  void setVisualModel();

  // TODO: remove here and in subclasses
  void getFirstModel();

  // TODO: remove here and in subclasses
  void getPreviousModel();

  // TODO: remove here and in subclasses
  void getNextModel();

  // TODO: remove here and in subclasses
  void getLastModel();

  void replay();

  void speed(int picsPerSecond);

  void autoLayout();

  boolean validateNavigation(long requestedModelId);

  void saveSession();

  void changeCurrentGraphToNext();

  void changeCurrentGraphToFirst();

  void changeCurrentGraphToPrev();

  void changeCurrentGraphToLast();
  
  int getCurrentGraphId();
  
  int getTotalGraphCount();

}
