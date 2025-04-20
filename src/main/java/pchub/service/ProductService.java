package pchub.service;

import pchub.dao.ProductDao;
import pchub.model.Product;

import java.util.Arrays;

public class ProductService {
    private ProductDao productDao;

    public ProductService() {
        this.productDao = new ProductDao();
    }

    public Product[] getAllProducts() {
        return productDao.findAll();
    }

    public Product[] searchProducts(String keyword) {
        Product[] allProducts = productDao.findAll();
        String lowercaseKeyword = keyword.toLowerCase();

        return Arrays.stream(allProducts)
                .filter(p -> p.getName().toLowerCase().contains(lowercaseKeyword) ||
                        p.getDescription().toLowerCase().contains(lowercaseKeyword))
                .toArray(Product[]::new);
    }

    public Product[] getProductsByCategory(String category) {
        return productDao.findByCategory(category);
    }

    public Product getProduct(String productId) {
        return productDao.findById(productId);
    }

    public boolean addProduct(Product product) {
        return productDao.insertProduct(product);
    }

    public boolean updateProduct(Product product) {
        return productDao.updateProduct(product);
    }

    public boolean deleteProduct(String productId) {
        return productDao.deleteProduct(productId);
    }
}
