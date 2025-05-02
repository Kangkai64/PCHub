package pchub.model;

public class OrderItem extends LineItem{
    private String orderItemId;
    private String orderId;

    public OrderItem(String orderId, Product product, int quantity, double unitPrice){
        super(product, quantity, unitPrice);
        this.orderId = orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getSubtotal() {
        return this.getUnitPrice() * this.getQuantity();
    }
}
