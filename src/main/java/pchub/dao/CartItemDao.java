package pchub.dao;

import pchub.model.CartItem;
import pchub.model.Product;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartItemDao extends DaoTemplate<CartItem> {

    public CartItem[] getCartItems(String cartId) throws SQLException {
        String sql = "SELECT ci.*, p.name AS productName FROM cart_item ci JOIN product p ON ci.productID = p.productID WHERE cartID = ?";
        CartItem[] items = new CartItem[30];

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, String.valueOf(cartId));
            ResultSet resultSet = preparedStatement.executeQuery();
            int index = 0;

            while (resultSet.next()) {
                CartItem item = mapResultSet(resultSet);
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
        String sql = "SELECT ci.*, p.name AS productName FROM cart_item ci JOIN product p ON ci.productID = p.productID WHERE cartItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, cartItemId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving cart item: " + e.getMessage());
        }

        return null;
    }

    public CartItem findByProductId(String cartId, String productId) throws SQLException {
        String sql = "SELECT ci.*, p.name AS productName FROM cart_item ci JOIN product p ON ci.productID = p.productID WHERE cartID = ? AND ci.productID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, String.valueOf(cartId));
            preparedStatement.setString(2, String.valueOf(productId));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving cart item by product ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean insert(CartItem item) throws SQLException {
        String sql = "INSERT INTO cart_item (cartID, productID, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, item.getCartId());
            preparedStatement.setString(2, item.getProduct().getProductID());
            preparedStatement.setInt(3, item.getQuantity());
            preparedStatement.setDouble(4, item.getUnitPrice());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the last inserted cart item ID using a separate query
                String getLastIdSql = "SELECT cartItemID FROM cart_item WHERE cartID = ? AND productID = ? ORDER BY cartItemID DESC LIMIT 1";
                try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                    getLastIdStmt.setString(1, item.getCartId());
                    getLastIdStmt.setString(2, item.getProduct().getProductID());

                    try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                        if (resultSet.next()) {
                            String cartItemId = resultSet.getString("cartItemID");
                            item.setCartItemId(cartItemId);
                        }
                    }
                }
                // Update the cart item count in the cart table
                String updateCartItemCount = "SELECT COUNT(*) FROM cart_item WHERE cartID = ?";
                try (PreparedStatement updateCartItemCountStmt = connection.prepareStatement(updateCartItemCount)) {
                    updateCartItemCountStmt.setString(1, item.getCartId());

                    try (ResultSet resultSet = updateCartItemCountStmt.executeQuery()) {
                        if (resultSet.next()) {
                            return true;
                        }
                    }
                }
            }

            throw new SQLException("Failed to add cart item");
        } catch (SQLException e) {
            throw new SQLException("Error adding cart item: " + e.getMessage());
        }
    }

    @Override
    public boolean update(CartItem item) throws SQLException {
        String sql = "UPDATE cart_item SET quantity = ?, price = ? WHERE cartItemID = ?";

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
        String sql = "DELETE FROM cart_item WHERE cartItemID = ?";

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
        String sql = "DELETE FROM cart_item WHERE cartID = ?";

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
    protected CartItem mapResultSet(ResultSet resultSet) throws SQLException {
        ProductDao productDao = new ProductDao();

        String productId = resultSet.getString("productID");
        Product product = productDao.findById(productId);

        String cartItemId = resultSet.getString("cartItemId");
        String cartId = resultSet.getString("cartID");
        int quantity = resultSet.getInt("quantity");
        double price = resultSet.getDouble("price");

        CartItem item = new CartItem(cartId, product, quantity, price);
        item.setCartItemId(cartItemId);

        return item;
    }
}