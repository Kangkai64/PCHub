package pchub.service;

import pchub.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> searchProducts(String keyword);
    List<Product> getProductsByCategory(String category);
    Product getProduct(String productId);
    boolean addProduct(pchub.model.Product product);
    boolean updateProduct(pchub.model.Product product);
    boolean deleteProduct(String productId);
}
