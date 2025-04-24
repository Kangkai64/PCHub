package pchub.dao;

import pchub.model.CartItem;
import pchub.model.ShoppingCart;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class CartDao {
    public ShoppingCart findByUserId(String customerId) throws SQLException {
        String sql = "SELECT * FROM shoppingcart WHERE customerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ShoppingCart cart = extractCartFromResultSet(resultSet);
                loadCartItems(connection, cart);
                return cart;
            } else {
                // No cart found, create a new one
                ShoppingCart cart = new ShoppingCart();
                cart.setCustomerId(customerId);
                String cartId = createCart(cart);
                cart.setCartId(cartId);
                return cart;
            }
        } catch (SQLException e) {
            System.err.println("Error finding cart by user ID: " + e.getMessage());
            throw e;
        }
    }

    public ShoppingCart getCartById(String cartId) throws SQLException {
        String sql = "SELECT * FROM shoppingcart WHERE cartID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, cartId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ShoppingCart cart = extractCartFromResultSet(resultSet);
                loadCartItems(connection, cart);
                return cart;
            }
        } catch (SQLException e) {
            System.err.println("Error finding cart by ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    public String createCart(ShoppingCart cart) throws SQLException {
        String sql = "INSERT INTO shoppingcart (cartID, customerID, createdDate, lastUpdated, itemCount, subtotal) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Generate a unique cart ID (10 characters)
        String cartId = generateCartId();

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                LocalDateTime now = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(now);

                preparedStatement.setString(1, cartId);
                preparedStatement.setString(2, cart.getCustomerId());
                preparedStatement.setTimestamp(3, timestamp);
                preparedStatement.setTimestamp(4, timestamp);
                preparedStatement.setInt(5, cart.getItemCount());
                preparedStatement.setDouble(6, cart.getSubtotal());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    cart.setCartId(cartId);

                    // Save cart items if there are any
                    if (cart.getItems() != null) {
                        CartItem[] cartItemArrays = cart.getItems();
                        CartItemDao cartItemDao = new CartItemDao();
                        for (CartItem cartItem: cartItemArrays){
                            cartItemDao.addCartItem(cartItem);
                        }
                        connection.commit();
                        return cartId;
                    } else if (cart.getItems() == null) {
                        // No items to insertOrder
                        connection.commit();
                        return cartId;
                    }
                }

                connection.rollback();
                throw new SQLException("Failed to create cart");
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error saving cart: " + e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while saving cart: " + e.getMessage());
            throw e;
        }
    }

    public boolean updateCart(ShoppingCart cart) throws SQLException {
        String sql = "UPDATE shoppingcart SET lastUpdated = ?, itemCount = ?, subtotal = ? WHERE cartID = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

                preparedStatement.setTimestamp(1, timestamp);
                preparedStatement.setInt(2, cart.getItemCount());
                preparedStatement.setDouble(3, cart.getSubtotal());
                preparedStatement.setString(4, cart.getCartId());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // Delete existing cart items
                    CartItemDao cartItemDao = new CartItemDao();
                    cartItemDao.deleteAllCartItems(cart.getCartId());

                    // Save new cart items if there are any
                    if (cart.getItems() != null) {
                        CartItem[] cartItemArrays = cart.getItems();
                        for (CartItem cartItem: cartItemArrays){
                            cartItemDao.addCartItem(cartItem);
                        }
                        connection.commit();
                        return true;
                    } else {
                        // No items to insertOrder
                        connection.commit();
                        return true;
                    }
                }

                connection.rollback();
                return false;
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Error updating cart: " + e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while updating cart: " + e.getMessage());
            throw e;
        }
    }

    public boolean deleteCart(String cartId) throws SQLException {
        String sql = "DELETE FROM shoppingcart WHERE cartID = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                // Delete cart items first
                CartItemDao cartItemDao = new CartItemDao();
                cartItemDao.deleteAllCartItems(cartId);

                // Delete the cart
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, cartId);
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
                System.err.println("Error deleting cart: " + e.getMessage());
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error with connection while deleting cart: " + e.getMessage());
            throw e;
        }
    }

    private ShoppingCart extractCartFromResultSet(ResultSet resultSet) throws SQLException {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartId(resultSet.getString("cartID"));
        cart.setCustomerId(resultSet.getString("customerID"));
        cart.setItemCount(resultSet.getInt("itemCount"));
        cart.setSubtotal(resultSet.getDouble("subtotal"));
        cart.setCreatedDate(resultSet.getTimestamp("createdDate").toLocalDateTime());
        cart.setLastUpdated(resultSet.getTimestamp("lastUpdated").toLocalDateTime());
        return cart;
    }

    private void loadCartItems(Connection conn, ShoppingCart cart) throws SQLException {
        String sql = "SELECT * FROM cartitem WHERE cartItemID = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, cart.getCartId());
            ResultSet resultSet = preparedStatement.executeQuery();

            CartItem[] items = new CartItem[resultSet.getInt("itemCount")];
            int index = 0;
            while (resultSet.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(resultSet.getString("cartItemID"));
                item.setCartId(resultSet.getString("cartID"));
                item.setProductId(resultSet.getString("productID"));
                item.setProductName(resultSet.getString("productName"));
                item.setUnitPrice(resultSet.getDouble("price"));
                item.setQuantity(resultSet.getInt("quantity"));

                items[index] = item;
                index++;
            }
            cart.setItems(items);
        }
    }

    private String generateCartId() {
        // Generate a UUID and take first 10 characters
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }
}