package gvs.ui.view.app;

import gvs.ui.application.view.SessionController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;

public class AppView {

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
  private Button deleteSessionBtn;

  //TODO: input correct generic
  @FXML
  private ComboBox<?> chooseSessionBox;
  
  @FXML
  private AnchorPane displaySessionPane;

  @FXML
  private void initialize() {
    setLogoAsBackground();
  }

  private void setLogoAsBackground() {
    BackgroundImage myBI = new BackgroundImage(
        new Image(
            getClass().getClassLoader().getResourceAsStream("images/logo.png"),
            200, 200, true, true),
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    displaySessionPane.setBackground(new Background(myBI));
  }

  /**
   * Called when the user clicks on the load button.
   */
  @FXML
  private void loadSession() {

  }

}
