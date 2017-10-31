package gvs.ui.view.controls;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.gluonhq.ignite.guice.GuiceContext;

import gvs.GuiceBaseModule;
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
  private GuiceContext context = new GuiceContext(this,
      () -> Arrays.asList(new GuiceBaseModule()));
  
  @Inject
  private FXMLLoader fxmlLoader;

  public StepProgressBar() {
    context.init();

   fxmlLoader.setLocation(
        getClass().getResource("/gvs/ui/view/controls/StepProgressBar.fxml"));
    try {

      fxmlLoader.setRoot(this);
      fxmlLoader.setController(this);
      // set a save inital value, otherwise div by 0!
      totalStepCount = 1;
      fxmlLoader.load();
      currentStepProperty().addListener((observable, oldValue,
          newValue) -> updateProgressBar(Integer.parseInt(newValue)));
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
  }

  public StringProperty totalStepProperty() {
    return totalStepLabel.textProperty();
  }

  public String getTotalStep() {
    return totalStepProperty().get();
  }

  private void updateProgressBar(int step) {
    double totalStepCount = Double.parseDouble(totalStepProperty().get());
    double progress = ((double)step) / (double) totalStepCount;
    stepIndicator.progressProperty().set(progress);
  }

}
