package pchub.service;

import pchub.model.ShoppingCart;

import java.sql.SQLException;

public interface ShoppingCartService {
    ShoppingCart getOrCreateCart(String userId) throws SQLException;
    ShoppingCart getCart(String cartId) throws SQLException;
    boolean addToCart(String cartId, String productId, int quantity) throws SQLException;
    boolean updateCartItemQuantity(String cartId, String cartItemId, int quantity) throws SQLException;
    boolean removeFromCart(String cartId, String cartItemId) throws SQLException;
    boolean clearCart(String cartId) throws SQLException;
    boolean checkoutCart(String cartId) throws SQLException;
}
