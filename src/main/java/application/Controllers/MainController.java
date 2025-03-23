package application.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainController {

    private Stage primaryStage;
    private Scene scene;
    private FXMLLoader fxmlLoader;

    @FXML
    private BorderPane mainPane;

    @FXML
    private AnchorPane logo, cartPageButton, loginPageButton, helpPageButton;

    @FXML
    private ImageView exitButton;

    public void switchScene(String fxmlPath, MouseEvent event) throws IOException {
        try {
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            setPrimaryStage(stage);

            // Store current stage properties
            boolean isFullScreen = stage.isFullScreen();
            double width = stage.getWidth();
            double height = stage.getHeight();

            // Always load a fresh copy of the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            setFxmlLoader(loader);

            // Load the root node
            Parent root = loader.load();

            // Create scene with explicit dimensions
            Scene newScene = new Scene(root);
            setScene(newScene);
            applyStylesheet(newScene);

            // Pre-process layout before showing
            root.applyCss();
            root.layout();

            stage.setScene(newScene);

            // Restore stage properties
            stage.setFullScreen(isFullScreen);
            if (!isFullScreen) {
                stage.setWidth(width);
                stage.setHeight(height);
            }

            stage.show();
            System.out.println("Successfully switched to: " + fxmlPath);
        } catch (Exception exception) {
            System.err.println("Failed to switch scene to: " + fxmlPath);
            exception.printStackTrace();
        }
    }

    protected void applyStylesheet(Scene scene) {
        String css = Objects.requireNonNull(getClass().getResource("/application/css/main.css")).toExternalForm();
        scene.getStylesheets().add(css);
    }

    public void onExitButtonClicked(MouseEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You are about to exit");
        alert.setContentText("Are you sure you want to exit?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            primaryStage = (Stage) mainPane.getScene().getWindow();
            primaryStage.close();
        }
    }

    public void switchToMainMenu(MouseEvent actionEvent) throws IOException {
        switchScene("/application/FXMLs/MainMenu.fxml", actionEvent);
    }

    public void switchToCart(MouseEvent actionEvent) throws IOException {
        switchScene("/application/FXMLs/Cart.fxml", actionEvent);
    }

    public void switchToLogin(MouseEvent actionEvent) throws IOException {
        switchScene("/application/FXMLs/Login.fxml", actionEvent);
    }

    public void switchToHelp(MouseEvent actionEvent) throws IOException {
        switchScene("/application/FXMLs/Help.fxml", actionEvent);
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
