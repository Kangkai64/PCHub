package pchub.dao;

import pchub.model.Order;
import java.util.List;

public interface OrderDao {
    Order findById(String orderId);
    List<Order> findByUserId(String customerId);
    List<Order> findAll();
    boolean insertOrder(Order order);
    boolean updateOrder(Order order);
    boolean deleteOrder(String orderId);
}
