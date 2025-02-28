package application.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private Stage primaryStage;
    private Scene scene;
    private FXMLLoader fxmlLoader;

    @FXML
    private AnchorPane mainMenuPane;

    public void switchScene(String fxmlPath, ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            setFxmlLoader(loader);

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            setPrimaryStage(stage);

            Scene scene = new Scene(loader.load());
            setScene(scene);

            stage.setScene(scene);
            stage.show();

            System.out.println("Successfully switched to: " + fxmlPath);
        } catch (Exception exception) {
            System.err.println("Failed to switch scene to: " + fxmlPath);
            exception.printStackTrace();
        }
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

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public void setFxmlLoader(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }
}
