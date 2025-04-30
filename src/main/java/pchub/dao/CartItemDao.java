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
        String sql = "SELECT * FROM cart_item WHERE cartID = ?";
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
        String sql = "SELECT * FROM cart_item WHERE cartItemID = ?";

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
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, item.getCartId());
            preparedStatement.setString(2, item.getProductId());
            preparedStatement.setInt(3, item.getQuantity());
            preparedStatement.setDouble(4, item.getUnitPrice());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the generated cart item ID
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String cartItemId = generatedKeys.getString(1);
                        item.setCartItemId(cartItemId);
                        return true;
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
    public CartItem mapResultSet(ResultSet resultSet) throws SQLException {
        CartItem item = new CartItem();
        item.setCartItemId(resultSet.getString("cartItemID"));
        item.setCartId(resultSet.getString("cartID"));
        item.setProductId(resultSet.getString("productID"));
        item.setQuantity(resultSet.getInt("quantity"));
        item.setUnitPrice(resultSet.getDouble("price"));
        return item;
    }
}