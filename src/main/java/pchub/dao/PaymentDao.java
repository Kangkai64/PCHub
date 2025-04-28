package pchub.dao;

import pchub.model.Payment;
import pchub.model.enums.PaymentStatus;
import pchub.utils.DatabaseConnection;

import java.sql.*;

public class PaymentDao extends DaoTemplate<Payment> {
    private static final int MAX_PAYMENTS = 100;

    @Override
    public Payment findById(String billId) throws SQLException {
        String sql = "SELECT * FROM payment WHERE billID = ?";  

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean insert(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (orderID, amount, paymentMethodID, transactionID, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
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

    public Payment findByOrderId(String orderId) throws SQLException {
        String sql = "SELECT * FROM payment WHERE orderID = ? ORDER BY date DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding payment by order ID: " + e.getMessage());
        }

        return null;
    }

    public Payment[] findAll() throws SQLException {
        Payment[] payments = new Payment[MAX_PAYMENTS];
        String sql = "SELECT * FROM payment";
        int index = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next() && index < MAX_PAYMENTS) {
                payments[index] = mapResultSet(resultSet);
                index++;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all payments: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public boolean update(Payment payment) throws SQLException {
        String sql = "UPDATE payment SET orderID = ?, amount = ?, paymentMethodID = ?, transactionID = ?, status = ?, date = ? WHERE billID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, payment.getOrderId());
            preparedStatement.setBigDecimal(2, payment.getAmount());
            preparedStatement.setString(3, payment.getPaymentMethodId());
            preparedStatement.setString(4, payment.getTransactionId());
            preparedStatement.setString(5, payment.getStatus().toString().toLowerCase());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(payment.getDate()));
            preparedStatement.setString(7, payment.getBillId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String billId) throws SQLException {
        String sql = "DELETE FROM payment WHERE billID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, billId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting payment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Payment mapResultSet(ResultSet resultSet) throws SQLException {
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