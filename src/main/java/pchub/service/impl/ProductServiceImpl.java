package pchub.service.impl;

import pchub.dao.ProductDao;
import pchub.dao.impl.ProductDaoImpl;
import pchub.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {
    private ProductDao productDao;

    public ProductServiceImpl() {
        this.productDao = new ProductDaoImpl();
    }

    @Override
    public List<pchub.model.Product> getAllProducts() {
        return productDao.findAll();
    }

    @Override
    public List<pchub.model.Product> searchProducts(String keyword) {
        List<pchub.model.Product> allProducts = productDao.findAll();
        String lowercaseKeyword = keyword.toLowerCase();

        return allProducts.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowercaseKeyword) ||
                        p.getDescription().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<pchub.model.Product> getProductsByCategory(String category) {
        return productDao.findByCategory(category);
    }

    @Override
    public pchub.model.Product getProduct(int productId) {
        return productDao.findById(productId);
    }

    @Override
    public boolean addProduct(pchub.model.Product product) {
        return productDao.save(product);
    }

    @Override
    public boolean updateProduct(pchub.model.Product product) {
        return productDao.update(product);
    }

    @Override
    public boolean deleteProduct(int productId) {
        return productDao.delete(productId);
    }
}
