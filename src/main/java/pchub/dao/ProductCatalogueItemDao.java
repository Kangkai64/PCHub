package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pchub.model.ProductCatalogueItem;
import pchub.utils.DatabaseConnection;

public class ProductCatalogueItemDao extends DaoTemplate<ProductCatalogueItem> {
    private final Connection connection;
    private final ProductDao productDao;
    private final ProductCatalogueDao catalogueDao;
    private final static int MAX_PRODUCT_CATALOGUE_ITEM = 100;

    public ProductCatalogueItemDao() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.productDao = new ProductDao();
        this.catalogueDao = new ProductCatalogueDao();
    }

    @Override
    public boolean insert(ProductCatalogueItem item) throws SQLException {
        String sql = "INSERT INTO product_catalogue_item (itemID, catalogueID, productID, specialPrice, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, item.getItemID());
            preparedStatement.setString(2, item.getCatalogue().getCatalogueID());
            preparedStatement.setString(3, item.getProduct().getProductID());
            preparedStatement.setDouble(4, item.getSpecialPrice());
            preparedStatement.setString(5, item.getNotes());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(ProductCatalogueItem item) throws SQLException {
        String sql = "UPDATE product_catalogue_item SET catalogueID = ?, productID = ?, specialPrice = ?, notes = ? WHERE itemID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, item.getCatalogue().getCatalogueID());
            preparedStatement.setString(2, item.getProduct().getProductID());
            preparedStatement.setDouble(3, item.getSpecialPrice());
            preparedStatement.setString(4, item.getNotes());
            preparedStatement.setString(5, item.getItemID());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String itemID) throws SQLException {
        String sql = "DELETE FROM product_catalogue_item WHERE itemID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, itemID);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public ProductCatalogueItem findById(String itemID) throws SQLException {
        String sql = "SELECT * FROM product_catalogue_item WHERE itemID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, itemID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSet(resultSet);
                }
            }
        }
        return null;
    }

    public ProductCatalogueItem[] findAll() throws SQLException {
        String sql = "SELECT * FROM product_catalogue_item";
        ProductCatalogueItem[] items = new ProductCatalogueItem[MAX_PRODUCT_CATALOGUE_ITEM];
        int index = 0;

        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                items[index] = mapResultSet(resultSet);
            }
        }
        return items;
    }

    public ProductCatalogueItem[] findByCatalogue(String catalogueID) throws SQLException {
        String sql = "SELECT * FROM product_catalogue_item WHERE catalogueID = ?";
        List<ProductCatalogueItem> items = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, catalogueID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapResultSet(resultSet));
                }
            }
        }
        return items.toArray(new ProductCatalogueItem[0]);
    }

    @Override
    protected ProductCatalogueItem mapResultSet(ResultSet rs) throws SQLException {
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
                    items[count++] = mapResultSet(resultSet);
                }
                
                return java.util.Arrays.copyOf(items, count);
            }
        }
    }
} 