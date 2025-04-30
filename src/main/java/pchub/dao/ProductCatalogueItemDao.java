package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pchub.model.ProductCatalogueItem;
import pchub.utils.DatabaseConnection;

public class ProductCatalogueItemDao {
    private final Connection connection;
    private final ProductDao productDao;
    private final ProductCatalogueDao catalogueDao;

    public ProductCatalogueItemDao() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.productDao = new ProductDao();
        this.catalogueDao = new ProductCatalogueDao();
    }

    public boolean insert(ProductCatalogueItem item) throws SQLException {
        String sql = "INSERT INTO product_catalogue_item (itemID, catalogueID, productID, specialPrice, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getItemID());
            stmt.setString(2, item.getCatalogue().getCatalogueID());
            stmt.setString(3, item.getProduct().getProductID());
            stmt.setDouble(4, item.getSpecialPrice());
            stmt.setString(5, item.getNotes());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(ProductCatalogueItem item) throws SQLException {
        String sql = "UPDATE product_catalogue_item SET catalogueID = ?, productID = ?, specialPrice = ?, notes = ? WHERE itemID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getCatalogue().getCatalogueID());
            stmt.setString(2, item.getProduct().getProductID());
            stmt.setDouble(3, item.getSpecialPrice());
            stmt.setString(4, item.getNotes());
            stmt.setString(5, item.getItemID());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String itemID) throws SQLException {
        String sql = "DELETE FROM product_catalogue_item WHERE itemID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemID);
            return stmt.executeUpdate() > 0;
        }
    }

    public ProductCatalogueItem findById(String itemID) throws SQLException {
        String sql = "SELECT * FROM product_catalogue_item WHERE itemID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        }
        return null;
    }

    public ProductCatalogueItem[] findAll() throws SQLException {
        String sql = "SELECT * FROM product_catalogue_item";
        List<ProductCatalogueItem> items = new ArrayList<>();
        try (ResultSet rs = connection.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        }
        return items.toArray(new ProductCatalogueItem[0]);
    }

    public ProductCatalogueItem[] findByCatalogue(String catalogueID) throws SQLException {
        String sql = "SELECT * FROM product_catalogue_item WHERE catalogueID = ?";
        List<ProductCatalogueItem> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, catalogueID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        }
        return items.toArray(new ProductCatalogueItem[0]);
    }

    private ProductCatalogueItem mapResultSetToItem(ResultSet rs) throws SQLException {
        ProductCatalogueItem item = new ProductCatalogueItem();
        item.setItemID(rs.getString("itemID"));
        item.setCatalogue(catalogueDao.findById(rs.getString("catalogueID")));
        item.setProduct(productDao.findById(rs.getString("productID")));
        item.setSpecialPrice(rs.getDouble("specialPrice"));
        item.setNotes(rs.getString("notes"));
        return item;
    }

    public ProductCatalogueItem[] findByProduct(String productId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT * FROM product_catalogue_item WHERE productID = ?")) {
            statement.setString(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                ProductCatalogueItem[] items = new ProductCatalogueItem[30];
                int count = 0;
                
                while (resultSet.next() && count < items.length) {
                    items[count++] = mapResultSetToItem(resultSet);
                }
                
                return java.util.Arrays.copyOf(items, count);
            }
        }
    }
} 