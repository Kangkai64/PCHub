package pchub.utils;

import pchub.dao.UserDao;

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
    private static final UserDao userDao = new UserDao();

    /**
     * Establishes and returns a connection to the database
     * @return Connection object for database operations
     */
    public static Connection getConnection() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a new connection each time
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Database connection failed");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes the database connection
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing database connection");
                e.printStackTrace();
            }
        }
    }

    public static void resetPassword(String password) {
        try {
            userDao.updateAllPasswords(password);
            System.out.println("Password reset successfully.");
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}