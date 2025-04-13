package pchub.service;

import pchub.dao.*;
import pchub.dao.impl.*;
import pchub.model.*;
import pchub.utils.*;

import java.sql.SQLException;
import java.util.List;

public class ShoppingCartService {
    private CartDao cartDAO;
    private CartItemDao cartItemDAO;
    private ProductDao productDAO;

    public ShoppingCartService() {
        this.cartDAO = new CartDaoImpl();
        this.cartItemDAO = new CartItemDao();
        this.productDAO = new ProductDaoImpl();
    }

    public ShoppingCart getOrCreateCart(int userId) throws SQLException {
        // Try to get existing active cart
        ShoppingCart cart = cartDAO.getActiveCartByUserId(userId);

        // If no active cart exists, create a new one
        if (cart == null) {
            cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setCreatedDate(new java.util.Date());

            int cartId = cartDAO.createCart(cart);
            if (cartId > 0) {
                cart.setId(cartId);
            } else {
                throw new SQLException("Failed to create new shopping cart");
            }
        }

        // Load cart items
        List<CartItem> items = cartItemDAO.getCartItems(cart.getId());
        cart.setItems(items);

        // Calculate totals
        updateCartTotals(cart);

        return cart;
    }

    public ShoppingCart getCart(int cartId) throws SQLException {
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

    public boolean addToCart(int cartId, int productId, int quantity) throws SQLException {
        // Check if product exists and has enough stock
        Product product = productDAO.getProductById(productId);
        if (product == null || product.getStockQuantity() < quantity) {
            return false;
        }

        // Check if the product is already in the cart
        CartItem existingItem = cartItemDAO.getCartItemByProductId(cartId, productId);

        if (existingItem != null) {
            // Update quantity of existing item
            int newQuantity = existingItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                return false; // Not enough stock for the combined quantity
            }
            return updateCartItemQuantity(cartId, existingItem.getId(), newQuantity);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setCartId(cartId);
            newItem.setProductId(productId);
            newItem.setProductName(product.getName());
            newItem.setUnitPrice(product.getPrice());
            newItem.setQuantity(quantity);

            return cartItemDAO.addCartItem(newItem) > 0;
        }
    }

    public boolean updateCartItemQuantity(int cartId, int itemId, int quantity) throws SQLException {
        // Get the cart item
        CartItem item = cartItemDAO.getCartItemById(itemId);
        if (item == null || item.getCartId() != cartId) {
            return false;
        }

        // Check product stock
        Product product = productDAO.getProductById(item.getProductId());
        if (product == null || product.getStockQuantity() < quantity) {
            return false;
        }

        // Update quantity
        item.setQuantity(quantity);
        return cartItemDAO.updateCartItem(item);
    }

    public boolean removeFromCart(int cartId, int itemId) throws SQLException {
        // Get the cart item
        CartItem item = cartItemDAO.getCartItemById(itemId);
        if (item == null || item.getCartId() != cartId) {
            return false;
        }

        return cartItemDAO.deleteCartItem(itemId);
    }

    public boolean clearCart(int cartId) throws SQLException {
        return cartItemDAO.deleteAllCartItems(cartId);
    }

    private void updateCartTotals(ShoppingCart cart) {
        int totalItems = 0;
        double totalAmount = 0.0;

        for (CartItem item : cart.getItems()) {
            totalItems += item.getQuantity();
            totalAmount += item.getSubtotal();
        }

        cart.setTotalItems(totalItems);
        cart.setTotalAmount(totalAmount);
    }

    public boolean checkoutCart(int cartId) throws SQLException {
        return cartDAO.markCartAsInactive(cartId);
    }
}
