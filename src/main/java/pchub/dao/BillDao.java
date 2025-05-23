package pchub.dao;

import pchub.model.*;
import pchub.model.enums.PaymentStatus;
import pchub.model.enums.UserRole;
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
        String sql = "INSERT INTO bill (orderID, amount, payment_MethodID, paymentStatus, issueDate) VALUES (?,?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, bill.getOrder().getOrderId());
            preparedStatement.setBigDecimal(2, bill.getTotalAmount());
            preparedStatement.setString(3, bill.getPaymentMethod().getPaymentMethodId());
            preparedStatement.setString(4, bill.getPaymentStatus().toString());
            preparedStatement.setTimestamp(5, new Timestamp(bill.getIssueDate().getTime()));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the last inserted bill ID using a separate query
                String getLastIdSql = "SELECT billID FROM bill WHERE orderID = ? ORDER BY billID DESC LIMIT 1";
                try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                    getLastIdStmt.setString(1, bill.getOrder().getOrderId());

                    try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                        if (resultSet.next()) {
                            String billId = resultSet.getString("billID");
                            bill.setBillId(billId);
                            return true;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting bill: " + e.getMessage());
            return false;
        }
        return false;
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
        String sql = "UPDATE bill SET orderID = ?, amount = ?, payment_MethodID = ?, paymentStatus = ?, issueDate = ? WHERE billID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, bill.getOrder().getOrderId());
            preparedStatement.setBigDecimal(2, bill.getTotalAmount());
            preparedStatement.setString(3, bill.getPaymentMethod().getPaymentMethodId());
            preparedStatement.setString(4, bill.getPaymentStatus().toString());
            preparedStatement.setTimestamp(5, new Timestamp(bill.getIssueDate().getTime()));
            preparedStatement.setString(6, bill.getBillId());

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
    protected Bill mapResultSet(ResultSet resultSet) throws SQLException {
        OrderDao orderDao = new OrderDao();
        UserDao userDao = new UserDao();
        TransactionDao transactionDao = new TransactionDao();

        Bill bill = new Bill();
        bill.setBillId(resultSet.getString("billID"));
        String orderId = resultSet.getString("orderID");
        Order order = orderDao.findById(orderId);
        bill.setOrder(order);

        // Get the user ID from the order
        String customerId = order.getCustomer().getUserId();
        User user = userDao.findById(customerId);

        // Simple conversion using the Customer constructor
        if (user != null && user.getRole() == UserRole.CUSTOMER) {
            Customer customer = new Customer(user);
            bill.setCustomer(customer);
        } else {
            // Handle the case where the user is not a Customer or doesn't exist
            throw new IllegalStateException("User with ID " + customerId + " is not a Customer or doesn't exist");
        }

        bill.setTotalAmount(resultSet.getBigDecimal("amount"));
        bill.getPaymentMethod().setPaymentMethodId(resultSet.getString("payment_MethodID"));
        bill.setPaymentStatus(PaymentStatus.valueOf(resultSet.getString("paymentStatus")));

        Timestamp timestamp = resultSet.getTimestamp("issueDate");
        if (timestamp != null) {
            bill.setIssueDate(new java.util.Date(timestamp.getTime()));
        }

        return bill;
    }
}