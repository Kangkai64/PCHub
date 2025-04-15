package pchub.dao;

import pchub.model.ShoppingCart;
import java.sql.SQLException;

public interface CartDao {
    ShoppingCart findByUserId(String customerId) throws SQLException;
    ShoppingCart getCartById(String cartId) throws SQLException;
    String createCart(ShoppingCart cart) throws SQLException;
    boolean updateCart(ShoppingCart cart) throws SQLException;
    boolean deleteCart(String cartId) throws SQLException;
}