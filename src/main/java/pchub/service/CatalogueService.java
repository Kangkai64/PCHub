package pchub.service;

import java.sql.SQLException;

import pchub.dao.ProductCatalogueDao;
import pchub.dao.ProductCatalogueItemDao;
import pchub.model.ProductCatalogue;
import pchub.model.ProductCatalogueItem;

public class CatalogueService {
    private final ProductCatalogueDao catalogueDao;
    private final ProductCatalogueItemDao itemDao;

    public CatalogueService() throws SQLException {
        this.catalogueDao = new ProductCatalogueDao();
        this.itemDao = new ProductCatalogueItemDao();
    }

    public ProductCatalogue[] getAllCatalogues() throws SQLException {
        return catalogueDao.findAll();
    }

    public ProductCatalogue getCatalogueById(String catalogueId) throws SQLException {
        return catalogueDao.findById(catalogueId);
    }

    public ProductCatalogueItem[] getCatalogueItems(String catalogueId) throws SQLException {
        return itemDao.findByCatalogue(catalogueId);
    }

    public ProductCatalogueItem getCatalogueItemById(String itemId) throws SQLException {
        return itemDao.findById(itemId);
    }

    public boolean addCatalogue(ProductCatalogue catalogue) throws SQLException {
        return catalogueDao.insert(catalogue);
    }

    public boolean updateCatalogue(ProductCatalogue catalogue) throws SQLException {
        return catalogueDao.update(catalogue);
    }

    public boolean deleteCatalogue(String catalogueId) throws SQLException {
        return catalogueDao.delete(catalogueId);
    }

    public boolean addCatalogueItem(ProductCatalogueItem item) throws SQLException {
        return itemDao.insert(item);
    }

    public boolean updateCatalogueItem(ProductCatalogueItem item) throws SQLException {
        return itemDao.update(item);
    }

    public boolean deleteCatalogueItem(String itemId) throws SQLException {
        return itemDao.delete(itemId);
    }
}