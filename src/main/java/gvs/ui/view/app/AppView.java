package gvs.ui.view.app;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import gvs.model.Session;
import gvs.ui.logic.app.AppViewModel;
import gvs.ui.view.session.SessionView;
import gvs.util.Configuration;
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
import javafx.stage.FileChooser.ExtensionFilter;
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
  private ComboBox<Session> chooseSessionBox;

  @FXML
  private BorderPane rootPane;

  @FXML
  private AnchorPane sessionContainer;

  @FXML
  private Parent sessionView;

  @FXML
  private SessionView sessionViewController;

  private final FileChooser fileChooser;
  private final AppViewModel appViewModel;

  private static final String DELETE_SESSION_TOOLTIP = "Delete Session";
  private static final String STORE_SESSION_TOOLTIP = "Store Session";
  private static final String LOAD_SESSION_TOOLTIP = "Load existing Session";
  private static final String CHOOSE_SESSION_TOOLTIP = "Choose active session";

  @Inject
  public AppView(AppViewModel appViewModel) {
    this.appViewModel = appViewModel;
    this.fileChooser = new FileChooser();
  }

  @FXML
  private void initialize() {
    rootPane.setPrefWidth(Configuration.getWindowWidth());
    rootPane.setPrefHeight(Configuration.getWindowHeight());

    initializeSessionView();
    initializeButtons();
    initializeFileChooser();
    initializeSessionDropdown();
  }

  /**
   * Bind dropdown menu to active sessions. Bind selected dropdown menu item to
   * current session.
   */
  private void initializeSessionDropdown() {
    chooseSessionBox.disableProperty()
        .bind(sessionViewController.isReplayingProperty());

    chooseSessionBox.setItems(appViewModel.getSessions());
    chooseSessionBox.valueProperty()
        .bindBidirectional(appViewModel.getCurrentSession());
  }

  /**
   * Set gvs file extension to file chooser
   */
  private void initializeFileChooser() {
    ExtensionFilter extensionFilter = new ExtensionFilter("GVS files (*.gvs)",
        "*.gvs");
    fileChooser.getExtensionFilters().add(extensionFilter);
  }

  /**
   * Initialize button label, tooltip and disable property
   */
  private void initializeButtons() {
    importSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.UPLOAD));
    importSessionBtn.setTooltip(new Tooltip(LOAD_SESSION_TOOLTIP));
    importSessionBtn.disableProperty()
        .bind(sessionViewController.isReplayingProperty());

    saveSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.SAVE));
    saveSessionBtn.setTooltip(new Tooltip(STORE_SESSION_TOOLTIP));
    saveSessionBtn.disableProperty()
        .bind(appViewModel.sessionVisibilityProperty().not()
            .or(sessionViewController.isReplayingProperty()));

    deleteSessionBtn.setGraphic(FontAwesome.createLabel(Glyph.TRASH));
    deleteSessionBtn.setTooltip(new Tooltip(DELETE_SESSION_TOOLTIP));
    deleteSessionBtn.disableProperty()
        .bind(appViewModel.sessionVisibilityProperty().not()
            .or(sessionViewController.isReplayingProperty()));

    chooseSessionBox.setTooltip(new Tooltip(CHOOSE_SESSION_TOOLTIP));
  }

  /**
   * Initialize session view visiblity and handle logo visiblity
   */
  private void initializeSessionView() {
    sessionView.setVisible(false);
    sessionView.visibleProperty()
        .bind(appViewModel.sessionVisibilityProperty());
    sessionView.visibleProperty().addListener((o, oldValue, newValue) -> {
      if (newValue) {
        sessionContainer.getStyleClass().remove("logo-bg");
      } else {
        sessionContainer.getStyleClass().add("logo-bg");
      }
    });
  }

  @FXML
  private void loadSession() {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    fileChooser.setTitle("Load Session File");
    File file = fileChooser.showOpenDialog(stage);
    // don't change this! file is null, when cancel is clicked!
    if (file != null) {
      appViewModel.loadSession(file);
    }
  }

  @FXML
  private void saveSession() {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    fileChooser.setTitle("Save Session File");
    File file = fileChooser.showSaveDialog(stage);
    // make sure the file extension is set -> this seems to be a problem only in
    // linux
    String chosenFilePath = file.getPath();
    if (!chosenFilePath.endsWith(".gvs")) {
      File fileWithExtension = new File(file.getPath() + ".gvs");
      if (fileWithExtension.exists()) {
        file = new File(chosenFilePath + "_copy.gvs");
      } else {
        file = fileWithExtension;
      }
    }
    appViewModel.saveSession(file);
  }

  @FXML
  private void removeSession() {
    appViewModel.removeCurrentSession();
  }

  @FXML
  private void changeSession() {
    if (chooseSessionBox.getValue() != null) {
      appViewModel.changeSession(chooseSessionBox.getValue());
    }
  }
}
