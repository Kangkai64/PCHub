package pchub.dao;

import pchub.model.CartItem;

import java.sql.SQLException;
import java.util.List;

public interface CartItemDao {
    List<CartItem> getCartItems(String cartId) throws SQLException;
    CartItem getCartItemById(String cartItemId) throws SQLException;
    CartItem getCartItemByProductId(String cartId, String productId) throws SQLException;
    String addCartItem(CartItem cartItem) throws SQLException;
    boolean updateCartItem(CartItem item) throws SQLException;
    boolean deleteCartItem(String cartItemId) throws SQLException;
    boolean deleteAllCartItems(String cartId) throws SQLException;
}
