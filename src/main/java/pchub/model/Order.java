package pchub.model;

import pchub.model.enums.OrderStatus;

import java.util.Date;
import java.util.Objects;

/**
 * Represents an order in the PC Hub system.
 * This class contains information about an order including its ID, customer details,
 * order date, status, total amount, items, shipping address, and payment method.
 */
public class Order {
    private String orderId;
    private String customerId;
    private String customerName;
    private Date orderDate;
    private OrderStatus status;
    private double totalAmount;
    private OrderItem[] items;
    private Address shippingAddress;  // Kept as Address object
    private PaymentMethod paymentMethod;  // Kept as PaymentMethod object

    private static final int MAX_ITEMS = 30;

    /**
     * Default constructor
     */
    public Order() {
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
        this.items = new OrderItem[MAX_ITEMS];
    }

    /**
     * Parameterized constructor
     * @param orderId The unique identifier for the order
     * @param customerId The ID of the customer who placed the order
     * @param userName The username of the customer
     * @param shippingAddress The shipping address for the order
     * @param paymentMethod The payment method for the order
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Order(String orderId, String customerId, String userName, 
                Address shippingAddress, PaymentMethod paymentMethod) {
        setOrderId(orderId);
        setCustomerId(customerId);
        setCustomerName(userName);
        setShippingAddress(shippingAddress);
        setPaymentMethod(paymentMethod);
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
        this.items = new OrderItem[MAX_ITEMS];
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        this.orderId = orderId.trim();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        this.customerId = customerId.trim();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.customerName = customerName.trim();
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        if (orderDate == null) {
            throw new IllegalArgumentException("Order date cannot be null");
        }
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        this.totalAmount = totalAmount;
    }

    public OrderItem[] getItems() {
        return items;
    }

    public void setItems(OrderItem[] items) {
        if (items == null) {
            throw new IllegalArgumentException("Items array cannot be null");
        }
        if (items.length > MAX_ITEMS) {
            throw new IllegalArgumentException("Items array cannot exceed " + MAX_ITEMS + " items");
        }
        this.items = items;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null");
        }
        this.shippingAddress = shippingAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.paymentMethod = paymentMethod;
    }

    /**
     * Adds an order item to the items array
     * @param item The order item to add
     * @throws IllegalArgumentException if the item is null
     * @throws IllegalStateException if the items array is full
     */
    public void addOrderItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item;
                return;
            }
        }
        throw new IllegalStateException("Cannot add more items: order is full");
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", userName='" + customerName + '\'' +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", shippingAddress=" + shippingAddress +
                ", paymentMethod=" + paymentMethod +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}