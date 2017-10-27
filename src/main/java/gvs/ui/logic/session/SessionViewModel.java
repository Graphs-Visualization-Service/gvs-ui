package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import gvs.interfaces.ISessionController;
import gvs.ui.application.model.ApplicationModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents the currently loaded session.
 * 
 * @author mtrentini
 *
 */
public class SessionViewModel implements Observer {
  private ApplicationModel appModel;
  private ISessionController currentSession;
  // TODO: find better names
  private final StringProperty totalGraphCountProperty = new SimpleStringProperty();
  private final StringProperty currentGraphModelIdProperty = new SimpleStringProperty();

  public SessionViewModel(ApplicationModel model) {
    appModel = model;
    appModel.addObserver(this);
    currentSession = appModel.getSession();
    updateStepProperties();
  }

  private void updateStepProperties() {
    totalGraphCountProperty.set(currentSession.getTotalGraphCount() + "");
    currentGraphModelIdProperty.set(totalGraphCountProperty.get());
  }

  public void changeCurrentGraphToNext() {
    currentSession.changeCurrentGraphToNext();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
  }

  public void changeCurrentGraphToPrevious() {
    currentSession.changeCurrentGraphToPrev();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
  }

  public void changeCurrentGraphToFirst() {
    currentSession.changeCurrentGraphToFirst();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
  }

  public void changeCurrentGraphToLast() {
    currentSession.changeCurrentGraphToLast();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
  }

  /**
   * This method is invoked whenever a new current session is set in the
   * ApplicationModel.
   */
  @Override
  public void update(Observable o, Object arg) {
    currentSession = appModel.getSession();
    updateStepProperties();
  }

  public void replayGraph(int replaySpeed) {
    // TODO Auto-generated method stub

  }

  public void autoLayout() {
    // TODO Auto-generated method stub

  }

  public StringProperty totalGraphCountProperty() {
    return totalGraphCountProperty;
  }

  public StringProperty currentGraphModelIdProperty() {
    return currentGraphModelIdProperty;
  }
}
