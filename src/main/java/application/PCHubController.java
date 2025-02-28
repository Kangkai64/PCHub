package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class PCHubController {

    private Stage primaryStage;

    @FXML
    private Button loginButton, aboutUsButton, exitButton;

    @FXML
    private AnchorPane mainMenuPane;

    public void initialize(ResourceBundle resources) {
        String imagePath = "css/images/mainMenuBackground.png";
        System.out.println(imagePath);
        BackgroundImageUtil.setBackgroundImage(mainMenuPane, imagePath, false);
    }

    public void onExitButtonClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit");
        alert.setContentText("Are you sure you want to exit?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            primaryStage = (Stage) mainMenuPane.getScene().getWindow();
            primaryStage.close();
        }
    }
}