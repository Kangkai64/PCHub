package pchub.model;

/**
 * Represents a product category in the PC Hub system.
 * This class contains information about a product category including its ID, name,
 * and subcategories.
 */
public class ProductCategory {
    private String product_categoryID;
    private String name;
    private String parentCategory;
    private String description;
    private ProductCategory[] subCategories;

    /**
     * Default constructor
     */
    public ProductCategory() {
        this.subCategories = new ProductCategory[30]; // Array size 30 as per project requirements
    }

    /**
     * Parameterized constructor
     * @param product_categoryID The unique identifier for the category
     * @param name The name of the category
     * @param description The description of the category
     */
    public ProductCategory(String product_categoryID, String name, String description) {
        this();
        this.product_categoryID = product_categoryID;
        this.name = name;
        this.description = description;
    }

    public String getProduct_categoryID() {
        return product_categoryID;
    }

    public void setProduct_categoryID(String product_categoryID) {
        this.product_categoryID = product_categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductCategory[] getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ProductCategory[] subCategories) {
        this.subCategories = subCategories;
    }

    public void addSubCategory(ProductCategory subCategory) {
        for (int i = 0; i < subCategories.length; i++) {
            if (subCategories[i] == null) {
                subCategories[i] = subCategory;
                subCategory.setParentCategory(this.product_categoryID);
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategory that = (ProductCategory) o;
        return product_categoryID.equals(that.product_categoryID);
    }

    @Override
    public int hashCode() {
        return product_categoryID.hashCode();
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "product_categoryID='" + product_categoryID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parentCategory=" + parentCategory +
                '}';
    }
} 