package pchub.dao;

import pchub.model.Order;
import java.util.List;

public interface OrderDao {
    Order findById(int id);
    List<Order> findByUserId(int userId);
    List<Order> findAll();
    boolean save(Order order);
    boolean update(Order order);
    boolean delete(int id);
}
