package pchub.service;

import pchub.model.Product;
import pchub.model.Product;

import java.util.List;

public interface ProductService {
    List<pchub.model.Product> getAllProducts();
    List<pchub.model.Product> searchProducts(String keyword);
    List<pchub.model.Product> getProductsByCategory(String category);
    pchub.model.Product getProduct(int productId);
    boolean addProduct(pchub.model.Product product);
    boolean updateProduct(pchub.model.Product product);
    boolean deleteProduct(int productId);
}
