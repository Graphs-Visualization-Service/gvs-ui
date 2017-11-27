package gvs.ui.view.controls;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.ignite.guice.GuiceContext;
import com.google.inject.Inject;

import gvs.GuiceBaseModule;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

/**
 * A modified progress bar that indicates a step count. E.g. 5/10. It fills it's
 * underlying ProgressBar accordingly.
 * 
 * @author mtrentini
 */
public class StepProgressBar extends StackPane {

  @FXML
  private ProgressBar stepIndicator;

  @FXML
  private Label currentStepLabel;

  @FXML
  private Label totalStepLabel;

  private static final Logger logger = LoggerFactory
      .getLogger(StepProgressBar.class);

  @Inject
  public StepProgressBar(FXMLLoader fxmlLoader) {
    logger.info("Initializing StepProgresssBar");
    fxmlLoader.setLocation(
        getClass().getResource("/gvs/ui/view/controls/StepProgressBar.fxml"));
    try {
      fxmlLoader.setRoot(this);
      fxmlLoader.setController(this);
      fxmlLoader.load();
    } catch (IOException e) {
      logger.error("Could not load StepProgressBar", e);
    }
  }

  @FXML
  private void initialize() {
    currentStepProperty().addListener((o, oldValue, newValue) -> {
      updateProgressBar();
    });
  }

  /**
   * Update current progress bars.
   */
  private void updateProgressBar() {
    double currentIndex = Double.parseDouble(currentStepProperty().get());
    double totalStepCount = Double.parseDouble(totalStepProperty().get());
    double progress = currentIndex / totalStepCount;
    stepIndicator.progressProperty().set(progress);
    logger.info("Update progress bar: step {} of {}", currentIndex,
        totalStepCount);
  }

  public StringProperty currentStepProperty() {
    return currentStepLabel.textProperty();
  }

  public StringProperty totalStepProperty() {
    return totalStepLabel.textProperty();
  }

}
