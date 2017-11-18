package gvs.interfaces;

import java.io.File;

import gvs.business.model.graph.GraphHolder;

public interface ISession {

  String getSessionName();

  long getId();

  void replay(long timeout, Action completionCallback);
  
  void pauseReplay();
  
  void cancelReplay();

  void layoutCurrentGraph(Action completionCallback);

  void saveSession(File file);

  void changeCurrentGraphToNext();

  void changeCurrentGraphToFirst();

  void changeCurrentGraphToPrev();

  void changeCurrentGraphToLast();

  int getTotalGraphCount();

  GraphHolder getGraphHolder();
  
  public boolean isTreeSession();
}
