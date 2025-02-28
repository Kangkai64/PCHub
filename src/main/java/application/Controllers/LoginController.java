package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class LoginController extends MainController {

    @FXML
    private Button mainMenuButton;

    public void switchToMainMenu(ActionEvent actionEvent) throws IOException {
        switchScene("/application/FXMLs/MainMenu.fxml", actionEvent);
    }
}
