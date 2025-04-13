package pchub.dao;

import pchub.model.ShoppingCart;

public interface CartDao {
    ShoppingCart findByUserId(int userId);
    boolean save(ShoppingCart cart);
    boolean update(ShoppingCart cart);
    boolean delete(int cartId);
}
