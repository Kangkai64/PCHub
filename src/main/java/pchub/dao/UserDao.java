package pchub.dao;

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
        String sql = "INSERT INTO user (userID, username, email, password, registrationDate, lastLogin, status, " +
                "phone, fullName, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, PasswordUtils.hashPassword(user.getPassword()));
            preparedStatement.setDate(5, new Date(user.getRegistrationDate().getTime()));
            preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // Current timestamp for last login
            preparedStatement.setString(7, user.getStatus() != null ? user.getStatus() : "Active");
            preparedStatement.setString(8, user.getPhone());
            preparedStatement.setString(9, user.getFullName());
            preparedStatement.setString(10, user.getRole().name());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            return false;
        }
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
    public User mapResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
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
        return user;
    }
}