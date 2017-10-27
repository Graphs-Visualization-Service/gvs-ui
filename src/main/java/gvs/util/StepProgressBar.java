package gvs.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.ui.logic.app.AppViewModel;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import javafx.scene.layout.StackPane;

/**
 * A modified Progressbar that indicates a step count. E.g. 5/10. It fills it's
 * underlying ProgressBar accordingly.
 * 
 * @author mtrentini
 *
 */
public class StepProgressBar extends StackPane {

  @FXML
  private ProgressBar stepIndicator;

  @FXML
  private Label currentStepLabel;

  @FXML
  private Label totalStepLabel;

  private int totalStepCount;
  private static final Logger logger = LoggerFactory
      .getLogger(StepProgressBar.class);

  
  public StepProgressBar() {

    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gvs/util/StepProgressBar.fxml"));
    try {
      
      fxmlLoader.setRoot(this);
      fxmlLoader.setController(this);
      // set a save inital value, otherwise div by 0!
      totalStepCount = 1;
      fxmlLoader.load();
      logger.info("Initializing StepProgressBar");
    } catch (IOException e) {
      logger.error("Could not load StepProgressBar", e);
    }

  }

  public StringProperty currentStepProperty() {
    return currentStepLabel.textProperty();
  }

  public String getCurrentStep() {
    return currentStepProperty().get();
  }

  public void setCurrentStep(int step) {
    currentStepProperty().set(step + "");
    updateProgressBar(step);
  }

  public StringProperty totalStepProperty() {
    return totalStepLabel.textProperty();
  }

  public String getTotalStep() {
    return totalStepProperty().get();
  }

  public void setTotalStep(int steps) {
    if (steps == 0) {
      throw new IllegalArgumentException("TotalSteps cannot be 0.");
    }
    totalStepCount = steps;
    totalStepProperty().set(steps + "");
  }

  private void updateProgressBar(int step) {
    stepIndicator.progressProperty().set(step / totalStepCount);
  }

}
