package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import pchub.model.ProductCatalogue;
import pchub.utils.DatabaseConnection;

public class ProductCatalogueDao {
    private final Connection connection;
    private static final int MAX_PRODUCT_CATALOGUE = 100;

    public ProductCatalogueDao() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public boolean insert(ProductCatalogue catalogue) throws SQLException {
        String sql = "INSERT INTO product_catalogue (name, description, startDate, endDate) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, catalogue.getName());
            stmt.setString(2, catalogue.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(catalogue.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(catalogue.getEndDate()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the last inserted ID using a separate query
                try (PreparedStatement getIdStmt = connection.prepareStatement(
                        "SELECT catalogueID FROM product_catalogue WHERE name = ? AND description = ? AND startDate = ? AND endDate = ? ORDER BY catalogueID DESC LIMIT 1")) {
                    getIdStmt.setString(1, catalogue.getName());
                    getIdStmt.setString(2, catalogue.getDescription());
                    getIdStmt.setTimestamp(3, Timestamp.valueOf(catalogue.getStartDate()));
                    getIdStmt.setTimestamp(4, Timestamp.valueOf(catalogue.getEndDate()));
                    
                    try (ResultSet resultSet = getIdStmt.executeQuery()) {
                        if (resultSet.next()) {
                            catalogue.setCatalogueID(resultSet.getString("catalogueID"));
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error inserting catalogue: " + e.getMessage());
            throw e;
        }
    }

    public boolean update(ProductCatalogue catalogue) throws SQLException {
        String sql = "UPDATE product_catalogue SET name = ?, description = ?, startDate = ?, endDate = ? WHERE catalogueID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, catalogue.getName());
            preparedStatement.setString(2, catalogue.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(catalogue.getStartDate()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(catalogue.getEndDate()));
            preparedStatement.setString(5, catalogue.getCatalogueID());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public boolean delete(String catalogueID) throws SQLException {
        String sql = "DELETE FROM product_catalogue WHERE catalogueID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, catalogueID);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    public ProductCatalogue findById(String catalogueID) throws SQLException {
        String sql = "SELECT * FROM product_catalogue WHERE catalogueID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, catalogueID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCatalogue(resultSet);
                }
            }
        }
        return null;
    }

    public ProductCatalogue[] findAll() throws SQLException {
        String sql = "SELECT * FROM product_catalogue";
        ProductCatalogue[] catalogues = new ProductCatalogue[MAX_PRODUCT_CATALOGUE];
        int index = 0;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                catalogues[index] = mapResultSetToCatalogue(resultSet);
                index++;
            }
        }
        return catalogues;
    }

    private ProductCatalogue mapResultSetToCatalogue(ResultSet resultSet) throws SQLException {
        ProductCatalogue catalogue = new ProductCatalogue();
        catalogue.setCatalogueID(resultSet.getString("catalogueID"));
        catalogue.setName(resultSet.getString("name"));
        catalogue.setDescription(resultSet.getString("description"));
        catalogue.setStartDate(resultSet.getTimestamp("startDate").toLocalDateTime());
        catalogue.setEndDate(resultSet.getTimestamp("endDate").toLocalDateTime());
        return catalogue;
    }
} 