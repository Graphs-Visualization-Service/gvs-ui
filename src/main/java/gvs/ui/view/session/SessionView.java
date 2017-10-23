package gvs.ui.view.session;

import gvs.ui.logic.session.SessionViewModel;
import gvs.util.FontAwesome;
import gvs.util.FontAwesome.Glyph;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
public class SessionView {
  @FXML
  private GridPane playGrid;

  @FXML
  private ProgressBar stepIndicator;

  @FXML
  private GridPane stepButtons;

  @FXML
  private TextArea descriptionField;

  @FXML
  private Button autoLayoutBtn;

  @FXML
  private AnchorPane modelPane;

  @FXML
  private Button firstBtn;

  @FXML
  private Button prevBtn;

  @FXML
  private Button nextBtn;

  @FXML
  private Button lastBtn;

  @FXML
  private Button playBtn;
  
  private SessionViewModel sessionViewModel;

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    initStepButtons();
    playBtn.setGraphic(new Button("", new FontAwesome().create(Glyph.PLAY)));
    
  }

  private void initStepButtons() {
    firstBtn.setGraphic(new FontAwesome().create(Glyph.STEP_BACKWARD));
    prevBtn.setGraphic(new FontAwesome().create(Glyph.BACKWARD));
    nextBtn.setGraphic(new FontAwesome().create(Glyph.FORWARD));
    lastBtn.setGraphic(new FontAwesome().create(Glyph.STEP_FORWARD));
  }
  
  @FXML
  private void stepForward() {
    sessionViewModel.changeCurrentGraphToNext();
  }
  
  @FXML
  private void stepBackward() {
    sessionViewModel.changeCurrentGraphToPrevious();
  }
  
  @FXML
  private void stepToFirst() {
    sessionViewModel.changeCurrentGraphToFirst();
  }
  
  @FXML
  private void stepToLast() {
    sessionViewModel.changeCurrentGraphToLast();
  }

}
