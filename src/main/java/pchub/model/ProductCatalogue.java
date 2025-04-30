package pchub.model;

import java.time.LocalDateTime;

/**
 * Represents a product catalogue in the PC Hub system.
 * This class contains information about a product catalogue including its ID, name,
 * description, and the items in the catalogue.
 */
public class ProductCatalogue {
    private String catalogueID;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ProductCatalogueItem[] items;

    /**
     * Default constructor
     */
    public ProductCatalogue() {
        this.items = new ProductCatalogueItem[30]; // Array size 30 as per project requirements
    }

    /**
     * Parameterized constructor
     * @param catalogueID The unique identifier for the catalogue
     * @param name The name of the catalogue
     * @param description The description of the catalogue
     * @param startDate The start date of the catalogue
     * @param endDate The end date of the catalogue
     */
    public ProductCatalogue(String catalogueID, String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this();
        this.catalogueID = catalogueID;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getCatalogueID() {
        return catalogueID;
    }

    public void setCatalogueID(String catalogueID) {
        this.catalogueID = catalogueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public ProductCatalogueItem[] getItems() {
        return items;
    }

    public void setItems(ProductCatalogueItem[] items) {
        this.items = items;
    }

    public void addItem(ProductCatalogueItem item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item;
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "ProductCatalogue{" +
                "catalogueID='" + catalogueID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
} 