package pchub.model;

/**
 * Represents an item in a product catalogue in the PC Hub system.
 * This class contains information about a catalogue item including its ID,
 * associated product, and special price if any.
 */
public class ProductCatalogueItem {
    private String itemID;
    private ProductCatalogue catalogue;
    private Product product;
    private double specialPrice;
    private String notes;

    /**
     * Default constructor
     */
    public ProductCatalogueItem() {
    }

    /**
     * Parameterized constructor
     * @param itemID The unique identifier for the catalogue item
     * @param product The associated product
     * @param catalogue The catalogue this item belongs to
     * @param specialPrice The special price for this item (if any)
     * @param notes Additional notes about this catalogue item
     */
    public ProductCatalogueItem(String itemID, Product product, ProductCatalogue catalogue, 
                              double specialPrice, String notes) {
        this.itemID = itemID;
        this.product = product;
        this.catalogue = catalogue;
        this.specialPrice = specialPrice;
        this.notes = notes;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public ProductCatalogue getCatalogue() {
        return catalogue;
    }

    public void setCatalogue(ProductCatalogue catalogue) {
        this.catalogue = catalogue;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(double specialPrice) {
        this.specialPrice = specialPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCatalogueItem that = (ProductCatalogueItem) o;
        return itemID.equals(that.itemID);
    }

    @Override
    public int hashCode() {
        return itemID.hashCode();
    }

    @Override
    public String toString() {
        return "ProductCatalogueItem{" +
                "itemID='" + itemID + '\'' +
                ", product=" + (product != null ? product.getName() : "null") +
                ", specialPrice=" + specialPrice +
                ", notes='" + notes + '\'' +
                '}';
    }
} 