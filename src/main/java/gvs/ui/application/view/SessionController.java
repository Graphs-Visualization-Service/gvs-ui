package gvs.ui.application.view;

import java.io.File;

import gvs.util.ResourceUtil;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class SessionController {

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
  private AnchorPane modelPane;

  /**
   * Called automatically by JavaFX Framework to initialize the view
   */
  @FXML
  private void initialize() {
    firstBtn.setImage(buildImage(ResourceUtil.getImagePath("firstBtn.png")));
    prevBtn.setImage(buildImage(ResourceUtil.getImagePath("prevBtn.png")));
    nextBtn.setImage(buildImage(ResourceUtil.getImagePath("nextBtn.png")));
    lastBtn.setImage(buildImage(ResourceUtil.getImagePath("lastBtn.png")));
    playBtn.setImage(buildImage(ResourceUtil.getImagePath("playBtn.png")));
  }

  private Image buildImage(String path) {
    return new Image(new File(path).toURI().toString());
  }

}
