package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import pchub.model.ProductCategory;
import pchub.utils.DatabaseConnection;

public class ProductCategoryDao {
    private final Connection connection;

    public ProductCategoryDao() {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean insert(ProductCategory category) throws SQLException {
        String sql = "INSERT INTO product_category (product_categoryID, name, description, parent_category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getProduct_categoryID());
            stmt.setString(2, category.getName());
            stmt.setString(3, category.getDescription());
            stmt.setString(4, category.getParentCategory());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(ProductCategory category) throws SQLException {
        String sql = "UPDATE product_category SET name = ?, description = ?, parent_category_id = ? WHERE product_categoryID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, category.getParentCategory());
            stmt.setString(4, category.getProduct_categoryID());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String categoryID) throws SQLException {
        String sql = "DELETE FROM product_category WHERE product_categoryID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryID);
            return stmt.executeUpdate() > 0;
        }
    }

    public ProductCategory findById(String categoryID) throws SQLException {
        String sql = "SELECT * FROM product_category WHERE product_categoryID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategory(rs);
                }
            }
        }
        return null;
    }

    public List<ProductCategory> findAll() throws SQLException {
        List<ProductCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM product_category";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }
        return categories;
    }

    public List<ProductCategory> findByParentId(String parentCategoryID) throws SQLException {
        List<ProductCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM product_category WHERE parent_category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, parentCategoryID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategory(rs));
                }
            }
        }
        return categories;
    }

    private ProductCategory mapResultSetToCategory(ResultSet rs) throws SQLException {
        ProductCategory category = new ProductCategory();
        category.setProduct_categoryID(rs.getString("product_categoryID"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setParentCategory(rs.getString("parent_category_id"));
        return category;
    }
} 