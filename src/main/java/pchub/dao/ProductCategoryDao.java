package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pchub.model.ProductCategory;
import pchub.utils.DatabaseConnection;

public class ProductCategoryDao extends DaoTemplate<ProductCategory> {
    private final Connection connection;
    private static final int MAX_CATEGORY = 100;

    public ProductCategoryDao() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public boolean insert(ProductCategory category) throws SQLException {
        String sql = "INSERT INTO product_category (product_categoryID, name, description, parent_category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category.getProduct_categoryID());
            preparedStatement.setString(2, category.getName());
            preparedStatement.setString(3, category.getDescription());
            preparedStatement.setString(4, category.getParentCategory());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
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

    @Override
    public boolean delete(String categoryID) throws SQLException {
        String sql = "DELETE FROM product_category WHERE product_categoryID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryID);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public ProductCategory findById(String categoryID) throws SQLException {
        String sql = "SELECT * FROM product_category WHERE product_categoryID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public ProductCategory[] findAll() throws SQLException {
        ProductCategory[] categories = new ProductCategory[MAX_CATEGORY];
        String sql = "SELECT * FROM product_category";
        int index = 0;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                categories[index] = mapResultSet(resultSet);
                index++;
            }
        }
        return categories;
    }

    public ProductCategory[] findByParentId(String parentCategoryID) throws SQLException {
        ProductCategory[] categories = new ProductCategory[MAX_CATEGORY];
        String sql = "SELECT * FROM product_category WHERE parent_category_id = ?";
        int index = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, parentCategoryID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    categories[index] = mapResultSet(resultSet);
                    index++;
                }
            }
        }
        return categories;
    }

    @Override
    protected ProductCategory mapResultSet(ResultSet resultSet) throws SQLException {
        ProductCategory category = new ProductCategory();
        category.setProduct_categoryID(resultSet.getString("product_categoryID"));
        category.setName(resultSet.getString("name"));
        category.setDescription(resultSet.getString("description"));
        category.setParentCategory(resultSet.getString("parent_category_id"));
        return category;
    }
} 