package gvs.ui.view.session;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
public class SessionView {

  @FXML
  private HBox stepButtons;

  @FXML
  private ProgressBar stepIndicator;

  @FXML
  private ImageView firstBtn;

  @FXML
  private ImageView prevBtn;

  @FXML
  private ImageView nextBtn;

  @FXML
  private ImageView lastBtn;

  @FXML
  private TextArea descriptionField;

  @FXML
  private ImageView playBtn;

  @FXML
  private Button autoLayoutBtn;

  @FXML
  private AnchorPane modelPane;

  /**
   * Called automatically by JavaFX Framework to initialize the view.
   */
  @FXML
  private void initialize() {
    firstBtn.setImage(new Image(SessionView.class.getClassLoader()
        .getResourceAsStream("images/firstBtn.png")));
    prevBtn.setImage(new Image(SessionView.class.getClassLoader()
        .getResourceAsStream("images/prevBtn.png")));
    nextBtn.setImage(new Image(SessionView.class.getClassLoader()
        .getResourceAsStream("images/nextBtn.png")));
    lastBtn.setImage(new Image(SessionView.class.getClassLoader()
        .getResourceAsStream("images/lastBtn.png")));
    playBtn.setImage(new Image(SessionView.class.getClassLoader()
        .getResourceAsStream("images/playBtn.png")));
  }

}
