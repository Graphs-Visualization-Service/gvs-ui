package gvs.ui.application.view;

import gvs.ui.application.controller.ApplicationViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Michi
 *
 */
public class ApplicationViewFX {

  private ApplicationViewModel viewModel = new ApplicationViewModel();

  @FXML
  private Menu gvsMenu;

  @FXML
  private MenuItem quitMenuItem;

  @FXML
  private Menu fileMenu;

  @FXML
  private MenuItem exportMenuItem;

  @FXML
  private MenuItem importMenuItem;

  @FXML
  private Menu openRecentMenu;

  @FXML
  private MenuItem clearListMenuItem;

  @FXML
  private AnchorPane displaySessionPane;

  @FXML
  private ImageView firstBtn;

  @FXML
  private ImageView prevBtn;

  @FXML
  private ImageView nextBtn;

  @FXML
  private ImageView lastBtn;

  @FXML
  private ProgressBar stepIndicator;

  @FXML
  private ImageView playBtn;

  @FXML
  private TextArea graphDescription;

  /**
   * Default initialization method of JavaFX.
   */
  @FXML
  private void initialize() {
    graphDescription.textProperty()
        .bindBidirectional(viewModel.getSnapshotDescription());

    firstBtn.setImage(new Image(getClass().getClassLoader()
        .getResourceAsStream("images/firstBtn.png")));
    prevBtn.setImage(new Image(
        getClass().getClassLoader().getResourceAsStream("images/prevBtn.png")));
    nextBtn.setImage(new Image(
        getClass().getClassLoader().getResourceAsStream("images/nextBtn.png")));
    lastBtn.setImage(new Image(
        getClass().getClassLoader().getResourceAsStream("images/lastBtn.png")));
    playBtn.setImage(new Image(
        getClass().getClassLoader().getResourceAsStream("images/playBtn.png")));
  }

  /**
   * Binds the play graph action to the responding ViewModel.
   */
  public void playBtnPressed() {
    viewModel.playGraphAnimation();
  }
}
