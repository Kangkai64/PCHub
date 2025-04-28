package pchub.dao;

import pchub.model.Product;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductDao extends DaoTemplate<Product> {
    @Override
    public Product findById(String productID) throws SQLException {
        String sql = "SELECT * FROM product WHERE productID = ?";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, productID);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error finding product by ID: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        return null;
    }

    public Product[] findAll() throws SQLException {
        Product[] products = new Product[100];
        String sql = "SELECT * FROM product";
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int index = 0;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }

            statement = conn.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                products[index] = mapResultSet(resultSet);
                index++;
            }
        } catch (SQLException e) {
            System.err.println("Error finding all products: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        return products;
    }

    public Product[] findByCategory(String category) throws SQLException {
        Product[] products = new Product[100];
        String sql = "SELECT * FROM product WHERE category = ?";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int index = 0;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, category);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                products[index] = mapResultSet(resultSet);
                index++;
            }
        } catch (SQLException e) {
            System.err.println("Error finding products by category: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        return products;
    }

    @Override
    public boolean insert(Product product) {
        String sql = "INSERT INTO product (productID, name, description, brand, category, unitPrice, currentQuantity, specifications) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, product.getProductID());
            preparedStatement.setString(2, product.getName());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setString(4, product.getBrand());
            preparedStatement.setString(5, product.getCategory());
            preparedStatement.setDouble(6, product.getUnitPrice());
            preparedStatement.setInt(7, product.getCurrentQuantity());
            preparedStatement.setString(8, product.getSpecifications());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saving product: " + e.getMessage());
            return false;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE product SET name = ?, description = ?, brand = ?, category = ?, " +
                "unitPrice = ?, currentQuantity = ?, specifications = ? WHERE productID = ?";
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setString(3, product.getBrand());
            preparedStatement.setString(4, product.getCategory());
            preparedStatement.setDouble(5, product.getUnitPrice());
            preparedStatement.setInt(6, product.getCurrentQuantity());
            preparedStatement.setString(7, product.getSpecifications());
            preparedStatement.setString(8, product.getProductID());

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean delete(String productID) throws SQLException {
        String sql = "DELETE FROM product WHERE productID = ?";
        Connection conn = null; 
        PreparedStatement preparedStatement = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Failed to establish database connection");
            }

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, productID);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
    }

    @Override
    public Product mapResultSet(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setProductID(resultSet.getString("productID"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setBrand(resultSet.getString("brand"));
        product.setCategory(resultSet.getString("category"));
        product.setUnitPrice(resultSet.getDouble("unitPrice"));
        product.setCurrentQuantity(resultSet.getInt("currentQuantity"));
        product.setSpecifications(resultSet.getString("specifications"));
        return product;
    }
}