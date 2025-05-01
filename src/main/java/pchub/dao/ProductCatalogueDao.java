package pchub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import pchub.model.ProductCatalogue;
import pchub.utils.DatabaseConnection;

public class ProductCatalogueDao {
    private final Connection connection;

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
                    
                    try (ResultSet rs = getIdStmt.executeQuery()) {
                        if (rs.next()) {
                            catalogue.setCatalogueID(rs.getString("catalogueID"));
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

    private String getNextCatalogueId() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(catalogueID, 5) AS UNSIGNED)) as max_id FROM product_catalogue";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int nextId = 1;
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (!rs.wasNull()) {
                    nextId = maxId + 1;
                }
            }
            
            return String.format("CATL%03d", nextId);
        }
    }

    public boolean update(ProductCatalogue catalogue) throws SQLException {
        String sql = "UPDATE product_catalogue SET name = ?, description = ?, startDate = ?, endDate = ? WHERE catalogueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, catalogue.getName());
            stmt.setString(2, catalogue.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(catalogue.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(catalogue.getEndDate()));
            stmt.setString(5, catalogue.getCatalogueID());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String catalogueID) throws SQLException {
        String sql = "DELETE FROM product_catalogue WHERE catalogueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, catalogueID);
            return stmt.executeUpdate() > 0;
        }
    }

    public ProductCatalogue findById(String catalogueID) throws SQLException {
        String sql = "SELECT * FROM product_catalogue WHERE catalogueID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, catalogueID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCatalogue(rs);
                }
            }
        }
        return null;
    }

    public ProductCatalogue[] findAll() throws SQLException {
        String sql = "SELECT * FROM product_catalogue";
        List<ProductCatalogue> catalogues = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                catalogues.add(mapResultSetToCatalogue(rs));
            }
        }
        return catalogues.toArray(new ProductCatalogue[0]);
    }

    private ProductCatalogue mapResultSetToCatalogue(ResultSet rs) throws SQLException {
        ProductCatalogue catalogue = new ProductCatalogue();
        catalogue.setCatalogueID(rs.getString("catalogueID"));
        catalogue.setName(rs.getString("name"));
        catalogue.setDescription(rs.getString("description"));
        catalogue.setStartDate(rs.getTimestamp("startDate").toLocalDateTime());
        catalogue.setEndDate(rs.getTimestamp("endDate").toLocalDateTime());
        return catalogue;
    }
} 