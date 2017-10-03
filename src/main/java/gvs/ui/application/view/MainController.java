package gvs.ui.application.view;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

public class MainController {

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
    private void initialize() {
        // set data for window here
    }

    /**
     * Called when the user clicks on the load button.
     */
    @FXML
    private void loadSession() {

    }
  

}
