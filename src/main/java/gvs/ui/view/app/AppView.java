package gvs.ui.view.app;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.ui.logic.app.AppViewModel;
import gvs.util.FontAwesome;
import gvs.util.FontAwesome.Glyph;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
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
@Singleton
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

  @FXML
  private Parent sessionView;

  private FileChooser fileChooser;
  private final AppViewModel appViewModel;

  @Inject
  public AppView(AppViewModel appViewModel) {
    this.appViewModel = appViewModel;
  }

  @FXML
  private void initialize() {
    sessionView.setVisible(false);
    sessionView.visibleProperty()
        .bind(appViewModel.sessionVisibilityProperty());
    deleteSessionBtn.disableProperty()
        .bind(appViewModel.sessionVisibilityProperty().not());
    saveSessionBtn.disableProperty()
        .bind(appViewModel.sessionVisibilityProperty().not());

    setLogoAsBackground();
    initButtonlabels();
    initFileChooser();
    fillDropDown();
  }

  /**
   * Bind dropdown menu to active sessions. Bind selected dropdown menu item to
   * current session.
   */
  private void fillDropDown() {
    chooseSessionBox.setItems(appViewModel.getSessionNames());
    chooseSessionBox.valueProperty()
        .bindBidirectional(appViewModel.getCurrentSessionName());
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
    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
        "GVS files (*.gvs)", "*.gvs");
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
    appViewModel.loadSession(file);
  }

  @FXML
  private void saveSession() {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    fileChooser.setTitle("Save Session File");
    File file = fileChooser.showSaveDialog(stage);
    appViewModel.saveSession(file);
  }

  @FXML
  private void removeSession() {
    appViewModel.removeCurrentSession();
  }

  @FXML
  private void changeSession() {
    String name = chooseSessionBox.getValue();
    appViewModel.changeSession(name);
  }

  @FXML
  private void quitGVS() {
    appViewModel.terminateApplication();
  }
}
