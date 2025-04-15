package pchub.model;

import java.time.LocalDateTime;

public class ShoppingCart {
    private String cartId;
    private String customerId;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;
    private int itemCount = 0;
    private double subtotal = 0.0;
    private CartItem[] items;

    public ShoppingCart() {
        this.items = new CartItem[20];
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
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

    public void setItems(CartItem[] items) {
        this.items = items;
        updateCartTotals();
    }

    public void updateCartTotals() {
        int count = 0;
        double total = 0.0;

        for (CartItem item : items) {
            count += item.getQuantity();
            total += item.getSubtotal();
        }

        this.itemCount = count;
        this.subtotal = total;
    }
}