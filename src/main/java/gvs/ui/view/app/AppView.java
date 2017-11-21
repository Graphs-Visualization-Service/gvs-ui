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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
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
  private Button importSessionBtn;

  @FXML
  private Button saveSessionBtn;

  @FXML
  private Button deleteSessionBtn;

  @FXML
  private ComboBox<String> chooseSessionBox;

  @FXML
  private BorderPane rootPane;

  @FXML
  private AnchorPane sessionContainer;

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

  private void initFileChooser() {
    fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
        "GVS files (*.gvs)", "*.gvs");
    fileChooser.getExtensionFilters().add(extensionFilter);
  }

  private void initButtonlabels() {
    importSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.UPLOAD));
    importSessionBtn.setTooltip(new Tooltip("Load existing Session"));

    saveSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.SAVE));
    saveSessionBtn.setTooltip(new Tooltip("Store Session"));

    deleteSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.TRASH));
    deleteSessionBtn.setTooltip(new Tooltip("Delete Session"));
  }

  @FXML
  private void loadSession() {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    fileChooser.setTitle("Load Session File");
    File file = fileChooser.showOpenDialog(stage);
    sessionContainer.getStyleClass().remove("logo-bg");
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
    if (appViewModel.getSessionNames().isEmpty()) {
      sessionContainer.getStyleClass().add("logo-bg");
    }
  }

  @FXML
  private void changeSession() {
    String name = chooseSessionBox.getValue();
    appViewModel.changeSession(name);
  }
}
