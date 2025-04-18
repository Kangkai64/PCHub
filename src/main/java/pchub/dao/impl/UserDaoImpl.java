package pchub.dao.impl;

import pchub.dao.UserDao;
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
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public User findById(String userId) {
        String sql = "SELECT * FROM user WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }

        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
        }

        return users;
    }

    @Override
    public boolean insertUser(User user) {
        String sql = "INSERT INTO user (userID, username, email, password, registrationDate, lastLogin, status, " +
                "phone, fullName, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

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
    public boolean updateUser(User user) {
        String sql = "UPDATE user SET username = ?, email = ?, password = ?, lastLogin = ?, " +
                "status = ?, phone = ?, fullName = ?, role = ? WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

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

    @Override
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
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM user WHERE userID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, userId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
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