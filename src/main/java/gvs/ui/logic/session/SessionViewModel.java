package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.model.ApplicationModel;
import gvs.interfaces.ISessionController;
import gvs.ui.view.session.SessionView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The ViewModel class for the current session. Corresponds to the classical
 * ViewModel of the MVVM Pattern. It observes the ApplicationModel and handles
 * the GUI session logic.
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
  
  private static final Logger logger = LoggerFactory
      .getLogger(SessionViewModel.class);

  public SessionViewModel(ApplicationModel model) {
    appModel = model;
    appModel.addObserver(this);
    currentSession = appModel.getSession();
    updateStepProperties();
    logger.info("Initializing SessionViewModel.");
  }

  private void updateStepProperties() {
    totalGraphCountProperty.set(currentSession.getTotalGraphCount() + "");
    currentGraphModelIdProperty.set(totalGraphCountProperty.get());
  }

  public void changeCurrentGraphToNext() {
    currentSession.changeCurrentGraphToNext();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
    logger.info("Changing the displayed graph model...");
  }

  public void changeCurrentGraphToPrevious() {
    currentSession.changeCurrentGraphToPrev();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
    logger.info("Changing the displayed graph model...");
  }

  public void changeCurrentGraphToFirst() {
    currentSession.changeCurrentGraphToFirst();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
    logger.info("Changing the displayed graph model...");
  }

  public void changeCurrentGraphToLast() {
    currentSession.changeCurrentGraphToLast();
    currentGraphModelIdProperty.set(currentSession.getCurrentGraphId() + "");
    logger.info("Changing the displayed graph model...");
  }

  /**
   * This method is invoked whenever a new current session is set in the
   * ApplicationModel.
   */
  @Override
  public void update(Observable o, Object arg) {
    currentSession = appModel.getSession();
    updateStepProperties();
    logger.info("Updating SessionViewModel because current session changed.");
  }

  public void replayGraph(double d) {
   logger.info("Starting replay with speed " + d) ;
  }

  public void autoLayout() {
    logger.info("Auto-layouting the current graph model...") ;

  }

  public StringProperty totalGraphCountProperty() {
    return totalGraphCountProperty;
  }

  public StringProperty currentGraphModelIdProperty() {
    return currentGraphModelIdProperty;
  }
}
