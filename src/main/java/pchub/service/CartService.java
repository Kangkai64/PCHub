package pchub.service;

import pchub.dao.CartDao;
import pchub.dao.ProductDao;
import pchub.model.CartItem;
import pchub.model.Product;
import pchub.model.ShoppingCart;
import pchub.model.User;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class CartService {
    private CartDao cartDao;
    private ProductDao productDao;

    public CartService() {
        this.cartDao = new CartDao();
        this.productDao = new ProductDao();
    }

    public ShoppingCart getCartForUser(User user) throws SQLException {
        ShoppingCart cart = cartDao.findByUserId(user.getUserId());
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setCustomerId(user.getUserId());
            cartDao.createCart(cart);
        }
        return cart;
    }

    public boolean addItemToCart(ShoppingCart cart, Product product, int quantity) throws SQLException {
        if (product.getCurrentQuantity() < quantity) {
            return false; // Not enough stock
        }

        // Check if the product is already in the cart
        Optional<CartItem> existingItem = Arrays.stream(cart.getItems())
                .filter(item -> item.getProductId().equals(product.getProductID())).findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getCurrentQuantity() < newQuantity) {
                return false; // Not enough stock for combined quantity
            }

            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getCartId());
            newItem.setProductId(product.getProductID());
            newItem.setUnitPrice(product.getUnitPrice());
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartDao.updateCart(cart);
    }

    public boolean updateItemQuantity(ShoppingCart cart, String productId, int quantity) throws SQLException{
        if (quantity <= 0) {
            return removeItemFromCart(cart, productId);
        }

        Optional<CartItem> itemOpt = Arrays.stream(cart.getItems())
                .filter(item -> item.getProductId().equals(productId)).findFirst();


        if (itemOpt.isEmpty()) {
            return false;
        }

        CartItem item = itemOpt.get();
        pchub.model.Product product = productDao.findById(productId);

        if (product == null || product.getCurrentQuantity() < quantity) {
            return false;
        }

        item.setQuantity(quantity);
        return cartDao.updateCart(cart);
    }

    public boolean removeItemFromCart(ShoppingCart cart, String productId) throws SQLException{
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            return cartDao.updateCart(cart);
        }
        return false;
    }

    public void clearCart(ShoppingCart cart) throws SQLException {
        cart.getItems();
        for (CartItem item : cart.getItems()) {
            cart = null;
        }
        cartDao.updateCart(cart);
    }

    public boolean saveCart(ShoppingCart cart) throws SQLException {
        return cartDao.updateCart(cart);
    }
}
