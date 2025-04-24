package pchub.dao;

import pchub.model.Payment;
import pchub.model.enums.PaymentStatus;
import pchub.utils.DatabaseConnection;

import java.sql.*;

public class PaymentDao {
    private static final int MAX_PAYMENTS = 100;
    
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public boolean insertPayment(Payment payment) {
        String sql = "INSERT INTO payment (orderID, amount, paymentMethodID, transactionID, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, payment.getOrderId());
            preparedStatement.setBigDecimal(2, payment.getAmount());
            preparedStatement.setString(3, payment.getPaymentMethodId());
            preparedStatement.setString(4, payment.getTransactionId());
            preparedStatement.setString(5, payment.getStatus().toString().toLowerCase());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated ID
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()")) {
                    if (resultSet.next()) {
                        try (PreparedStatement idStmt = connection.prepareStatement("SELECT billID FROM payment WHERE orderID = ? ORDER BY date DESC LIMIT 1")) {
                            idStmt.setString(1, payment.getOrderId());
                            ResultSet idRs = idStmt.executeQuery();
                            if (idRs.next()) {
                                payment.setBillId(idRs.getString("billID"));
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
            return false;
        }
    }

    public Payment findById(String billId) {
        String sql = "SELECT * FROM payment WHERE billID = ?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToPayment(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment by ID: " + e.getMessage());
        }

        return null;
    }

    public Payment findByOrderId(String orderId) {
        String sql = "SELECT * FROM payment WHERE orderID = ? ORDER BY date DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToPayment(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment by order ID: " + e.getMessage());
        }

        return null;
    }

    public Payment[] findAll() {
        Payment[] payments = new Payment[MAX_PAYMENTS];
        String sql = "SELECT * FROM payment";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            int index = 0;
            while (resultSet.next() && index < MAX_PAYMENTS) {
                payments[index] = mapResultSetToPayment(resultSet);
                index++;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all payments: " + e.getMessage());
        }

        return payments;
    }

    public boolean updatePaymentStatus(String billId, PaymentStatus status) {
        String sql = "UPDATE payment SET status = ? WHERE billID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, status.toString().toLowerCase());
            preparedStatement.setString(2, billId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTransactionId(String billId, String transactionId) {
        String sql = "UPDATE payment SET transactionID = ? WHERE billID = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, transactionId);
            preparedStatement.setString(2, billId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating transaction ID: " + e.getMessage());
            return false;
        }
    }

    private Payment mapResultSetToPayment(ResultSet resultSet) throws SQLException {
        Payment payment = new Payment();
        payment.setBillId(resultSet.getString("billID"));
        payment.setOrderId(resultSet.getString("orderID"));
        payment.setAmount(resultSet.getBigDecimal("amount"));
        payment.setPaymentMethodId(resultSet.getString("paymentMethodID"));
        payment.setTransactionId(resultSet.getString("transactionID"));
        payment.setStatus(PaymentStatus.valueOf(resultSet.getString("status").toUpperCase()));

        Timestamp timestamp = resultSet.getTimestamp("date");
        if (timestamp != null) {
            payment.setDate(timestamp.toLocalDateTime());
        }

        return payment;
    }
}