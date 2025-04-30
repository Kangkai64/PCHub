package pchub.dao;

import pchub.model.PaymentMethod;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class PaymentMethodDao extends DaoTemplate<PaymentMethod> {
    final static int MAX_PAYMENT_METHOD = 50;

    @Override
    public PaymentMethod findById(String paymentMethodId) {
        String sql = "SELECT * FROM payment_method WHERE payment_methodID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethodId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving payment method by ID: " + e.getMessage());
        }

        return null;
    }

    public PaymentMethod[] findAll() {
        PaymentMethod[] paymentMethods = new PaymentMethod[MAX_PAYMENT_METHOD];
        int index = 0;
        String sql = "SELECT * FROM payment_method";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                paymentMethods[index++] = mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all payment methods: " + e.getMessage());
        }

        return paymentMethods;
    }

    @Override
    public boolean insert(PaymentMethod paymentMethod) {
        String sql = "INSERT INTO payment_method (payment_methodID, name, description, addedDate) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethod.getPaymentMethodId());
            preparedStatement.setString(2, paymentMethod.getName());
            preparedStatement.setString(3, paymentMethod.getDescription());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(paymentMethod.getAddedDate().atStartOfDay()));

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saving payment method: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(PaymentMethod paymentMethod) {
        String sql = "UPDATE payment_method SET name = ?, description = ? WHERE payment_methodID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethod.getName());
            preparedStatement.setString(2, paymentMethod.getDescription());
            preparedStatement.setString(3, paymentMethod.getPaymentMethodId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment method: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String paymentMethodId) {
        String sql = "DELETE FROM payment_method WHERE payment_methodID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, paymentMethodId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment method: " + e.getMessage());
            return false;
        }
    }

    @Override
    public PaymentMethod mapResultSet(ResultSet resultSet) throws SQLException {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(resultSet.getString("payment_methodID"));
        paymentMethod.setName(resultSet.getString("name"));
        paymentMethod.setDescription(resultSet.getString("description"));
        paymentMethod.setAddedDate(resultSet.getTimestamp("addedDate").toLocalDateTime().toLocalDate());
        return paymentMethod;
    }
}