package pchub.dao;

import pchub.model.Product;
import java.util.List;

public interface ProductDao {
    Product findById(String productId);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    boolean insertProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(String productId);
}