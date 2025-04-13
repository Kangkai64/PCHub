package pchub.service;

import pchub.model.CartItem;
import pchub.model.Product;
import pchub.model.ShoppingCart;
import pchub.model.User;
import pchub.model.Product;
import pchub.model.ShoppingCart;
import pchub.model.User;

public interface CartService {
    pchub.model.ShoppingCart getCartForUser(pchub.model.User user);
    boolean addItemToCart(pchub.model.ShoppingCart cart, pchub.model.Product product, int quantity);
    boolean updateItemQuantity(pchub.model.ShoppingCart cart, int productId, int quantity);
    boolean removeItemFromCart(pchub.model.ShoppingCart cart, int productId);
    void clearCart(pchub.model.ShoppingCart cart);
    boolean saveCart(pchub.model.ShoppingCart cart);
}