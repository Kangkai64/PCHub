package pchub.dao;

import pchub.model.CartItem;
import pchub.model.Cart;
import pchub.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;

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

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                LocalDateTime now = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(now);

                preparedStatement.setString(1, cart.getCustomerId());
                preparedStatement.setTimestamp(2, timestamp);
                preparedStatement.setTimestamp(3, timestamp);
                preparedStatement.setInt(4, cart.getItemCount());
                preparedStatement.setDouble(5, cart.getSubtotal());

                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    // Get the generated cart ID
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            String cartId = generatedKeys.getString(1);
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

    @Override
    public Cart mapResultSet(ResultSet resultSet) throws SQLException {
        Cart cart = new Cart();
        cart.setCartId(resultSet.getString("cartID"));
        cart.setCustomerId(resultSet.getString("customerID"));
        cart.setItemCount(resultSet.getInt("itemCount"));
        cart.setSubtotal(resultSet.getDouble("subtotal"));
        cart.setCreatedDate(resultSet.getTimestamp("createdDate").toLocalDateTime());
        cart.setLastUpdated(resultSet.getTimestamp("lastUpdated").toLocalDateTime());
        return cart;
    }

    private void loadCartItems(Connection connection, Cart cart) throws SQLException {
        String sql = "SELECT ci.*, p.name AS productName FROM cart_item ci JOIN product p ON ci.productID = p.productID WHERE cartID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cart.getCartId());
            ResultSet resultSet = preparedStatement.executeQuery();

            CartItem[] items = new CartItem[30];
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
            if (index == 0){
                return;
            }
            cart.setItems(items);
        }
    }
}