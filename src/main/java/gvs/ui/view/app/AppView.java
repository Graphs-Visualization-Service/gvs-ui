package gvs.ui.view.app;

import java.io.File;

import ch.qos.logback.classic.joran.action.RootLoggerAction;
import gvs.ui.logic.app.AppViewModel;
import gvs.util.FontAwesome;
import gvs.util.FontAwesome.Glyph;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * MVVM View Class. Defines functions for all fxml actions. Defines bindings to
 * current session and active sessions.
 * 
 * @author mtrentini
 */
public class AppView {

  @FXML
  private Menu gvsMenu;

  @FXML
  private MenuItem quitMenuItem;

  @FXML
  private MenuItem importMenuItem;

  @FXML
  private Button deleteSessionBtn;
  
  @FXML
  private Button saveSessionBtn;

  @FXML
  private ComboBox<String> chooseSessionBox;

  @FXML
  private BorderPane rootPane;


  private AppViewModel model;
  private FileChooser fileChooser;

  @FXML
  private void initialize() {
    setLogoAsBackground();
    initButtonlabels();
    initFileChooser();
  }

  /**
   * Bind dropdown menu to active sessions. Bind selected dropdown menu item to
   * current session.
   */
  private void fillDropDown() {
    chooseSessionBox.setItems(model.getSessionNames());
    chooseSessionBox.valueProperty()
        .bindBidirectional(model.getCurrentSessionName());
  }

  private void setLogoAsBackground() {
    BackgroundImage myBI = new BackgroundImage(
        new Image(
            getClass().getClassLoader().getResourceAsStream("images/logo.png"),
            200, 200, true, true),
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
        BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
    rootPane.setBackground(new Background(myBI));
  }
  
  private void initFileChooser() {
    fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("GVS files (*.gvs)", "*.gvs");
    fileChooser.getExtensionFilters().add(extensionFilter);
  }
  
  private void initButtonlabels() {
    saveSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.SAVE));
    deleteSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.REMOVE));
  }

  @FXML
  private void loadSession() {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    fileChooser.setTitle("Load Session File");
    File file = fileChooser.showOpenDialog(stage);
    model.loadSession(file);
  }

  @FXML
  private void saveSession() {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    fileChooser.setTitle("Save Session File");
    File file = fileChooser.showSaveDialog(stage);
    model.saveSession(file);
  }

  @FXML
  private void removeSession() {
    model.removeCurrentSession();
  }

  @FXML
  private void changeSession() {
    String name = chooseSessionBox.getValue();
    model.changeSession(name);
  }

  @FXML
  private void quitGVS() {
    model.terminateApplication();
  }

  public void setAppViewModel(AppViewModel viewModel) {
    model = viewModel;
    fillDropDown();
  }
}
