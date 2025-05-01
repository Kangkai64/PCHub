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
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int index = 0;

        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                throw new SQLException("Failed to establish database connection");
            }

            statement = connection.createStatement();
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
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        return products;
    }

    public Product[] findByCategory(String product_categoryID) throws SQLException {
        Product[] products = new Product[100];
        String sql = "SELECT * FROM product WHERE product_categoryID = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int index = 0;

        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                throw new SQLException("Failed to establish database connection");
            }

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, product_categoryID);
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
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }

        return products;
    }

    @Override
    public boolean insert(Product product) {
        String sql = "INSERT INTO product (name, description, brand, product_categoryID, unitPrice, currentQuantity, specifications) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            if (connection == null) {
                throw new SQLException("Failed to establish database connection");
            }

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setString(3, product.getBrand());
            preparedStatement.setString(4, product.getCategory());
            preparedStatement.setDouble(5, product.getUnitPrice());
            preparedStatement.setInt(6, product.getCurrentQuantity());
            preparedStatement.setString(7, product.getSpecifications());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Get the last inserted product ID using a separate query
                String getLastIdSql = "SELECT productID FROM `product` ORDER BY productID DESC LIMIT 1";
                try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                    try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                        if (resultSet.next()) {
                            String productID = resultSet.getString("productID");
                            product.setProductID(productID);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving product: " + e.getMessage());
            return false;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
        return false;
    }

    @Override
    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE product SET name = ?, description = ?, brand = ?, product_categoryID = ?, " +
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
    protected Product mapResultSet(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setProductID(resultSet.getString("productID"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setBrand(resultSet.getString("brand"));
        product.setCategory(resultSet.getString("product_categoryID"));
        product.setUnitPrice(resultSet.getDouble("unitPrice"));
        product.setCurrentQuantity(resultSet.getInt("currentQuantity"));
        product.setSpecifications(resultSet.getString("specifications"));
        return product;
    }
}