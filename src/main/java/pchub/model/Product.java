package pchub.model;

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
}