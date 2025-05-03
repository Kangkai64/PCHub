package pchub.dao;

import pchub.model.Customer;
import pchub.model.User;
import pchub.model.enums.UserRole;
import pchub.utils.DatabaseConnection;
import pchub.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;

public class UserDao extends DaoTemplate<User> {
    final static int MAX_USER = 999;

    @Override
    public User findById(String userId) throws SQLException {
        String sql = "SELECT * FROM user WHERE userID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Finds a customer by their user ID.
     *
     * @param userId The ID of the customer to find
     * @return The Customer object or null if not found or if user is not a customer
     * @throws SQLException If a database error occurs
     */
    public Customer findCustomerById(String userId) throws SQLException {
        User user = findById(userId);
        if (user != null && user.getRole() == UserRole.CUSTOMER) {
            return convertToCustomer(user);
        }
        return null;
    }

    /**
     * Converts a User object to a Customer object if the role is CUSTOMER.
     * Uses the Customer(User) constructor to simplify conversion.
     *
     * @param user The User object to convert
     * @return A Customer object with the User's properties
     */
    private Customer convertToCustomer(User user) {
        if (user == null || user.getRole() != UserRole.CUSTOMER) {
            return null;
        }

        // Create a new Customer instance using the constructor that takes a User
        Customer customer = new Customer(user);

        // Load any Customer-specific properties if needed
        loadCustomerSpecificProperties(customer);

        return customer;
    }

    /**
     * Loads any additional Customer-specific properties from the database.
     *
     * @param customer The Customer object to load properties for
     */
    private void loadCustomerSpecificProperties(Customer customer) {
        // If Customer has additional properties that are stored in a separate table,
        // you would load them here using an additional query
        // For example:
        // String sql = "SELECT * FROM customer_details WHERE userID = ?";
        // ...
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }

        return null;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }

        return null;
    }

    public User[] findAll() throws SQLException {
        User[] users = new User[MAX_USER];
        int index = 0;
        String sql = "SELECT * FROM user";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                users[index] = mapResultSet(resultSet);
                index++;
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean insert(User user) throws SQLException {
        String sql = "INSERT INTO user (username, email, password, registrationDate, lastLogin, status, " +
                "phone, fullName, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, PasswordUtils.hashPassword(user.getPassword()));
            preparedStatement.setDate(4, new Date(user.getRegistrationDate().getTime()));
            preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Current timestamp for last login
            preparedStatement.setString(6, user.getStatus() != null ? user.getStatus() : "Active");
            preparedStatement.setString(7, user.getPhone());
            preparedStatement.setString(8, user.getFullName());
            preparedStatement.setString(9, user.getRole().name());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the last inserted user ID using a separate query
                String getLastIdSql = "SELECT userID FROM `user` WHERE role = ? ORDER BY registrationDate DESC LIMIT 1";
                try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                    preparedStatement.setString(1, user.getRole().name());
                    try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                        if (resultSet.next()) {
                            String userId = resultSet.getString("userID");
                            user.setUserId(userId);
                            return true;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
        return false;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE user SET username = ?, email = ?, password = ?, lastLogin = ?, " +
                "status = ?, phone = ?, fullName = ?, role = ? WHERE userID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, PasswordUtils.hashPassword(user.getPassword()));
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(5, user.getStatus());
            preparedStatement.setString(6, user.getPhone());
            preparedStatement.setString(7, user.getFullName());
            preparedStatement.setString(8, user.getRole().name());
            preparedStatement.setString(9, user.getUserId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAllPasswords(String newPassword) {
        String sql = "UPDATE user SET password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            // Hash the password before storing
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            preparedStatement.setString(1, hashedPassword);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating passwords: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String userId) throws SQLException {
        String sql = "DELETE FROM user WHERE userID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected User mapResultSet(ResultSet resultSet) throws SQLException {
        String role = resultSet.getString("role");
        UserRole userRole = UserRole.valueOf(role);

        // For future flexibility, you can create different user types based on role
        if (userRole == UserRole.CUSTOMER) {
            Customer customer = new Customer();
            mapBaseUserFields(customer, resultSet);
            // Map any Customer-specific fields
            return customer;
        } else {
            User user = new User();
            mapBaseUserFields(user, resultSet);
            return user;
        }
    }

    /**
     * Maps the common user fields from the result set to the User object
     *
     * @param user The User object to map fields to
     * @param resultSet The ResultSet containing the user data
     * @throws SQLException If a database error occurs
     */
    private void mapBaseUserFields(User user, ResultSet resultSet) throws SQLException {
        user.setUserId(resultSet.getString("userID"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setEmail(resultSet.getString("email"));
        user.setRegistrationDate(resultSet.getTimestamp("registrationDate"));
        user.setLastLogin(resultSet.getTimestamp("lastLogin"));
        user.setStatus(resultSet.getString("status"));
        user.setPhone(resultSet.getString("phone"));
        user.setFullName(resultSet.getString("fullName"));
        user.setRole(UserRole.valueOf(resultSet.getString("role")));
    }
}