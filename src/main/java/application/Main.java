package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLs/MainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/icon.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("PCHub");
        primaryStage.setScene(scene);

        String css = Objects.requireNonNull(getClass().getResource("css/main.css")).toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            logout(primaryStage);
        });
    }

    public void logout(Stage stage){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to exit!");
        alert.setContentText("Do you want to save before exiting?");

        if (alert.showAndWait().get() == ButtonType.OK){
            stage.close();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}