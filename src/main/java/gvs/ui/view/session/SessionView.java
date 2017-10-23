package gvs.ui.view.session;

import gvs.util.FontAwesome;
import gvs.util.FontAwesome.Glyph;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

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

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    stepButtons.add(
        new Button("", new FontAwesome().create(Glyph.STEP_BACKWARD)), 0, 0);
    stepButtons.add(
        new Button("", new FontAwesome().create(Glyph.BACKWARD)), 1, 0);
    stepButtons.add(
        new Button("", new FontAwesome().create(Glyph.FORWARD)), 2, 0);
    stepButtons.add(
        new Button("", new FontAwesome().create(Glyph.STEP_FORWARD)), 3, 0);
    playGrid.add(
        new Button("", new FontAwesome().create(Glyph.PLAY)), 0, 0);   
  }

}
