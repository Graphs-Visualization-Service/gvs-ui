package gvs.interfaces;

import java.io.File;

import gvs.business.model.graph.Graph;

public interface ISession {

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

  void replay(long timeout, Action c);

  void autoLayout();

  void saveSession(File file);

  void changeCurrentGraphToNext();

  void changeCurrentGraphToFirst();

  void changeCurrentGraphToPrev();

  void changeCurrentGraphToLast();

  int getTotalGraphCount();

  Graph getCurrentGraph();
}
