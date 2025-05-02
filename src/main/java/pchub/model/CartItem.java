package pchub.model;

public class CartItem extends LineItem {
    private String cartItemId;
    private String cartId;

    public CartItem(String cartId, Product product, int quantity, double unitPrice) {
        super(product, quantity, unitPrice);
        this.cartId = cartId;
    }

    public CartItem(){

    }

    // Getters and setters
    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public double getSubtotal() {
        return this.getUnitPrice() * this.getQuantity();
    }
}
