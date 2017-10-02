package gvs.ui.application.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

public class MainController {

  @FXML
  private Menu fileMenu;

  @FXML
  private MenuItem saveMenuItem;

  @FXML
  private MenuItem loadMenuItem;

  @FXML
  private MenuItem exitMenuItem;

  @FXML
  private Menu optionsMenu;

  @FXML
  private Menu layoutMenu;

  @FXML
  private Menu graphMenu;

  @FXML
  private CheckMenuItem randomLayoutMenuItem;

  @FXML
  private CheckMenuItem stableLayoutMenuItem;

  @FXML
  private Menu treeMenu;

  @FXML
  private CheckMenuItem clusterSplittingMenuItem;

  @FXML
  private MenuItem tracerMenuItem;

  @FXML
  private Menu helpMenu;

  @FXML
  private ComboBox<?> chooseModelDropDown;

  @FXML
  private Button removeModelBtn;

  @FXML
  private AnchorPane displayModelPane;

  @FXML
  private void initialize() {
    // set data for window here
  }

}
