package pchub.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections
 */
public class DatabaseConnection {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/pchub";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    /**
     * Establishes and returns a connection to the database
     * @return Connection object for database operations
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load the MySQL JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC driver not found");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Database connection failed");
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Closes the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing database connection");
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }
}