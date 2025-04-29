package pchub.service;

import pchub.dao.OrderDao;
import pchub.dao.BillDao;
import pchub.dao.ProductDao;
import pchub.model.*;
import pchub.model.enums.OrderStatus;
import pchub.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Date;

/**
 * Service class for managing orders in the PC Hub system.
 * This class provides methods for order creation, processing, and management.
 */
public class OrderService {
    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final CartService cartService;
    private final BillDao billDao;
    private static final BigDecimal TAX_RATE = new BigDecimal("0.13"); // 13% tax rate
    private static final BigDecimal SHIPPING_RATE = new BigDecimal("9.99"); // Standard shipping cost

    /**
     * Default constructor
     */
    public OrderService() {
        this.orderDao = new OrderDao();
        this.productDao = new ProductDao();
        this.cartService = new CartService();
        this.billDao = new BillDao();
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
    public Order createOrderFromCart(User user, Cart cart, Address shippingAddress, PaymentMethod paymentMethod) {
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
            Order order = new Order();
            order.setCustomerId(user.getUserId());
            order.setCustomerName(user.getUsername());
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);

            double totalAmount = 0;
            OrderItem[] orderItems = new OrderItem[30];
            int itemCount = 0;

            for (CartItem cartItem : cart.getItems()) {
                if (cartItem != null && itemCount < orderItems.length) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(cartItem.getProductId());
                    orderItem.setProductName(cartItem.getProductName());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    orderItem.setQuantity(cartItem.getQuantity());

                    orderItems[itemCount++] = orderItem;
                    totalAmount += (orderItem.getUnitPrice() * orderItem.getQuantity());
                }
            }

            order.setItems(orderItems);
            order.setTotalAmount(totalAmount);
            return order;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order from cart: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if all items in an array are null
     * @param items The array of items to check
     * @return true if all items are null, false otherwise
     */
    private boolean allItemsNull(CartItem[] items) {
        if (items == null) return true;

        for (CartItem item : items) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Places an order in the system
     * @param order The order to place
     * @return true if the order was placed successfully, false otherwise
     * @throws IllegalArgumentException if order is null
     * @throws SQLException if there is a database error
     */
    public boolean placeOrder(Order order) throws SQLException {
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
                cartService.clearCart(cart);
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
    public Bill generateBill(String orderId) {
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

            // TODO: This part need to be done when the Transaction Class is created
//            // Create and save transaction record
//            Bill bill = new Bill();
//            bill.setOrderId(orderId);
//            bill.setTotalAmount(totalAmount);
//            bill.getPaymentMethod().setPaymentMethodId(order.getPaymentMethod().getPaymentMethodId());
//            bill.setPaymentStatus(PaymentStatus.PENDING);

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
    public Order getOrderById(String orderId) {
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
    public Order[] getOrdersByUser(String customerId) {
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
    public Order[] getAllOrders() {
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
    public boolean updateOrderStatus(String orderId, OrderStatus newStatus) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("Order status cannot be null");
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
     * Deletes an order
     * @param orderId The ID of the order to delete
     * @return true if the deletion was successful, false otherwise
     * @throws IllegalArgumentException if orderId is null or empty
     */
    public boolean deleteOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        try {
            return orderDao.delete(orderId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete order: " + e.getMessage(), e);
        }
    }

    /**
     * Processes a payment for a bill
     * @param billId The ID of the bill
     * @param transactionId The transaction ID from the payment processor
     * @return true if the payment was processed successfully, false otherwise
     * @throws IllegalArgumentException if billId or transactionId is null or empty
     */
    public boolean processPayment(String billId, String transactionId) {
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
}