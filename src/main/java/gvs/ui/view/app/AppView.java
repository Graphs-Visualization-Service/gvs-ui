package gvs.ui.view.app;

import java.io.File;

import gvs.interfaces.ISessionController;
import gvs.ui.logic.app.AppViewModel;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * MVVM View Class.
 * 
 * @author mtrentini
 */
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

  @FXML
  private ComboBox<String> chooseSessionBox;

  @FXML
  private AnchorPane displaySessionPane;

  private AppViewModel model;
  
  @FXML
  private void initialize() {
    setLogoAsBackground();
  }

  private void fillDropDown() {
    chooseSessionBox.setItems(model.getSessionControllers());
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
    displaySessionPane.setBackground(new Background(myBI));
  }

  @FXML
  private void loadSession() {
    Stage stage = (Stage) displaySessionPane.getScene().getWindow();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Session File");
    File file = fileChooser.showOpenDialog(stage);
    model.loadSession(file);
  }
  
  @FXML
  private void saveSession() {
    model.saveSession();
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
    Platform.exit();
  }

  public void setAppViewModel(AppViewModel appViewModel) {
    model = appViewModel;
    fillDropDown();
  }

}
