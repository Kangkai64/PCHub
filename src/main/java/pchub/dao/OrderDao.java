package pchub.dao;

import pchub.model.*;
import pchub.model.enums.OrderStatus;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class OrderDao extends DaoTemplate<Order> {
    private static final int MAX_ITEMS = 30;
    private final PaymentMethodDao paymentMethodDao;
    private final AddressDao addressDao;

    public OrderDao() {
        this.paymentMethodDao = new PaymentMethodDao();
        this.addressDao = new AddressDao();
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
        String sql = "INSERT INTO `order` (customerID, orderDate, orderStatus, totalAmount, " +
                "shipping_addressID, payment_MethodID) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Start transaction
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, order.getCustomer().getUserId());
                preparedStatement.setTimestamp(2, new Timestamp(order.getOrderDate().getTime()));
                preparedStatement.setString(3, order.getStatus().name().toUpperCase());
                preparedStatement.setDouble(4, order.getTotalAmount());
                preparedStatement.setString(5, order.getShippingAddress().getAddressId()); // Assuming Address has getAddressId()
                preparedStatement.setString(6, order.getPaymentMethod().getPaymentMethodId());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // Get the generated order ID using the same connection
                    String orderId = getLastInsertId(connection, order.getCustomer().getUserId());
                    if (orderId != null) {
                        order.setOrderId(orderId);
                        // Commit the transaction
                        connection.commit();
                        return true;
                    }
                }

                // If we reached here, something went wrong
                connection.rollback();
                return false;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error saving order: " + e.getMessage());
                throw e; // Re-throw to handle in the service layer
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e; // Re-throw to handle in the service layer
        }
    }

    private String getLastInsertId(Connection connection, String customerId) throws SQLException {
        String getLastIdSql = "SELECT orderID FROM `order` WHERE customerID = ? ORDER BY orderID DESC LIMIT 1";
        try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
            getLastIdStmt.setString(1, customerId);
            ResultSet resultSet = getLastIdStmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("orderID");
            }
        }
        return null;
    }

    @Override
    public boolean update(Order order) throws SQLException {
        String sql = "UPDATE `order` SET orderStatus = ?, totalAmount = ? WHERE orderID = ?";

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
        UserDao userDao = new UserDao();

        String orderId = resultSet.getString("orderID");
        String customerId = resultSet.getString("customerID");
        Customer customer = (Customer) userDao.findById(customerId);

        Date orderDate = resultSet.getTimestamp("orderDate");
        OrderStatus status = OrderStatus.valueOf(resultSet.getString("orderStatus").toUpperCase());
        double totalAmount = resultSet.getDouble("totalAmount");

        // Get Address and PaymentMethod objects
        String addressId = resultSet.getString("shipping_addressID");
        String paymentMethodId = resultSet.getString("payment_MethodID");
        Address shippingAddress = addressDao.findById(addressId);
        PaymentMethod paymentMethod = paymentMethodDao.findById(paymentMethodId);

        // Create and return the Order object
        Order order = new Order(orderId, customer, orderDate, status, totalAmount,
                new OrderItem[0], shippingAddress, paymentMethod);
        return order;
    }

    private void loadOrderItems(Connection connection, Order order) throws SQLException {
        // First, count the number of items for this order
        String countSql = "SELECT COUNT(*) AS itemCount FROM order_item WHERE orderID = ?";
        int itemCount = 0;

        try (PreparedStatement countStmt = connection.prepareStatement(countSql)) {
            countStmt.setString(1, order.getOrderId());
            ResultSet countRs = countStmt.executeQuery();

            if (countRs.next()) {
                itemCount = countRs.getInt("itemCount");
                if (itemCount == 0) {
                    return; // No items to process
                }
            }
        }

        // Now fetch the actual items
        String sql = "SELECT oi.*, p.name AS productName FROM order_item oi JOIN product p ON oi.productID = p.productID WHERE oi.orderID = ? ORDER BY oi.orderItemID";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, order.getOrderId());
            ResultSet resultSet = preparedStatement.executeQuery();

            OrderItem[] items = new OrderItem[itemCount];
            int index = 0;

            while (resultSet.next() && index < itemCount) {
                ProductDao productDao = new ProductDao();

                String productId = resultSet.getString("productID");
                Product product = productDao.findById(productId);

                String orderItemId = resultSet.getString("orderItemID");
                String orderId = resultSet.getString("orderID");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                OrderItem item = new OrderItem(orderId, product, quantity, price);
                item.setOrderItemId(orderItemId);

                items[index] = item;
                index++;
            }

            order.setItems(items);
        }
    }

    private boolean insertOrderItems(Connection connection, Order order) throws SQLException {
        String sql = "INSERT INTO order_item (orderID, productID, quantity, price) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (OrderItem item : order.getItems()) {
                if (item == null) continue;

                preparedStatement.setString(1, order.getOrderId());
                preparedStatement.setString(2, item.getProduct().getProductID());
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
        try {
            AddressDao addressDao = new AddressDao();
            return addressDao.findById(addressString.trim());
        } catch (SQLException e) {
            System.err.println("Error loading shipping address: " + e.getMessage());
            return null;
        }
    }

    private PaymentMethod lookupPaymentMethodById(String paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            return null;
        }
        return paymentMethodDao.findById(paymentMethodId.trim());
    }
}