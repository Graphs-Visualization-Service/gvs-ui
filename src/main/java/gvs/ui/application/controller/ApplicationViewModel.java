package gvs.ui.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author Michi
 *
 */
public class ApplicationViewModel {

  private final Logger logger = LoggerFactory
      .getLogger(ApplicationViewModel.class);

  private StringProperty snapshotDescription = new SimpleStringProperty();

  /**
   * Default Constructor.
   */
  public ApplicationViewModel() {
    snapshotDescription.set("Test");
  }

  /**
   * Shows all queued snapshots with given speed.
   */
  public final void playGraphAnimation() {
      logger.info("got {}", snapshotDescription.getValue());
  }

  public final StringProperty getSnapshotDescription() {
    return snapshotDescription;
  }

  public final void setSnapshotDescription(StringProperty snapshotDescription) {
    this.snapshotDescription = snapshotDescription;
  }

}
