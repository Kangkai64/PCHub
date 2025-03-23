package application.Controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.Utils.BCrypt;
import application.Utils.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class LoginController extends MainController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button resetButton;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize database connection
        connection = DatabaseConnection.getConnection();

        // Set action for login button
        loginButton.setOnAction(this::login);

        // Set action for reset button
        resetButton.setOnAction(this::resetFields);
    }

    /**
     * Handles the login process by validating user credentials against the database
     * @param event The action event triggered by the login button
     */
    @FXML
    private void login(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validate input fields
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(AlertType.ERROR, "Error", "Empty Fields", "Please fill in all fields");
            return;
        }

        // Check credentials in database
        String query = "SELECT * FROM user WHERE email = ?";

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Get the stored hashed password from the database
                String storedHashedPassword = resultSet.getString("password");
                // Uncomment these two line to fix the password, edit in the MySQL
                // System.out.println(resultSet.getString("password"));
                // System.out.println(hashPassword(password));

                // Verify the password
                if (checkPassword(password, storedHashedPassword)) {
                    // Login successful
                    showAlert(AlertType.INFORMATION, "Success", "Login Successful", "Welcome to PCHub!");

                    // Store user information in a session or application context if needed
                    // For example: UserSession.getInstance().setCurrentUser(resultSet.getInt("id"), resultSet.getString("name"));

                } else {
                    // Password doesn't match
                    showAlert(AlertType.ERROR, "Error", "Login Failed", "Invalid email or password");
                }
            } else {
                // User not found
                showAlert(AlertType.ERROR, "Error", "Login Failed", "Invalid email or password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Login Error", "Error connecting to database: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String hashPassword(String plainTextPassword) {
        // The gensalt() method determines the complexity (work factor)
        // Default is 10, higher is more secure but slower
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    // To verify a password
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    /**
     * Clears the email and password fields
     * @param event The action event triggered by the reset button
     */
    @FXML
    private void resetFields(ActionEvent event) {
        emailField.clear();
        passwordField.clear();
    }

    /**
     * Utility method for showing alerts
     * @param type Alert type
     * @param title Alert title
     * @param header Alert header
     * @param content Alert content
     */
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}