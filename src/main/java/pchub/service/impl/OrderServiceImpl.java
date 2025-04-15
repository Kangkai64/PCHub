package pchub.service.impl;

import pchub.dao.OrderDao;
import pchub.dao.ProductDao;
import pchub.dao.impl.OrderDaoImpl;
import pchub.dao.impl.ProductDaoImpl;
import pchub.model.Address;
import pchub.model.PaymentMethod;
import pchub.model.ShoppingCart;
import pchub.model.User;
import pchub.model.enums.OrderStatus;
import pchub.service.CartService;
import pchub.service.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {
    private OrderDao orderDao;
    private ProductDao productDao;
    private CartService cartService;

    public OrderServiceImpl() {
        this.orderDao = new OrderDaoImpl();
        this.productDao = new ProductDaoImpl();
        this.cartService = new CartServiceImpl();
    }

    @Override
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

    @Override
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

    @Override
    public pchub.model.Order getOrderById(int orderId) {
        return orderDao.findById(orderId);
    }

    @Override
    public List<pchub.model.Order> getOrdersByUser(String customerId) {
        return orderDao.findByUserId(customerId);
    }

    @Override
    public List<pchub.model.Order> getAllOrders() {
        return orderDao.findAll();
    }

    @Override
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        pchub.model.Order order = orderDao.findById(orderId);
        if (order == null) {
            return false;
        }

        order.setStatus(newStatus);
        return orderDao.updateOrder(order);
    }

    @Override
    public boolean deleteOrder(String orderId) {
        return orderDao.deleteOrder(orderId);
    }
}
