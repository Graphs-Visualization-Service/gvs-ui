package gvs.interfaces;

import java.io.File;

import gvs.business.model.graph.Graph;

public interface ISession {

  String getSessionName();

  long getSessionId();

  void replay(long timeout, Action c);

  void layoutCurrentGraph();

  void saveSession(File file);

  void changeCurrentGraphToNext();

  void changeCurrentGraphToFirst();

  void changeCurrentGraphToPrev();

  void changeCurrentGraphToLast();

  int getTotalGraphCount();

  Graph getCurrentGraph();
}
