package pchub.dao;

import pchub.model.CartItem;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CartItemDao extends DaoTemplate<CartItem> {

    public CartItem[] getCartItems(String cartId) throws SQLException {
        String sql = "SELECT * FROM cartitem WHERE cartID = ?";
        CartItem[] items = new CartItem[30];

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, String.valueOf(cartId));
            ResultSet resultSet = preparedStatement.executeQuery();
            int index = 0;

            while (resultSet.next()) {
                CartItem item = new CartItem();
                item = mapResultSet(resultSet);
                // Note: subtotal is computed by the database as a generated column

                items[index] = item;
                index++;
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving cart items: " + e.getMessage());
        }

        return items;
    }

    @Override
    public CartItem findById(String cartItemId) throws SQLException {
        String sql = "SELECT * FROM cartitem WHERE cartItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, cartItemId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                CartItem item = new CartItem();
                item = mapResultSet(resultSet);
                return item;
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving cart item: " + e.getMessage());
        }

        return null;
    }

    public CartItem findByProductId(String cartId, String productId) throws SQLException {
        String sql = "SELECT * FROM cartitem WHERE cartID = ? AND productID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, String.valueOf(cartId));
            preparedStatement.setString(2, String.valueOf(productId));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                CartItem item = new CartItem();
                item = mapResultSet(resultSet);
                return item;
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving cart item by product ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean insert(CartItem item) throws SQLException {
        String sql = "INSERT INTO cartitem (cartItemID, cartID, productID, quantity, unitPrice) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Generate a simple ID (in a real app, you might want a more robust ID generation)
            String cartItemId = generateCartItemId();

            preparedStatement.setString(1, cartItemId);
            preparedStatement.setString(2, item.getCartId());
            preparedStatement.setString(3, item.getProductId());
            preparedStatement.setInt(4, item.getQuantity());
            preparedStatement.setDouble(5, item.getUnitPrice());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new SQLException("Error adding cart item: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean update(CartItem item) throws SQLException {
        String sql = "UPDATE cartitem SET quantity = ?, unitPrice = ? WHERE cartItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, item.getQuantity());
            preparedStatement.setDouble(2, item.getUnitPrice());
            preparedStatement.setString(3, item.getCartItemId());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error updating cart item: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String cartItemId) throws SQLException {
        String sql = "DELETE FROM cartitem WHERE cartItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, cartItemId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error deleting cart item: " + e.getMessage());
        }
    }
    
    public boolean deleteAllCartItems(String cartId) throws SQLException {
        String sql = "DELETE FROM cartitem WHERE cartID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, cartId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows >= 0; // Returns true even if no items were deleted
        } catch (SQLException e) {
            throw new SQLException("Error deleting all cart items: " + e.getMessage());
        }
    }

    @Override
    public CartItem mapResultSet(ResultSet resultSet) throws SQLException {
        CartItem item = new CartItem();
        item.setCartItemId(resultSet.getString("cartItemID"));
        item.setCartId(resultSet.getString("cartID"));
        item.setProductId(resultSet.getString("productID"));
        item.setQuantity(resultSet.getInt("quantity"));
        item.setUnitPrice(resultSet.getDouble("price"));
        return item;
    }

    // Helper method to generate a simple cart item ID
    private String generateCartItemId() {
        return String.valueOf(System.currentTimeMillis() % 10000000);
    }
}