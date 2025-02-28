package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class MainMenuController extends MainController {
    @FXML
    private Button loginButton, aboutUsButton, exitButton;

    public void switchToLogin(ActionEvent actionEvent) throws IOException {
        switchScene("/application/FXMLs/Login.fxml", actionEvent);
    }

    public void switchToAboutUs(ActionEvent actionEvent) throws IOException {
        switchScene("/application/FXMLs/AboutUs.fxml", actionEvent);
    }
}