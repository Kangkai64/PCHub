package pchub.dao;

import pchub.model.UserPaymentMethod;
import pchub.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserPaymentMethodDao {
    private final Connection connection;

    public UserPaymentMethodDao() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void addUserPaymentMethod(UserPaymentMethod userPaymentMethod) throws SQLException {
        String sql = "INSERT INTO user_payment_method (userPaymentMethodID, userID, paymentMethodID, details, isDefault, addedDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userPaymentMethod.getUserPaymentMethodId());
            statement.setString(2, userPaymentMethod.getUserId());
            statement.setString(3, userPaymentMethod.getPaymentMethodId());
            statement.setString(4, userPaymentMethod.getDetails());
            statement.setBoolean(5, userPaymentMethod.isDefault());
            statement.setTimestamp(6, Timestamp.valueOf(userPaymentMethod.getAddedDate()));
            
            statement.executeUpdate();
        }
    }

    public List<UserPaymentMethod> getUserPaymentMethods(String userId) throws SQLException {
        List<UserPaymentMethod> paymentMethods = new ArrayList<>();
        String sql = "SELECT * FROM user_payment_method WHERE userID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                UserPaymentMethod paymentMethod = new UserPaymentMethod();
                paymentMethod.setUserPaymentMethodId(resultSet.getString("userPaymentMethodID"));
                paymentMethod.setUserId(resultSet.getString("userID"));
                paymentMethod.setPaymentMethodId(resultSet.getString("paymentMethodID"));
                paymentMethod.setDetails(resultSet.getString("details"));
                paymentMethod.setDefault(resultSet.getBoolean("isDefault"));
                paymentMethod.setAddedDate(resultSet.getTimestamp("addedDate").toLocalDateTime());
                
                paymentMethods.add(paymentMethod);
            }
        }
        
        return paymentMethods;
    }

    public UserPaymentMethod getDefaultPaymentMethod(String userId) throws SQLException {
        String sql = "SELECT * FROM user_payment_method WHERE userID = ? AND isDefault = true";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                UserPaymentMethod paymentMethod = new UserPaymentMethod();
                paymentMethod.setUserPaymentMethodId(resultSet.getString("userPaymentMethodID"));
                paymentMethod.setUserId(resultSet.getString("userID"));
                paymentMethod.setPaymentMethodId(resultSet.getString("paymentMethodID"));
                paymentMethod.setDetails(resultSet.getString("details"));
                paymentMethod.setDefault(resultSet.getBoolean("isDefault"));
                paymentMethod.setAddedDate(resultSet.getTimestamp("addedDate").toLocalDateTime());
                
                return paymentMethod;
            }
        }
        
        return null;
    }

    public void updateDefaultPaymentMethod(String userId, String userPaymentMethodId) throws SQLException {
        // First, set all payment methods for this user to non-default
        String resetSql = "UPDATE user_payment_method SET isDefault = false WHERE userID = ?";
        try (PreparedStatement resetStatement = connection.prepareStatement(resetSql)) {
            resetStatement.setString(1, userId);
            resetStatement.executeUpdate();
        }
        
        // Then, set the specified payment method as default
        String updateSql = "UPDATE user_payment_method SET isDefault = true WHERE userPaymentMethodID = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            updateStatement.setString(1, userPaymentMethodId);
            updateStatement.executeUpdate();
        }
    }

    public void deleteUserPaymentMethod(String userPaymentMethodId) throws SQLException {
        String sql = "DELETE FROM user_payment_method WHERE userPaymentMethodID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userPaymentMethodId);
            statement.executeUpdate();
        }
    }
} 