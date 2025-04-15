package pchub.service.impl;

import pchub.dao.*;
import pchub.dao.impl.*;
import pchub.model.*;
import pchub.service.ShoppingCartService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ShoppingCartServiceImpl implements ShoppingCartService {
    private CartDao cartDAO;
    private CartItemDao cartItemDAO;
    private ProductDao productDAO;

    public ShoppingCartServiceImpl() {
        this.cartDAO = new CartDaoImpl();
        this.cartItemDAO = new CartItemDaoImpl();
        this.productDAO = new ProductDaoImpl();
    }

    @Override
    public ShoppingCart getOrCreateCart(String userId) throws SQLException {
        // Try to get existing active cart
        ShoppingCart cart = cartDAO.findByUserId(userId);

        // If no active cart exists, create a new one
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setCustomerId(userId);
            cart.setCreatedDate(LocalDateTime.now());

            String cartId = cartDAO.createCart(cart);
            if (cartId != null) {
                cart.setCartId(cartId);
            } else {
                throw new SQLException("Failed to create new shopping cart");
            }
        }

        // Load cart items
        List<CartItem> items = cartItemDAO.getCartItems(cart.getCartId());
        cart.setItems(items);

        // Calculate totals
        updateCartTotals(cart);

        return cart;
    }

    @Override
    public ShoppingCart getCart(String cartId) throws SQLException {
        ShoppingCart cart = cartDAO.getCartById(cartId);
        if (cart != null) {
            // Load cart items
            List<CartItem> items = cartItemDAO.getCartItems(cartId);
            cart.setItems(items);

            // Calculate totals
            updateCartTotals(cart);
        }
        return cart;
    }

    @Override
    public boolean addToCart(String cartId, String productId, int quantity) throws SQLException {
        // Check if product exists and has enough stock
        Product product = productDAO.findById(productId);
        if (product == null || product.getCurrentQuantity() < quantity) {
            return false;
        }

        // Check if the product is already in the cart
        CartItem existingItem = cartItemDAO.getCartItemByProductId(cartId, productId);

        if (existingItem != null) {
            // Update quantity of existing item
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getCurrentQuantity() < newQuantity) {
                return false; // Not enough stock for the combined quantity
            }
            return updateCartItemQuantity(cartId, existingItem.getCartItemId(), newQuantity);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setCartId(cartId);
            newItem.setProductId(productId);
            newItem.setUnitPrice(product.getUnitPrice());
            newItem.setQuantity(quantity);

            return cartItemDAO.addCartItem(newItem) > 0;
        }
    }

    @Override
    public boolean updateCartItemQuantity(String cartId, String cartItemId, int quantity) throws SQLException {
        // Get the cart item
        CartItem item = cartItemDAO.getCartItemById(cartItemId);
        if (item == null || !item.getCartId().equals(cartId)) {
            return false;
        }

        // Check product stock
        Product product = productDAO.findById(item.getProductId());
        if (product == null || product.getCurrentQuantity() < quantity) {
            return false;
        }

        // Update quantity
        item.setQuantity(quantity);
        return cartItemDAO.updateCartItem(item);
    }

    @Override
    public boolean removeFromCart(String cartId, String cartItemId) throws SQLException {
        // Get the cart item
        CartItem item = cartItemDAO.getCartItemById(cartItemId);
        if (item == null || !item.getCartId().equals(cartId)) {
            return false;
        }

        return cartItemDAO.deleteCartItem(cartItemId);
    }

    @Override
    public boolean clearCart(String cartId) throws SQLException {
        return cartItemDAO.deleteAllCartItems(cartId);
    }

    @Override
    public boolean checkoutCart(String cartId) throws SQLException {
        // return cartDAO.markCartAsInactive(cartId); Change to order
        return false;
    }
}
