package pchub.dao;

import pchub.model.OrderItem;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderItemDao extends DaoTemplate<OrderItem> {

    public OrderItem[] getOrderItems(String orderId) throws SQLException {
        String sql = "SELECT oi.*, p.name AS productName FROM order_item oi JOIN product p ON oi.productID = p.productID WHERE orderID = ?";
        OrderItem[] items = new OrderItem[30];

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, String.valueOf(orderId));
            ResultSet resultSet = preparedStatement.executeQuery();
            int index = 0;

            while (resultSet.next()) {
                OrderItem item = mapResultSet(resultSet);
                items[index] = item;
                index++;
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving order items: " + e.getMessage());
        }

        return items;
    }

    @Override
    public OrderItem findById(String orderItemId) throws SQLException {
        String sql = "SELECT oi.*, p.name AS productName FROM order_item oi JOIN product p ON oi.productID = p.productID WHERE orderItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, orderItemId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving order item: " + e.getMessage());
        }

        return null;
    }

    public OrderItem findByProductId(String orderId, String productId) throws SQLException {
        String sql = "SELECT oi.*, p.name AS productName FROM order_item oi JOIN product p ON oi.productID = p.productID WHERE orderID = ? AND oi.productID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, String.valueOf(orderId));
            preparedStatement.setString(2, String.valueOf(productId));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving order item by product ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean insert(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_item (orderID, productID, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, item.getOrderId());
            preparedStatement.setString(2, item.getProductId());
            preparedStatement.setInt(3, item.getQuantity());
            preparedStatement.setDouble(4, item.getUnitPrice());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the last inserted order item ID using a separate query
                String getLastIdSql = "SELECT orderItemID FROM order_item WHERE orderID = ? AND productID = ? ORDER BY orderItemID DESC LIMIT 1";
                try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                    getLastIdStmt.setString(1, item.getOrderId());
                    getLastIdStmt.setString(2, item.getProductId());

                    try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                        if (resultSet.next()) {
                            String orderItemId = resultSet.getString("orderItemID");
                            item.setOrderItemId(orderItemId);
                            return true;
                        }
                    }
                }
            }

            throw new SQLException("Failed to add order item");
        } catch (SQLException e) {
            throw new SQLException("Error adding order item: " + e.getMessage());
        }
    }

    /**
     * Inserts multiple order items in a batch operation
     * @param items Array of order items to insert
     * @return true if all items were inserted successfully, false otherwise
     * @throws SQLException if there's a database error
     */
    public boolean insertBatch(OrderItem[] items) throws SQLException {
        if (items == null || items.length == 0) {
            return true; // Nothing to insert
        }

        String sql = "INSERT INTO order_item (orderID, productID, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (OrderItem item : items) {
                    if (item != null) {
                        preparedStatement.setString(1, item.getOrderId());
                        preparedStatement.setString(2, item.getProductId());
                        preparedStatement.setInt(3, item.getQuantity());
                        preparedStatement.setDouble(4, item.getUnitPrice());
                        preparedStatement.addBatch();
                    }
                }

                int[] results = preparedStatement.executeBatch();
                connection.commit();

                // Update each item with its generated ID
                for (OrderItem item : items) {
                    if (item != null) {
                        String getLastIdSql = "SELECT orderItemID FROM order_item WHERE orderID = ? AND productID = ? ORDER BY orderItemID DESC LIMIT 1";
                        try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                            getLastIdStmt.setString(1, item.getOrderId());
                            getLastIdStmt.setString(2, item.getProductId());

                            try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                                if (resultSet.next()) {
                                    String orderItemId = resultSet.getString("orderItemID");
                                    item.setOrderItemId(orderItemId);
                                }
                            }
                        }
                    }
                }

                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Error batch inserting order items: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean update(OrderItem item) throws SQLException {
        String sql = "UPDATE order_item SET quantity = ?, price = ? WHERE orderItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, item.getQuantity());
            preparedStatement.setDouble(2, item.getUnitPrice());
            preparedStatement.setString(3, item.getOrderItemId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error updating order item: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String orderItemId) throws SQLException {
        String sql = "DELETE FROM order_item WHERE orderItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, orderItemId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error deleting order item: " + e.getMessage());
        }
    }

    @Override
    protected OrderItem mapResultSet(ResultSet resultSet) throws SQLException {
        OrderItem item = new OrderItem();
        item.setOrderItemId(resultSet.getString("orderItemID"));
        item.setOrderId(resultSet.getString("orderID"));
        item.setProductId(resultSet.getString("productID"));
        item.setProductName(resultSet.getString("productName"));
        item.setQuantity(resultSet.getInt("quantity"));
        item.setUnitPrice(resultSet.getDouble("price"));
        return item;
    }

    /**
     * Deletes all order items for a given order
     * @param orderId The ID of the order whose items should be deleted
     * @return true if successful, false otherwise
     * @throws SQLException if there's a database error
     */
    public boolean deleteAllOrderItems(String orderId) throws SQLException {
        String sql = "DELETE FROM order_item WHERE orderID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, orderId);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows >= 0; // Return true even if no rows were deleted (0 is valid)
        } catch (SQLException e) {
            throw new SQLException("Error deleting all order items: " + e.getMessage());
        }
    }
}