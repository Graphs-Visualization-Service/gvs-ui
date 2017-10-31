package gvs.ui.logic.session;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.business.logic.graph.GraphSessionController;
import gvs.business.logic.tree.TreeSessionController;
import gvs.business.model.ApplicationModel;
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

  private GraphViewModel graphViewModel;
  private ApplicationModel applicationModel;
  private ISessionController currentSession;

  // TODO: find better names
  private final StringProperty totalGraphCountProperty = new SimpleStringProperty();
  private final StringProperty currentGraphModelIdProperty = new SimpleStringProperty();

  private static final Logger logger = LoggerFactory
      .getLogger(SessionViewModel.class);

  @Inject
  public SessionViewModel(ApplicationModel appModel,
      GraphViewModel graphViewModel) {

    logger.info("Initializing SessionViewModel.");
    this.applicationModel = appModel;
    this.graphViewModel = graphViewModel;

    applicationModel.addObserver(this);

    currentGraphModelIdProperty.set("0");
    totalGraphCountProperty.set("0");
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
    currentSession = applicationModel.getSession();

    Platform.runLater(() -> {
      updateStepProperties();
      logger.info("Updating SessionViewModel because current session changed.");
    });

    importBusinessModel();
  }

  public void importBusinessModel() {
    // TODO check if this can be done more elegantly
    if (currentSession != null) {
      if (currentSession instanceof GraphSessionController) {
        GraphSessionController graphSessionController = (GraphSessionController) currentSession;
        graphViewModel
            .transformGraphModel(graphSessionController.getCurrentGraph());
      } else {
        TreeSessionController treeSessionController = (TreeSessionController) currentSession;
        graphViewModel
            .transformTreeModel(treeSessionController.getCurrentTreeModel());
      }
    }
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
