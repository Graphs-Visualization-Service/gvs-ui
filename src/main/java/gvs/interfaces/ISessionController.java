package gvs.interfaces;

import java.io.File;

public interface ISessionController{

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

  void saveSession(File file);

  void changeCurrentGraphToNext();

  void changeCurrentGraphToFirst();

  void changeCurrentGraphToPrev();

  void changeCurrentGraphToLast();
  
  int getCurrentGraphId();
  
  int getTotalGraphCount();

}
