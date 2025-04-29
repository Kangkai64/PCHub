package pchub.dao;

import pchub.model.Bill;
import pchub.model.enums.PaymentStatus;
import pchub.utils.DatabaseConnection;

import java.sql.*;

public class BillDao extends DaoTemplate<Bill> {
    private static final int MAX_BILLS = 100;

    @Override
    public Bill findById(String billId) throws SQLException {
        String sql = "SELECT * FROM bill WHERE billID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding bill by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean insert(Bill bill) throws SQLException {
        String sql = "INSERT INTO bill (billID, orderID, amount, payment_MethodID, transactionID, paymentStatus, issueDate) VALUES (?,?,?,?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, bill.getBillId());
            preparedStatement.setString(2, bill.getOrderId());
            preparedStatement.setBigDecimal(3, bill.getTotalAmount());
            preparedStatement.setString(4, bill.getPaymentMethod().getPaymentMethodId());
            preparedStatement.setString(5, bill.getTransactionId());
            preparedStatement.setString(6, bill.getPaymentStatus().toString());
            preparedStatement.setTimestamp(7, new Timestamp(bill.getIssueDate().getTime()));

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting bill: " + e.getMessage());
            return false;
        }
    }

    public Bill findByOrderId(String orderId) throws SQLException {
        String sql = "SELECT * FROM bill WHERE orderID = ? ORDER BY issueDate DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding bill by order ID: " + e.getMessage());
        }

        return null;
    }

    public Bill[] findAll() throws SQLException {
        Bill[] bills = new Bill[MAX_BILLS];
        String sql = "SELECT * FROM bill";
        int index = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next() && index < MAX_BILLS) {
                bills[index] = mapResultSet(resultSet);
                index++;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all bills: " + e.getMessage());
        }

        return bills;
    }

    @Override
    public boolean update(Bill bill) throws SQLException {
        String sql = "UPDATE bill SET orderID = ?, amount = ?, payment_MethodID = ?, transactionID = ?, paymentStatus = ?, issueDate = ? WHERE billID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, bill.getOrderId());
            preparedStatement.setBigDecimal(2, bill.getTotalAmount());
            preparedStatement.setString(3, bill.getPaymentMethod().getPaymentMethodId());
            preparedStatement.setString(4, bill.getTransactionId());
            preparedStatement.setString(5, bill.getPaymentStatus().toString());
            preparedStatement.setTimestamp(6, new Timestamp(bill.getIssueDate().getTime()));
            preparedStatement.setString(7, bill.getBillId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bill: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String billId) throws SQLException {
        String sql = "DELETE FROM bill WHERE billID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, billId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting bill: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Bill mapResultSet(ResultSet resultSet) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(resultSet.getString("billID"));
        bill.setOrderId(resultSet.getString("orderID"));
        bill.setTotalAmount(resultSet.getBigDecimal("amount"));
        bill.getPaymentMethod().setPaymentMethodId(resultSet.getString("payment_MethodID"));
        bill.setTransactionId(resultSet.getString("transactionID"));
        bill.setPaymentStatus(PaymentStatus.valueOf(resultSet.getString("paymentStatus")));

        Timestamp timestamp = resultSet.getTimestamp("issueDate");
        if (timestamp != null) {
            bill.setIssueDate(new java.util.Date(timestamp.getTime()));
        }

        return bill;
    }
}