package pchub.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import pchub.dao.BillDao;
import pchub.dao.OrderDao;
import pchub.dao.OrderItemDao;
import pchub.dao.ProductDao;
import pchub.dao.AddressDao;
import pchub.dao.PaymentMethodDao;
import pchub.model.enums.OrderStatus;
import pchub.model.enums.PaymentStatus;

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
    private Address shippingAddress;
    private PaymentMethod paymentMethod;

    private static final int MAX_ITEMS = 30;
    private static final OrderDao orderDao = new OrderDao();
    private static final OrderItemDao orderItemDao = new OrderItemDao();
    private static final ProductDao productDao = new ProductDao();
    private static final BillDao billDao = new BillDao();
    private static final AddressDao addressDao = new AddressDao();
    private static final PaymentMethodDao paymentMethodDao = new PaymentMethodDao();
    private static final BigDecimal TAX_RATE = new BigDecimal("0.13"); // 13% tax rate
    private static final BigDecimal SHIPPING_RATE = new BigDecimal("9.99"); // Standard shipping cost

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

    public void setItems(OrderItem[] items) throws SQLException {
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

    /**
     * Creates an order from a shopping cart
     * @param user The user placing the order
     * @param cart The shopping cart containing items
     * @param shippingAddress The shipping address for the order
     * @param paymentMethod The payment method to be used
     * @return The created order, or null if cart is empty or invalid
     * @throws IllegalArgumentException if any parameter is null
     */
    public static Order createOrderFromCart(User user, Cart cart, Address shippingAddress, PaymentMethod paymentMethod) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (cart.getItems() == null || allItemsNull(cart.getItems())) {
            return null;
        }

        try {
            // First calculate the total amount from cart items
            double totalAmount = 0;
            for (CartItem cartItem : cart.getItems()) {
                if (cartItem != null) {
                    totalAmount += (cartItem.getUnitPrice() * cartItem.getQuantity());
                }
            }

            // Create and populate the order
            Order order = new Order();
            order.setCustomerId(user.getUserId());
            order.setCustomerName(user.getUsername());
            order.setOrderDate(new Date()); // Set current date
            order.setStatus(OrderStatus.PENDING); // Set initial status
            order.setTotalAmount(totalAmount);
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);

            // Insert the order first
            if (!orderDao.insert(order)) {
                throw new SQLException("Failed to insert order");
            }

            // Now create and save the order items
            OrderItem[] orderItems = new OrderItem[30];
            int itemCount = 0;

            for (CartItem cartItem : cart.getItems()) {
                if (cartItem != null && itemCount < orderItems.length) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getOrderId());
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setProductName(cartItem.getProductName());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    orderItem.setQuantity(cartItem.getQuantity());

                    orderItems[itemCount] = orderItem;
                    orderItemDao.insert(orderItems[itemCount]);
                    itemCount++;
                }
            }

            order.setItems(orderItems);
            return order;
        } catch (SQLException e) {
            throw new SQLException("Failed to create order from cart: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order from cart: " + e.getMessage(), e);
        }
    }

    /**
     * Places an order
     * @param order The order to place
     * @return true if the order was placed successfully, false otherwise
     * @throws IllegalArgumentException if order is null
     * @throws SQLException if there is a database error
     */
    public static boolean placeOrder(Order order) throws SQLException {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }

        try {
            // First, check if all products are in stock
            for (OrderItem item : order.getItems()) {
                if (item != null) {
                    Product product = productDao.findById(item.getProductId());
                    if (product == null || product.getCurrentQuantity() < item.getQuantity()) {
                        throw new IllegalStateException("Product " + item.getProductId() + " is out of stock or does not exist");
                    }
                }
            }

            // Update product quantities
            for (OrderItem item : order.getItems()) {
                if (item != null) {
                    Product product = productDao.findById(item.getProductId());
                    product.setCurrentQuantity(product.getCurrentQuantity() - item.getQuantity());
                    if (!productDao.update(product)) {
                        throw new RuntimeException("Failed to update product quantity");
                    }
                }
            }

            // Save the order
            boolean orderSaved = orderDao.insert(order);

            // Clear the cart after successful order placement
            if (orderSaved) {
                Cart cart = new Cart();
                cart.setCustomerId(order.getCustomerId());
                Cart.clearCart(cart);
            }

            return orderSaved;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to place order: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a bill for an order
     * @param orderId The ID of the order
     * @return The generated bill, or null if order not found
     * @throws IllegalArgumentException if orderId is null or empty
     */
    public static Bill generateBill(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        try {
            Order order = orderDao.findById(orderId.trim());
            if (order == null) {
                return null;
            }

            Bill bill = new Bill();
            bill.setOrderId(orderId);
            bill.setCustomerId(order.getCustomerId());
            bill.setCustomerName(order.getCustomerName());
            bill.setShippingAddress(order.getShippingAddress());
            bill.setItems(order.getItems());
            bill.setPaymentMethod(order.getPaymentMethod());
            bill.setIssueDate(new Date());

            // Calculate bill components
            BigDecimal subtotal = BigDecimal.valueOf(order.getTotalAmount());
            BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal shippingCost = SHIPPING_RATE;
            BigDecimal totalAmount = subtotal.add(tax).add(shippingCost);

            bill.setSubtotal(subtotal);
            bill.setTax(tax);
            bill.setShippingCost(shippingCost);
            bill.setTotalAmount(totalAmount);

            if (billDao.insert(bill)) {
                bill.setBillId(bill.getBillId());
                bill.setPaymentStatus(bill.getPaymentStatus());
            } else {
                bill.setPaymentStatus(PaymentStatus.FAILED);
            }

            return bill;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate bill: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves an order by its ID
     * @param orderId The ID of the order to retrieve
     * @return The order if found, null otherwise
     * @throws IllegalArgumentException if orderId is null or empty
     */
    public static Order getOrderById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        try {
            return orderDao.findById(orderId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve order: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all orders for a user
     * @param customerId The ID of the customer
     * @return Array of orders for the customer
     * @throws IllegalArgumentException if customerId is null or empty
     */
    public static Order[] getOrdersByUser(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }

        try {
            return orderDao.findByUserId(customerId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve user orders: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all orders in the system
     * @return Array of all orders
     */
    public static Order[] getAllOrders() {
        try {
            return orderDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve all orders: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the status of an order
     * @param orderId The ID of the order to update
     * @param newStatus The new status to set
     * @return true if the update was successful, false otherwise
     * @throws IllegalArgumentException if orderId is null or empty, or newStatus is null
     */
    public static boolean updateOrderStatus(String orderId, OrderStatus newStatus) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }

        try {
            Order order = orderDao.findById(orderId.trim());
            if (order == null) {
                return false;
            }

            order.setStatus(newStatus);
            return orderDao.update(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order status: " + e.getMessage(), e);
        }
    }

    /**
     * Processes a payment for a bill
     * @param billId The ID of the bill
     * @param transactionId The transaction ID
     * @return true if the payment was processed successfully, false otherwise
     * @throws IllegalArgumentException if billId or transactionId is null or empty
     */
    public static boolean processPayment(String billId, String transactionId) {
        if (billId == null || billId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill ID cannot be null or empty");
        }
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }

        try {
            Bill bill = billDao.findById(billId.trim());
            if (bill == null) {
                return false;
            }

            // Update transaction ID
            if (!billDao.update(bill)) {
                return false;
            }

            // Update payment status to COMPLETED
            return billDao.update(bill);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    private static boolean allItemsNull(Object[] items) {
        if (items == null) {
            return true;
        }
        for (Object item : items) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }
}