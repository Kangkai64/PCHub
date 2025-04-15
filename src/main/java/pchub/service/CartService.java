package pchub.service;

import pchub.model.Product;
import pchub.model.ShoppingCart;
import pchub.model.User;

import java.sql.SQLException;

public interface CartService {
    ShoppingCart getCartForUser(User user) throws SQLException;
    boolean addItemToCart(ShoppingCart cart, Product product, int quantity) throws SQLException;
    boolean updateItemQuantity(ShoppingCart cart, String productId, int quantity) throws SQLException;
    boolean removeItemFromCart(ShoppingCart cart, String productId) throws SQLException;
    void clearCart(ShoppingCart cart) throws SQLException;
    boolean saveCart(ShoppingCart cart) throws SQLException;
}