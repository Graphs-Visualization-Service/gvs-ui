package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.graph.GraphSessionController;
import gvs.business.logic.tree.TreeSessionController;
import gvs.business.model.CurrentSessionHolder;
import gvs.interfaces.ISessionController;
import gvs.ui.model.graph.GraphViewModel;
import javafx.application.Platform;
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
@Singleton
public class SessionViewModel implements Observer {

  // TODO add boundary checks
  private int currentStepNumber = 0;

  private final GraphViewModel graphViewModel;
  private final CurrentSessionHolder currentSessionHolder;

  // TODO: find better names
  private final StringProperty totalGraphCountProperty = new SimpleStringProperty();
  private final StringProperty currentGraphModelIdProperty = new SimpleStringProperty();

  private static final Logger logger = LoggerFactory
      .getLogger(SessionViewModel.class);

  @Inject
  public SessionViewModel(CurrentSessionHolder appModel,
      GraphViewModel graphViewModel) {

    logger.info("Initializing SessionViewModel.");
    this.currentSessionHolder = appModel;
    this.graphViewModel = graphViewModel;

    currentSessionHolder.addObserver(this);

    currentGraphModelIdProperty.set("0");
    totalGraphCountProperty.set("0");
  }

  private void updateStepProperties() {
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    totalGraphCountProperty.set(currentSession.getTotalGraphCount() + "");
    currentGraphModelIdProperty.set(totalGraphCountProperty.get());
  }

  public void changeCurrentGraphToNext() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToNext();
    currentGraphModelIdProperty.set(++currentStepNumber + "");
  }

  public void changeCurrentGraphToPrevious() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToPrev();
    currentGraphModelIdProperty.set(--currentStepNumber + "");
  }

  public void changeCurrentGraphToFirst() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToFirst();
    currentStepNumber = 1;
    currentGraphModelIdProperty.set(currentStepNumber + "");
  }

  public void changeCurrentGraphToLast() {
    logger.info("Changing the displayed graph model...");
    ISessionController currentSession = currentSessionHolder
        .getCurrentSession();
    currentSession.changeCurrentGraphToLast();
    currentStepNumber = Integer.parseInt(totalGraphCountProperty.get());
    currentGraphModelIdProperty.set(currentStepNumber + "");
  }

  /**
   * This method is invoked whenever a new current session is set in the
   * ApplicationModel.
   */
  @Override
  public void update(Observable o, Object arg) {
    logger.info("Current session changed...");
    Platform.runLater(() -> {
      updateStepProperties();
      // TODO release observer connection to old session
      currentSessionHolder.getCurrentSession().addObserver(this);
    });
  }

  public void replayGraph(double d) {
    logger.info("Starting replay with speed {}", d);
  }

  public void autoLayout() {
    logger.info("Auto-layouting the current graph model...");

  }

  public StringProperty totalGraphCountProperty() {
    return totalGraphCountProperty;
  }

  public StringProperty currentGraphModelIdProperty() {
    return currentGraphModelIdProperty;
  }
}
