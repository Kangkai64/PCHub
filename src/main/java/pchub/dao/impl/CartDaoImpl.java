package pchub.dao.impl;

import pchub.dao.CartDao;
import pchub.model.CartItem;
import pchub.model.ShoppingCart;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CartDaoImpl implements CartDao {

    @Override
    public ShoppingCart findByUserId(int userId) {
        String sql = "SELECT * FROM carts WHERE user_id = ?";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ShoppingCart cart = new ShoppingCart();
                cart.setId(rs.getInt("id"));
                cart.setUserId(rs.getInt("user_id"));

                loadCartItems(conn, cart);
                return cart;
            } else {
                // No cart found, create a new one
                ShoppingCart cart = new ShoppingCart();
                cart.setUserId(userId);
                cart.setItems(new ArrayList<>());
                save(cart);
                return cart;
            }
        } catch (SQLException e) {
            System.err.println("Error finding cart by user ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean save(ShoppingCart cart) {
        String sql = "INSERT INTO carts (user_id) VALUES (?)";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, cart.getUserId());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int cartId = generatedKeys.getInt(1);
                        cart.setId(cartId);

                        // Save cart items
                        if (saveCartItems(conn, cart)) {
                            conn.commit();
                            return true;
                        }
                    }
                }

                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error saving cart: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while saving cart: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(ShoppingCart cart) {
        try (Connection conn = pchub.utils.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete existing cart items
                deleteCartItems(conn, cart.getId());

                // Save new cart items
                if (saveCartItems(conn, cart)) {
                    conn.commit();
                    return true;
                }

                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error updating cart: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while updating cart: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int cartId) {
        String sql = "DELETE FROM carts WHERE id = ?";

        try (Connection conn = pchub.utils.DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Delete cart items first
                deleteCartItems(conn, cartId);

                // Delete the cart
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, cartId);
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
                System.err.println("Error deleting cart: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while deleting cart: " + e.getMessage());
            return false;
        }
    }

    private void loadCartItems(Connection conn, ShoppingCart cart) throws SQLException {
        String sql = "SELECT * FROM cart_items WHERE cart_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cart.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem();
                item.setId(rs.getInt("id"));
                item.setCartId(rs.getInt("cart_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setQuantity(rs.getInt("quantity"));

                cart.getItems().add(item);
            }
        }
    }

    private boolean saveCartItems(Connection conn, ShoppingCart cart) throws SQLException {
        if (cart.getItems().isEmpty()) {
            return true; // No items to save
        }

        String sql = "INSERT INTO cart_items (cart_id, product_id, product_name, unit_price, quantity) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CartItem item : cart.getItems()) {
                pstmt.setInt(1, cart.getId());
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

    private void deleteCartItems(Connection conn, int cartId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            pstmt.executeUpdate();
        }
    }
}