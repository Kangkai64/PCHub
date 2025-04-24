package pchub.dao;

import pchub.model.PaymentMethod;
import pchub.model.enums.PaymentType;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDao {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public List<PaymentMethod> getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        String sql = "SELECT * FROM payment_method";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                paymentMethods.add(mapResultSetToPaymentMethod(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all payment methods: " + e.getMessage());
        }

        return paymentMethods;
    }

    public PaymentMethod getPaymentMethodById(String paymentMethodId) {
        String sql = "SELECT * FROM payment_method WHERE paymentMethodID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethodId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToPaymentMethod(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment method by ID: " + e.getMessage());
        }

        return null;
    }

    public boolean save(PaymentMethod paymentMethod) {
        String sql = "INSERT INTO payment_method (paymentMethodID, name, description, type, addedDate) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethod.getPaymentMethodId());
            preparedStatement.setString(2, paymentMethod.getName());
            preparedStatement.setString(3, paymentMethod.getDescription());
            preparedStatement.setString(4, paymentMethod.getType().toString());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(paymentMethod.getAddedDate().atStartOfDay()));

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saving payment method: " + e.getMessage());
            return false;
        }
    }

    public boolean update(PaymentMethod paymentMethod) {
        String sql = "UPDATE payment_method SET name = ?, description = ?, type = ? WHERE paymentMethodID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethod.getName());
            preparedStatement.setString(2, paymentMethod.getDescription());
            preparedStatement.setString(3, paymentMethod.getType().toString());
            preparedStatement.setString(4, paymentMethod.getPaymentMethodId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment method: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String paymentMethodId) {
        String sql = "DELETE FROM payment_method WHERE paymentMethodID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethodId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment method: " + e.getMessage());
            return false;
        }
    }

    private PaymentMethod mapResultSetToPaymentMethod(ResultSet resultSet) throws SQLException {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(resultSet.getString("paymentMethodID"));
        paymentMethod.setName(resultSet.getString("name"));
        paymentMethod.setDescription(resultSet.getString("description"));
        paymentMethod.setType(PaymentType.valueOf(resultSet.getString("type")));
        paymentMethod.setAddedDate(resultSet.getTimestamp("addedDate").toLocalDateTime().toLocalDate());
        return paymentMethod;
    }
} 