package pchub.service.impl;

import pchub.dao.CartDao;
import pchub.dao.ProductDao;
import pchub.dao.impl.CartDaoImpl;
import pchub.dao.impl.ProductDaoImpl;
import pchub.model.*;
import pchub.service.CartService;

import java.sql.SQLException;
import java.util.Optional;

public class CartServiceImpl implements CartService {
    private CartDao cartDao;
    private ProductDao productDao;

    public CartServiceImpl() {
        this.cartDao = new CartDaoImpl();
        this.productDao = new ProductDaoImpl();
    }

    @Override
    public ShoppingCart getCartForUser(User user) throws SQLException {
        ShoppingCart cart = cartDao.findByUserId(user.getUserId());
        if (cart == null) {
            cart = new pchub.model.ShoppingCart();
            cart.setCustomerId(user.getUserId());
            cartDao.createCart(cart);
        }
        return cart;
    }

    @Override
    public boolean addItemToCart(ShoppingCart cart, pchub.model.Product product, int quantity) throws SQLException {
        if (product.getCurrentQuantity() < quantity) {
            return false; // Not enough stock
        }

        // Check if the product is already in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getProductID())).findFirst();

        if (existingItem.isPresent()) {
            pchub.model.CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getCurrentQuantity() < newQuantity) {
                return false; // Not enough stock for combined quantity
            }

            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new pchub.model.CartItem();
            newItem.setCartId(cart.getCartId());
            newItem.setProductId(product.getProductID());
            newItem.setUnitPrice(product.getUnitPrice());
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartDao.updateCart(cart);
    }

    @Override
    public boolean updateItemQuantity(ShoppingCart cart, String productId, int quantity) throws SQLException{
        if (quantity <= 0) {
            return removeItemFromCart(cart, productId);
        }

        Optional<pchub.model.CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst();

        if (itemOpt.isEmpty()) {
            return false;
        }

        pchub.model.CartItem item = itemOpt.get();
        pchub.model.Product product = productDao.findById(productId);

        if (product == null || product.getCurrentQuantity() < quantity) {
            return false;
        }

        item.setQuantity(quantity);
        return cartDao.updateCart(cart);
    }

    @Override
    public boolean removeItemFromCart(ShoppingCart cart, String productId) throws SQLException{
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            return cartDao.updateCart(cart);
        }
        return false;
    }

    @Override
    public void clearCart(ShoppingCart cart) throws SQLException {
        cart.getItems().clear();
        cartDao.updateCart(cart);
    }

    @Override
    public boolean saveCart(ShoppingCart cart) throws SQLException {
        return cartDao.updateCart(cart);
    }
}
