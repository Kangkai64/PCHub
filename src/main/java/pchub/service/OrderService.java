package pchub.service;

import pchub.model.*;
import pchub.model.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    Order createOrderFromCart(User user, ShoppingCart cart, Address shippingAddress, PaymentMethod paymentMethod);
    boolean placeOrder(Order order);
    Order getOrderById(String orderId);
    List<Order> getOrdersByUser(String customerId);
    List<Order> getAllOrders();
    boolean updateOrderStatus(String orderId, OrderStatus newStatus);
    boolean deleteOrder(String orderId);
}
