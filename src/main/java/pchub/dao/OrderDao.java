package pchub.dao;

import pchub.model.Address;
import pchub.model.Order;
import pchub.model.OrderItem;
import pchub.model.PaymentMethod;
import pchub.model.enums.OrderStatus;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;

public class OrderDao extends DaoTemplate<Order> {
    private static final int MAX_ITEMS = 30;
    private final PaymentMethodDao paymentMethodDao;

    public OrderDao() {
        this.paymentMethodDao = new PaymentMethodDao();
    }

    @Override
    public Order findById(String orderId) throws SQLException {
        String sql = "SELECT * FROM `order` WHERE orderID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Order order = mapResultSet(resultSet);
                loadOrderItems(connection, order);
                return order;
            }
        } catch (SQLException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
        }

        return null;
    }

    public Order[] findByUserId(String customerId) throws SQLException {
        Order[] orders = new Order[30];
        int index = 0;
        String sql = "SELECT * FROM `order` WHERE customerID = ? ORDER BY orderDate DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next() && index < orders.length) {
                Order order = mapResultSet(resultSet);
                loadOrderItems(connection, order);
                orders[index++] = order;
            }
            if (index == 0) {
                return new Order[0];
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by user ID: " + e.getMessage());
        }

        return orders;
    }

    public Order[] findAll() throws SQLException {
        Order[] orders = new Order[30];
        int index = 0;
        String sql = "SELECT * FROM `order` ORDER BY orderDate DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next() && index < orders.length) {
                Order order = mapResultSet(resultSet);
                loadOrderItems(connection, order);
                orders[index++] = order;
            }
        } catch (SQLException e) {
            System.err.println("Error finding all orders: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public boolean insert(Order order) throws SQLException {
        String sql = "INSERT INTO `order` (customerID, orderDate, order_status, totalAmount, " +
                "shipping_addressID, payment_MethodID) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, order.getCustomerId());
                preparedStatement.setTimestamp(2, new Timestamp(order.getOrderDate().getTime()));
                preparedStatement.setString(3, order.getStatus().name().toLowerCase());
                preparedStatement.setDouble(4, order.getTotalAmount());
                preparedStatement.setString(5, formatAddressToString(order.getShippingAddress()));
                preparedStatement.setString(6, order.getPaymentMethod().getPaymentMethodId());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // Get the last inserted order ID using a separate query
                    String getLastIdSql = "SELECT orderID FROM `order` WHERE customerID = ? ORDER BY orderID DESC LIMIT 1";
                    try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                        preparedStatement.setString(1, order.getCustomerId());
                        try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                            if (resultSet.next()) {
                                String orderId = resultSet.getString("orderID");
                                order.setOrderId(orderId);
                                return true;
                            }
                        }
                    }
                }

                connection.rollback();
                return false;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error saving order: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Order order) throws SQLException {
        String sql = "UPDATE `order` SET order_status = ?, totalAmount = ? WHERE orderID = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, order.getStatus().name().toLowerCase());
                preparedStatement.setDouble(2, order.getTotalAmount());
                preparedStatement.setString(3, order.getOrderId());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // Delete existing order items and insert new ones
                    deleteOrderItems(connection, order.getOrderId());
                    if (insertOrderItems(connection, order)) {
                        connection.commit();
                        return true;
                    }
                }

                connection.rollback();
                return false;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error updating order: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while updating order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String orderId) throws SQLException {
        String sql = "DELETE FROM `order` WHERE orderID = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Delete order items first
                deleteOrderItems(connection, orderId);

                // Delete the order
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, orderId);
                    int affectedRows = preparedStatement.executeUpdate();

                    if (affectedRows > 0) {
                        connection.commit();
                        return true;
                    }

                    connection.rollback();
                    return false;
                }
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error deleting order: " + e.getMessage());
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while deleting order: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected Order mapResultSet(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setOrderId(resultSet.getString("orderID"));
        order.setCustomerId(resultSet.getString("customerID"));
        order.setOrderDate(resultSet.getTimestamp("orderDate"));
        order.setStatus(OrderStatus.valueOf(resultSet.getString("status").toUpperCase()));
        order.setTotalAmount(resultSet.getDouble("totalAmount"));
        order.setShippingAddress(convertStringToAddress(resultSet.getString("shippingAddress")));
        order.setPaymentMethod(lookupPaymentMethodById(resultSet.getString("paymentMethodID")));
        return order;
    }

    private void loadOrderItems(Connection connection, Order order) throws SQLException {
        String sql = "SELECT * FROM order_item WHERE orderID = ?";
        OrderItem[] items = new OrderItem[MAX_ITEMS];
        int index = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, order.getOrderId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next() && index < MAX_ITEMS) {
                OrderItem item = new OrderItem();
                item.setProductId(resultSet.getString("productID"));
                item.setQuantity(resultSet.getInt("quantity"));
                item.setUnitPrice(resultSet.getDouble("price"));
                items[index++] = item;
            }
        }

        order.setItems(Arrays.copyOf(items, index));
    }

    private boolean insertOrderItems(Connection connection, Order order) throws SQLException {
        String sql = "INSERT INTO order_item (orderID, productID, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (OrderItem item : order.getItems()) {
                if (item == null) continue;

                preparedStatement.setString(1, order.getOrderId());
                preparedStatement.setString(2, item.getProductId());
                preparedStatement.setInt(3, item.getQuantity());
                preparedStatement.setDouble(4, item.getUnitPrice());
                preparedStatement.addBatch();
            }

            int[] results = preparedStatement.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            return true;
        }
    }

    private void deleteOrderItems(Connection connection, String orderId) throws SQLException {
        String sql = "DELETE FROM order_item WHERE orderID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, orderId);
            preparedStatement.executeUpdate();
        }
    }

    private String formatAddressToString(Address address) {
        if (address == null) {
            return "";
        }
        return String.format("%s|%s|%s|%s|%s",
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry());
    }

    private Address convertStringToAddress(String addressString) {
        if (addressString == null || addressString.trim().isEmpty()) {
            return null;
        }

        String[] parts = addressString.split("\\|");
        if (parts.length != 5) {
            return null;
        }

        Address address = new Address();
        address.setStreet(parts[0]);
        address.setCity(parts[1]);
        address.setState(parts[2]);
        address.setZipCode(parts[3]);
        address.setCountry(parts[4]);
        return address;
    }

    private PaymentMethod lookupPaymentMethodById(String paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            return null;
        }
        return paymentMethodDao.findById(paymentMethodId.trim());
    }
}