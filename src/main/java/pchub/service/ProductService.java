package pchub.service;

import pchub.dao.ProductDao;
import pchub.model.Product;

import java.util.Arrays;
import java.util.Objects;

/**
 * Service class for managing products in the PC Hub system.
 * This class provides methods for CRUD operations and searching products.
 */
public class ProductService {
    private final ProductDao productDao;

    /**
     * Default constructor
     */
    public ProductService() {
        this.productDao = new ProductDao();
    }

    /**
     * Retrieves all products from the database
     * @return Array of all products
     */
    public Product[] getAllProducts() {
        try {
            return productDao.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve products: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for products matching the given keyword
     * @param keyword The search keyword
     * @return Array of matching products
     * @throws IllegalArgumentException if keyword is null or empty
     */
    public Product[] searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }

        try {
            Product[] allProducts = productDao.findAll();
            String lowercaseKeyword = keyword.toLowerCase().trim();

            return Arrays.stream(allProducts)
                    .filter(Objects::nonNull)
                    .filter(p -> p.getName().toLowerCase().contains(lowercaseKeyword) ||
                            p.getDescription().toLowerCase().contains(lowercaseKeyword))
                    .toArray(Product[]::new);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search products: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves products by category
     * @param category The category to filter by
     * @return Array of products in the specified category
     * @throws IllegalArgumentException if category is null or empty
     */
    public Product[] getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }

        try {
            return productDao.findByCategory(category.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve products by category: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a product by its ID
     * @param productId The ID of the product to retrieve
     * @return The product if found, null otherwise
     * @throws IllegalArgumentException if productId is null or empty
     */
    public Product getProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        try {
            return productDao.findById(productId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve product: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a new product to the database
     * @param product The product to add
     * @return true if the product was added successfully, false otherwise
     * @throws IllegalArgumentException if product is null or invalid
     */
    public boolean addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        try {
            return productDao.insertProduct(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing product in the database
     * @param product The product to update
     * @return true if the product was updated successfully, false otherwise
     * @throws IllegalArgumentException if product is null or invalid
     */
    public boolean updateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        try {
            return productDao.updateProduct(product);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a product from the database
     * @param productId The ID of the product to delete
     * @return true if the product was deleted successfully, false otherwise
     * @throws IllegalArgumentException if productId is null or empty
     */
    public boolean deleteProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        try {
            return productDao.deleteProduct(productId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }
}
