package gvs.interfaces;

import java.io.File;
import java.util.Observer;

import gvs.business.model.graph.Graph;

public interface ISessionController {

  String getSessionName();

  long getSessionId();

  // TODO: remove here and in subclasses
  void getFirstModel();

  // TODO: remove here and in subclasses
  void getPreviousModel();

  // TODO: remove here and in subclasses
  void getNextModel();

  // TODO: remove here and in subclasses
  void getLastModel();

  void replay(long timeout);

  void autoLayout();

  void saveSession(File file);
  
  void changeCurrentGraphToNext();

  void changeCurrentGraphToFirst();

  void changeCurrentGraphToPrev();

  void changeCurrentGraphToLast();

  int getTotalGraphCount();

  void addObserver(Observer o);
  
  Graph getCurrentGraph();

}
