package pchub.model;

import pchub.dao.ProductCatalogueDao;
import pchub.dao.ProductCatalogueItemDao;

import java.sql.SQLException;
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

    private static final ProductCatalogueDao PRODUCT_CATALOGUE_DAO;

    static {
        try {
            PRODUCT_CATALOGUE_DAO = new ProductCatalogueDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final ProductCatalogueItemDao itemDao;

    static {
        try {
            itemDao = new ProductCatalogueItemDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Default constructor
     */
    public ProductCatalogue() throws SQLException {
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
    public ProductCatalogue(String catalogueID, String name, String description, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
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

    public static ProductCatalogue[] getAllCatalogues() throws SQLException {
        return PRODUCT_CATALOGUE_DAO.findAll();
    }

    public static ProductCatalogue getCatalogueById(String catalogueId) throws SQLException {
        return PRODUCT_CATALOGUE_DAO.findById(catalogueId);
    }

    public static ProductCatalogueItem[] getCatalogueItems(String catalogueId) throws SQLException {
        return itemDao.findByCatalogue(catalogueId);
    }

    public static ProductCatalogueItem getCatalogueItemById(String itemId) throws SQLException {
        return itemDao.findById(itemId);
    }

    public static boolean addCatalogue(ProductCatalogue catalogue) throws SQLException {
        return PRODUCT_CATALOGUE_DAO.insert(catalogue);
    }

    public static boolean updateCatalogue(ProductCatalogue catalogue) throws SQLException {
        return PRODUCT_CATALOGUE_DAO.update(catalogue);
    }
    public static boolean deleteCatalogue(String catalogueId) throws SQLException {
        return PRODUCT_CATALOGUE_DAO.delete(catalogueId);
    }

    public static boolean addCatalogueItem(ProductCatalogueItem item) throws SQLException {
        return itemDao.insert(item);
    }

    public static boolean updateCatalogueItem(ProductCatalogueItem item) throws SQLException {
        return itemDao.update(item);
    }

    public static boolean deleteCatalogueItem(String itemId) throws SQLException {
        return itemDao.delete(itemId);
    }
} 