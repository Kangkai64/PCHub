package pchub.service;

import pchub.dao.OrderDao;
import pchub.dao.ProductDao;
import pchub.model.*;
import pchub.model.enums.OrderStatus;

import java.util.List;

public class OrderService {
    private OrderDao orderDao;
    private ProductDao productDao;
    private CartService cartService;

    public OrderService() {
        this.orderDao = new OrderDao();
        this.productDao = new ProductDao();
        this.cartService = new CartService();
    }

    public Order createOrderFromCart(User user, ShoppingCart cart, Address shippingAddress, PaymentMethod paymentMethod) {
        if (cart.getItems().isEmpty()) {
            return null;
        }

        pchub.model.Order order = new pchub.model.Order();
        order.setCustomerId(user.getId());
        order.setUserName(user.getUsername());
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);

        double totalAmount = 0;

        for (pchub.model.CartItem cartItem : cart.getItems()) {
            pchub.model.OrderItem orderItem = new pchub.model.OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            order.getItems().add(orderItem);
            totalAmount += orderItem.getSubtotal();
        }

        order.setTotalAmount(totalAmount);
        return order;
    }

    public boolean placeOrder(pchub.model.Order order) {
        // First, check if all products are in stock
        for (pchub.model.OrderItem item : order.getItems()) {
            pchub.model.Product product = productDao.findById(item.getProductId());
            if (product == null || product.getStockQuantity() < item.getQuantity()) {
                return false;
            }
        }

        // Update product quantities
        for (pchub.model.OrderItem item : order.getItems()) {
            pchub.model.Product product = productDao.findById(item.getProductId());
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productDao.updateProduct(product);
        }

        // Save the order
        boolean orderSaved = orderDao.insertOrder(order);

        // Clear the cart after successful order placement
        if (orderSaved) {
            pchub.model.ShoppingCart cart = new pchub.model.ShoppingCart();
            cart.setUserId(order.getCustomerId());
            cartService.clearCart(cart);
        }

        return orderSaved;
    }

    public pchub.model.Order getOrderById(int orderId) {
        return orderDao.findById(orderId);
    }

    public List<pchub.model.Order> getOrdersByUser(String customerId) {
        return orderDao.findByUserId(customerId);
    }

    public List<pchub.model.Order> getAllOrders() {
        return orderDao.findAll();
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        pchub.model.Order order = orderDao.findById(orderId);
        if (order == null) {
            return false;
        }

        order.setStatus(newStatus);
        return orderDao.updateOrder(order);
    }

    public boolean deleteOrder(String orderId) {
        return orderDao.deleteOrder(orderId);
    }
}
