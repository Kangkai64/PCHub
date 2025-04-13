package pchub.dao;

import pchub.model.Product;
import java.util.List;

public interface ProductDao {
    Product findById(int id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(int id);
}