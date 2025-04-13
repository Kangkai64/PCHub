package pchub.dao.impl;

import pchub.OrderDao;
import pchub.model.Order;
import pchub.model.OrderItem;
import pchub.model.enums.OrderStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDaoImpl implements OrderDao {

    @Override
    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadOrderItems(conn, order);
                return order;
            }
        } catch (SQLException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Order> findByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadOrderItems(conn, order);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding orders by user ID: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadOrderItems(conn, order);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error finding all orders: " + e.getMessage());
        }

        return orders;
    }

    @Override
    public boolean save(Order order) {
        String sql = "INSERT INTO orders (user_id, user_name, order_date, status, total_amount, " +
                "shipping_address_id, payment_method_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getUserId());
                pstmt.setString(2, order.getUserName());
                pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
                pstmt.setString(4, OrderStatus.PENDING.name());
                pstmt.setDouble(5, order.getTotalAmount());
                pstmt.setInt(6, order.getShippingAddress().getId());
                pstmt.setInt(7, order.getPaymentMethod().getId());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        order.setId(orderId);

                        // Save order items
                        if (saveOrderItems(conn, order)) {
                            conn.commit();
                            return true;
                        }
                    }
                }

                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error saving order: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while saving order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Order order) {
        String sql = "UPDATE orders SET status = ?, total_amount = ? WHERE id = ?";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, order.getStatus().name());
                pstmt.setDouble(2, order.getTotalAmount());
                pstmt.setInt(3, order.getId());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    // Delete existing order items and save new ones
                    deleteOrderItems(conn, order.getId());
                    if (saveOrderItems(conn, order)) {
                        conn.commit();
                        return true;
                    }
                }

                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error updating order: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while updating order: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete order items first
                deleteOrderItems(conn, id);

                // Delete the order
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    int affectedRows = pstmt.executeUpdate();

                    if (affectedRows > 0) {
                        conn.commit();
                        return true;
                    }

                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error deleting order: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while deleting order: " + e.getMessage());
            return false;
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setUserName(rs.getString("user_name"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setTotalAmount(rs.getDouble("total_amount"));

        // Load shipping address and payment method (you would need to implement these methods)
        // loadShippingAddress(order, rs.getInt("shipping_address_id"));
        // loadPaymentMethod(order, rs.getInt("payment_method_id"));

        return order;
    }

    private void loadOrderItems(Connection conn, Order order) throws SQLException {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setQuantity(rs.getInt("quantity"));

                order.getItems().add(item);
            }
        }
    }

    private boolean saveOrderItems(Connection conn, Order order) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (OrderItem item : order.getItems()) {
                pstmt.setInt(1, order.getId());
                pstmt.setInt(2, item.getProductId());
                pstmt.setString(3, item.getProductName());
                pstmt.setDouble(4, item.getUnitPrice());
                pstmt.setInt(5, item.getQuantity());

                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }

            return true;
        }
    }

    private void deleteOrderItems(Connection conn, int orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE order_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
        }
    }
}