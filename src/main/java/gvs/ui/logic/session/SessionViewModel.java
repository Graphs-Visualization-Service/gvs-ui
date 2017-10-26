package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import gvs.interfaces.ISessionController;
import gvs.ui.application.model.ApplicationModel;

/**
 * Represents the currently loaded session.
 * 
 * @author mtrentini
 *
 */
public class SessionViewModel implements Observer {
  private ApplicationModel appModel;

  public SessionViewModel(ApplicationModel model) {
    appModel = model;
    appModel.addObserver(this);

  }

  public void changeCurrentGraphToNext() {
    appModel.getSession().changeCurrentGraphToNext();
  }

  public void changeCurrentGraphToPrevious() {
    appModel.getSession().changeCurrentGraphToPrev();
  }

  public void changeCurrentGraphToFirst() {
    appModel.getSession().changeCurrentGraphToFirst();
  }

  public void changeCurrentGraphToLast() {
    appModel.getSession().changeCurrentGraphToLast();
  }

  /**
   * This method is invoked whenever a new current session is set in the
   * ApplicationModel.
   */
  @Override
  public void update(Observable o, Object arg) {
    // TODO: change GraphViewModel & GraphView
  }

  public void replayGraph(int replaySpeed) {
    // TODO Auto-generated method stub

  }

  public void autoLayout() {
    // TODO Auto-generated method stub

  }

}
