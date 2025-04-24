package pchub.service;

import pchub.dao.CartDao;
import pchub.dao.CartItemDao;
import pchub.dao.ProductDao;
import pchub.model.CartItem;
import pchub.model.Product;
import pchub.model.ShoppingCart;
import pchub.model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Service class for managing shopping carts in the PC Hub system.
 * This class provides methods for cart operations and item management.
 */
public class CartService {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final ProductDao productDao;

    /**
     * Default constructor
     */
    public CartService() {
        this.cartDao = new CartDao();
        this.cartItemDao = new CartItemDao();
        this.productDao = new ProductDao();
    }

    /**
     * Retrieves or creates a cart for a user
     * @param user The user to get/create cart for
     * @return The user's shopping cart
     * @throws IllegalArgumentException if user is null
     * @throws SQLException if there is a database error
     */
    public ShoppingCart getCartForUser(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            ShoppingCart cart = cartDao.findByUserId(user.getUserId());
            if (cart == null) {
                cart = new ShoppingCart();
                cart.setCustomerId(user.getUserId());
                cart.setCreatedDate(LocalDateTime.now());
                String cartId = cartDao.createCart(cart);
                if (cartId != null) {
                    cart.setCartId(cartId);
                } else {
                    throw new SQLException("Failed to create new shopping cart");
                }
            }
            
            // Load cart items
            CartItem[] items = cartItemDao.getCartItems(cart.getCartId());
            cart.setItems(items);
            
            // Calculate totals
            cart.updateCartTotals();
            
            return cart;
        } catch (SQLException e) {
            throw new SQLException("Failed to get/create cart for user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a cart by its ID
     * @param cartId The ID of the cart to retrieve
     * @return The shopping cart if found, null otherwise
     * @throws IllegalArgumentException if cartId is null or empty
     * @throws SQLException if there is a database error
     */
    public ShoppingCart getCart(String cartId) throws SQLException {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new IllegalArgumentException("Cart ID cannot be null or empty");
        }

        try {
            ShoppingCart cart = cartDao.getCartById(cartId.trim());
            if (cart != null) {
                // Load cart items
                CartItem[] items = cartItemDao.getCartItems(cartId.trim());
                cart.setItems(items);

                // Calculate totals
                cart.updateCartTotals();
            }
            return cart;
        } catch (SQLException e) {
            throw new SQLException("Failed to get cart: " + e.getMessage(), e);
        }
    }

    /**
     * Adds an item to the cart
     * @param cart The cart to add the item to
     * @param product The product to add
     * @param quantity The quantity to add
     * @return true if the item was added successfully, false otherwise
     * @throws IllegalArgumentException if cart, product, or quantity is invalid
     * @throws SQLException if there is a database error
     */
    public boolean addItemToCart(ShoppingCart cart, Product product, int quantity) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (product.getCurrentQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock available");
        }

        try {
            // Check if the product is already in the cart
            CartItem existingItem = cartItemDao.getCartItemByProductId(cart.getCartId(), product.getProductID());

            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (product.getCurrentQuantity() < newQuantity) {
                    throw new IllegalStateException("Not enough stock available for the combined quantity");
                }
                return updateItemQuantity(cart, product.getProductID(), newQuantity);
            } else {
                CartItem newItem = new CartItem();
                newItem.setCartId(cart.getCartId());
                newItem.setProductId(product.getProductID());
                newItem.setProductName(product.getName());
                newItem.setUnitPrice(product.getUnitPrice());
                newItem.setQuantity(quantity);

                return cartItemDao.addCartItem(newItem) != null;
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Failed to add item to cart: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the quantity of an item in the cart
     * @param cart The cart containing the item
     * @param productId The ID of the product to update
     * @param quantity The new quantity
     * @return true if the quantity was updated successfully, false otherwise
     * @throws IllegalArgumentException if cart, productId, or quantity is invalid
     * @throws SQLException if there is a database error
     */
    public boolean updateItemQuantity(ShoppingCart cart, String productId, int quantity) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        try {
            if (quantity == 0) {
                return removeItemFromCart(cart, productId);
            }

            CartItem item = cartItemDao.getCartItemByProductId(cart.getCartId(), productId.trim());
            if (item == null) {
                return false;
            }

            Product product = productDao.findById(productId.trim());
            if (product == null || product.getCurrentQuantity() < quantity) {
                throw new IllegalStateException("Not enough stock available");
            }

            item.setQuantity(quantity);
            return cartItemDao.updateCartItem(item);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("Failed to update item quantity: " + e.getMessage(), e);
        }
    }

    /**
     * Removes an item from the cart
     * @param cart The cart containing the item
     * @param productId The ID of the product to remove
     * @return true if the item was removed successfully, false otherwise
     * @throws IllegalArgumentException if cart or productId is invalid
     * @throws SQLException if there is a database error
     */
    public boolean removeItemFromCart(ShoppingCart cart, String productId) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        try {
            CartItem item = cartItemDao.getCartItemByProductId(cart.getCartId(), productId.trim());
            if (item == null) {
                return false;
            }

            return cartItemDao.deleteCartItem(item.getCartItemId());
        } catch (Exception e) {
            throw new SQLException("Failed to remove item from cart: " + e.getMessage(), e);
        }
    }

    /**
     * Clears all items from the cart
     * @param cart The cart to clear
     * @throws IllegalArgumentException if cart is null
     * @throws SQLException if there is a database error
     */
    public void clearCart(ShoppingCart cart) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        try {
            cartItemDao.deleteAllCartItems(cart.getCartId());
            cart.setItems(new CartItem[0]);
            cart.updateCartTotals();
        } catch (Exception e) {
            throw new SQLException("Failed to clear cart: " + e.getMessage(), e);
        }
    }

    /**
     * Saves the cart to the database
     * @param cart The cart to save
     * @return true if the cart was saved successfully, false otherwise
     * @throws IllegalArgumentException if cart is null
     * @throws SQLException if there is a database error
     */
    public boolean saveCart(ShoppingCart cart) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        try {
            return cartDao.updateCart(cart);
        } catch (Exception e) {
            throw new SQLException("Failed to save cart: " + e.getMessage(), e);
        }
    }
}
