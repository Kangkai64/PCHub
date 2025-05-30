package pchub.model;

import java.sql.SQLException;
import java.time.LocalDateTime;

import pchub.dao.CartDao;
import pchub.dao.CartItemDao;
import pchub.dao.ProductDao;

public class Cart {
    private String cartId;
    private String customerId;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;
    private int itemCount;
    private double subtotal;
    private CartItem[] items;

    private static final CartDao CART_DAO = new CartDao();
    private static final CartItemDao CART_ITEM_DAO = new CartItemDao();
    private static final ProductDao PRODUCT_DAO = new ProductDao();

    // Default constructor
    public Cart() {
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.itemCount = 0;
        this.subtotal = 0.0;
        this.items = new CartItem[0];
    }

    // Constructor with customer ID
    public Cart(String customerId) {
        this();
        this.customerId = customerId;
    }

    // Full parameterized constructor
    public Cart(String cartId, String customerId, LocalDateTime createdDate, LocalDateTime lastUpdated,
                int itemCount, double subtotal, CartItem[] items) {
        this.cartId = cartId;
        this.customerId = customerId;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
        this.itemCount = itemCount;
        this.subtotal = subtotal;
        this.items = items;
    }

    // Getters and setters
    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public CartItem[] getItems() {
        return items;
    }

    public void setItems(CartItem[] items) throws SQLException {
        this.items = items;
        updateCartTotals();
        if (items != null && items.length > 0 && items[items.length - 1] != null) {
            CART_ITEM_DAO.insert(items[items.length - 1]);
        }
        itemCount = items != null ? items.length : 0;
    }

    @Override
    public String toString() {
        return "Cart ID: " + cartId + ", Customer ID: " + customerId +
                ", Created Date: " + createdDate + ", Last Updated: " + lastUpdated +
                ", Item Count: " + itemCount + ", Subtotal: " + subtotal;
    }

    /**
     * Updates the cart totals based on the items
     */
    public void updateCartTotals() {
        subtotal = 0.0;
        itemCount = 0;
        for (CartItem item : items) {
            if (item != null) {
                subtotal += item.getUnitPrice() * item.getQuantity();
                itemCount++;
            }
        }
    }

    /**
     * Retrieves or creates a cart for a user
     * @param user The user to get/create cart for
     * @return The user's shopping cart
     * @throws IllegalArgumentException if user is null
     * @throws SQLException if there is a database error
     */
    public static Cart getCartForUser(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            Cart cart = CART_DAO.findByUserId(user.getUserId());
            if (cart == null) {
                // Create a new cart
                cart = new Cart();
                cart.setCustomerId(user.getUserId());
                cart.setCreatedDate(LocalDateTime.now());
                boolean cartFlag = CART_DAO.insert(cart);
                if (cartFlag) {
                    Cart newCart = CART_DAO.findByUserId(user.getUserId());
                    cart.setCartId(newCart.getCartId());
                } else {
                    throw new SQLException("Failed to create new shopping cart");
                }
            }

            // Load cart items
            CartItem[] items = CART_ITEM_DAO.getCartItems(cart.getCartId());
            cart.setItems(items);
            cart.updateCartTotals();

            return cart;
        } catch (SQLException e) {
            throw new SQLException("Failed to get / create cart for user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a cart by its ID
     * @param cartId The ID of the cart to retrieve
     * @return The shopping cart if found, null otherwise
     * @throws IllegalArgumentException if cartId is null or empty
     * @throws SQLException if there is a database error
     */
    public static Cart getCart(String cartId) throws SQLException {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new IllegalArgumentException("Cart ID cannot be null or empty");
        }

        try {
            Cart cart = CART_DAO.findById(cartId.trim());
            if (cart != null) {
                // Load cart items
                CartItem[] items = CART_ITEM_DAO.getCartItems(cartId.trim());
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
    public static boolean addItemToCart(Cart cart, Product product, int quantity) throws SQLException {
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
            CartItem existingItem = CART_ITEM_DAO.findByProductId(cart.getCartId(), product.getProductID());

            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (product.getCurrentQuantity() < newQuantity) {
                    throw new IllegalStateException("Not enough stock available for the combined quantity");
                }
                return updateItemQuantity(cart, product.getProductID(), newQuantity);
            } else {
                CartItem newItem = new CartItem(cart.getCartId(), product, quantity, product.getUnitPrice());
                return CART_ITEM_DAO.insert(newItem);
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
    public static boolean updateItemQuantity(Cart cart, String productId, int quantity) throws SQLException {
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

            CartItem item = CART_ITEM_DAO.findByProductId(cart.getCartId(), productId.trim());
            if (item == null) {
                return false;
            }

            Product product = PRODUCT_DAO.findById(productId.trim());
            if (product == null || product.getCurrentQuantity() < quantity) {
                throw new IllegalStateException("Not enough stock available");
            }

            item.setQuantity(quantity);
            return CART_ITEM_DAO.update(item);
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
    public static boolean removeItemFromCart(Cart cart, String productId) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        try {
            CartItem item = CART_ITEM_DAO.findByProductId(cart.getCartId(), productId.trim());
            if (item == null) {
                return false;
            }

            return CART_ITEM_DAO.delete(item.getCartItemId());
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
    public static void clearCart(Cart cart) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        try {
            CART_ITEM_DAO.deleteAllCartItems(cart.getCartId());
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
    public static boolean saveCart(Cart cart) throws SQLException {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        try {
            return CART_DAO.update(cart);
        } catch (Exception e) {
            throw new SQLException("Failed to save cart: " + e.getMessage(), e);
        }
    }
}