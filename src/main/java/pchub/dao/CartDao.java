package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import pchub.model.Cart;
import pchub.model.CartItem;
import pchub.model.Product;
import pchub.utils.DatabaseConnection;

public class CartDao extends DaoTemplate<Cart> {
    @Override
    public Cart findById(String cartId) throws SQLException {
        String sql = "SELECT * FROM cart WHERE cartID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, cartId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Cart cart = mapResultSet(resultSet);
                loadCartItems(connection, cart);
                return cart;
            }
        } catch (SQLException e) {
            System.err.println("Error finding cart by ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    public Cart findByUserId(String customerId) throws SQLException {
        String sql = "SELECT * FROM cart WHERE customerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Cart cart = mapResultSet(resultSet);
                loadCartItems(connection, cart);
                return cart;
            } else {
                // No cart found, create a new one
                Cart cart = new Cart();
                cart.setCustomerId(customerId);
                if (insert(cart)) {
                    // Get the newly created cart with the cartID
                    Cart newCart = findByUserId(customerId);
                    cart.setCartId(newCart.getCartId());
                    return newCart;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding cart by user ID: " + e.getMessage());
            throw e;
        }
        return null;
    }


    @Override
    public boolean insert(Cart cart) throws SQLException {
        String sql = "INSERT INTO cart (customerID, createdDate, lastUpdated, itemCount, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                LocalDateTime now = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(now);

                preparedStatement.setString(1, cart.getCustomerId());
                preparedStatement.setTimestamp(2, timestamp);
                preparedStatement.setTimestamp(3, timestamp);
                preparedStatement.setInt(4, cart.getItemCount());
                preparedStatement.setDouble(5, cart.getSubtotal());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // Get the generated cart ID using a separate query
                    try (PreparedStatement getCartIdStmt = connection.prepareStatement(
                            "SELECT cartID FROM cart WHERE customerID = ? ORDER BY createdDate DESC LIMIT 1")) {
                        getCartIdStmt.setString(1, cart.getCustomerId());
                        ResultSet resultSet = getCartIdStmt.executeQuery();
                        if (resultSet.next()) {
                            String cartId = resultSet.getString("cartID");
                            cart.setCartId(cartId);

                            // Save cart items if there are any
                            if (cart.getItems() != null) {
                                CartItem[] cartItemArrays = cart.getItems();
                                CartItemDao cartItemDao = new CartItemDao();
                                for (CartItem cartItem : cartItemArrays) {
                                    cartItem.setCartId(cartId); // Set the cart ID for each item
                                    cartItemDao.insert(cartItem);
                                }
                            }

                            connection.commit();
                            return true;
                        }
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

    @Override
    public boolean update(Cart cart) throws SQLException {
        String sql = "UPDATE cart SET lastUpdated = ?, itemCount = ?, subtotal = ? WHERE cartID = ?";

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
                            cartItemDao.insert(cartItem);
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

    @Override
    public boolean delete(String cartId) throws SQLException {
        String sql = "DELETE FROM cart WHERE cartID = ?";

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
        return false;
    }

    @Override
    protected Cart mapResultSet(ResultSet resultSet) throws SQLException {
        String cartId = resultSet.getString("cartID");
        String customerId = resultSet.getString("customerID");
        LocalDateTime createdDate = resultSet.getTimestamp("createdDate").toLocalDateTime();
        LocalDateTime lastUpdated = resultSet.getTimestamp("lastUpdated").toLocalDateTime();
        int itemCount = resultSet.getInt("itemCount");
        double subtotal = resultSet.getDouble("subtotal");

        // Create and return the Cart object
        Cart cart = new Cart(cartId, customerId, createdDate, lastUpdated, itemCount, subtotal, new CartItem[0]);
        return cart;
    }

    private void loadCartItems(Connection connection, Cart cart) throws SQLException {
        String sql = "SELECT ci.*, c.itemCount AS itemCount, p.name AS productName FROM cart_item ci JOIN product p ON ci.productID = p.productID JOIN cart c ON ci.cartID = c.cartID WHERE c.customerID = ? AND ci.cartID = ? ORDER BY lastUpdated DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cart.getCustomerId());
            preparedStatement.setString(2, cart.getCartId());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Get count first
            if (resultSet.next()) {
                int itemCount = resultSet.getInt("itemCount");
                if (itemCount == 0) {
                    return; // No items to process
                }

                CartItem[] items = new CartItem[itemCount];
                int index = 0;
                ProductDao productDao = new ProductDao();
                CartItemDao cartItemDao = new CartItemDao();

                // Process the first row (we've already moved to it)
                do {
                    CartItem item = cartItemDao.mapResultSet(resultSet);;
                    String cartItemId = resultSet.getString("cartItemId");
                    item.setCartItemId(cartItemId);

                    items[index] = item;
                    index++;
                } while (resultSet.next() && index < itemCount);

                cart.setItems(items);
            }
        }
    }
}