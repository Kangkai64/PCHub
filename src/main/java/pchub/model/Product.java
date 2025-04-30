package pchub.model;

import pchub.dao.ProductDao;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a product in the PC Hub system.
 * This class contains information about a product including its ID, name, description,
 * brand, category, price, quantity, and specifications.
 */
public class Product {
    private String productID;
    private String name;
    private String description;
    private String brand;
    private String category;
    private double unitPrice;
    private int currentQuantity;
    private String specifications;

    private static final ProductDao productDao = new ProductDao();

    /**
     * Default constructor
     */
    public Product() {
    }

    /**
     * Parameterized constructor
     * @param productID The unique identifier for the product
     * @param name The name of the product
     * @param description The description of the product
     * @param brand The brand of the product
     * @param category The category of the product
     * @param unitPrice The unit price of the product
     * @param currentQuantity The current quantity in stock
     * @param specifications The specifications of the product
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Product(String productID, String name, String description, String brand, 
                  String category, double unitPrice, int currentQuantity, String specifications) {
        setProductID(productID);
        setName(name);
        setDescription(description);
        setBrand(brand);
        setCategory(category);
        setUnitPrice(unitPrice);
        setCurrentQuantity(currentQuantity);
        setSpecifications(specifications);
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        if (productID == null || productID.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        this.productID = productID.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Product description cannot be null");
        }
        this.description = description.trim();
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Product brand cannot be null or empty");
        }
        this.brand = brand.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
        this.category = category.trim();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.unitPrice = unitPrice;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        if (currentQuantity < 0) {
            throw new IllegalArgumentException("Current quantity cannot be negative");
        }
        this.currentQuantity = currentQuantity;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        if (specifications == null) {
            throw new IllegalArgumentException("Product specifications cannot be null");
        }
        this.specifications = specifications.trim();
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID='" + productID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", unitPrice=" + unitPrice +
                ", currentQuantity=" + currentQuantity +
                ", specifications='" + specifications + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productID.equals(product.productID);
    }

    @Override
    public int hashCode() {
        return productID.hashCode();
    }

    /**
     * Retrieves all products from the database
     * @return Array of all products
     */
    public static Product[] getAllProducts() {
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
    public static Product[] searchProducts(String keyword) {
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
    public static Product[] getProductsByCategory(String category) {
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
    public static Product getProduct(String productId) {
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
    public static boolean addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        try {
            return productDao.insert(product);
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
    public static boolean updateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        try {
            return productDao.update(product);
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
    public static boolean deleteProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        try {
            return productDao.delete(productId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product: " + e.getMessage(), e);
        }
    }
}