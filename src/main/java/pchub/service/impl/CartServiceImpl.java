package pchub.service.impl;

import pchub.dao.CartDao;
import pchub.dao.ProductDao;
import pchub.dao.impl.CartDaoImpl;
import pchub.dao.impl.ProductDaoImpl;
import pchub.model.CartItem;
import pchub.model.Product;
import pchub.model.ShoppingCart;
import pchub.model.User;
import pchub.service.CartService;
import pchub.model.CartItem;
import pchub.model.Product;
import pchub.model.ShoppingCart;
import pchub.model.User;

import java.util.Optional;

public class CartServiceImpl implements CartService {
    private CartDao cartDao;
    private ProductDao productDao;

    public CartServiceImpl() {
        this.cartDao = new CartDaoImpl();
        this.productDao = new ProductDaoImpl();
    }

    @Override
    public pchub.model.ShoppingCart getCartForUser(pchub.model.User user) {
        pchub.model.ShoppingCart cart = cartDao.findByUserId(user.getId());
        if (cart == null) {
            cart = new pchub.model.ShoppingCart();
            cart.setUserId(user.getId());
            cartDao.save(cart);
        }
        return cart;
    }

    @Override
    public boolean addItemToCart(pchub.model.ShoppingCart cart, pchub.model.Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            return false; // Not enough stock
        }

        // Check if the product is already in the cart
        Optional<pchub.model.CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId() == product.getId())
                .findFirst();

        if (existingItem.isPresent()) {
            pchub.model.CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getStockQuantity() < newQuantity) {
                return false; // Not enough stock for combined quantity
            }

            item.setQuantity(newQuantity);
        } else {
            pchub.model.CartItem newItem = new pchub.model.CartItem();
            newItem.setCartId(cart.getId());
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setUnitPrice(product.getPrice());
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartDao.update(cart);
    }

    @Override
    public boolean updateItemQuantity(pchub.model.ShoppingCart cart, int productId, int quantity) {
        if (quantity <= 0) {
            return removeItemFromCart(cart, productId);
        }

        Optional<pchub.model.CartItem> itemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId() == productId)
                .findFirst();

        if (!itemOpt.isPresent()) {
            return false;
        }

        pchub.model.CartItem item = itemOpt.get();
        pchub.model.Product product = productDao.findById(productId);

        if (product == null || product.getStockQuantity() < quantity) {
            return false;
        }

        item.setQuantity(quantity);
        return cartDao.update(cart);
    }

    @Override
    public boolean removeItemFromCart(pchub.model.ShoppingCart cart, int productId) {
        boolean removed = cart.getItems().removeIf(item -> item.getProductId() == productId);
        if (removed) {
            return cartDao.update(cart);
        }
        return false;
    }

    @Override
    public void clearCart(pchub.model.ShoppingCart cart) {
        cart.getItems().clear();
        cartDao.update(cart);
    }

    @Override
    public boolean saveCart(pchub.model.ShoppingCart cart) {
        return cartDao.update(cart);
    }
}
