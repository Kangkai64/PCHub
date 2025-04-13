package pchub.service;

import pchub.model.enums.OrderStatus;
import pchub.model.PaymentMethod;

import java.util.List;

public interface OrderService {
    pchub.model.Order createOrderFromCart(pchub.model.User user, pchub.model.ShoppingCart cart, pchub.model.Address shippingAddress, PaymentMethod paymentMethod);
    boolean placeOrder(pchub.model.Order order);
    pchub.model.Order getOrderById(int orderId);
    List<pchub.model.Order> getOrdersByUser(int userId);
    List<pchub.model.Order> getAllOrders();
    boolean updateOrderStatus(int orderId, OrderStatus newStatus);
    boolean deleteOrder(int orderId);
}
