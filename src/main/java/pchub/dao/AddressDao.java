package pchub.dao;

import pchub.model.Address;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressDao extends DaoTemplate<Address> {

    @Override
    public boolean insert(Address address) throws SQLException {
        String sql = "INSERT INTO shipping_address (customerID, street, city, state, zipCode, country) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, address.getUserId());
            statement.setString(2, address.getStreet());
            statement.setString(3, address.getCity());
            statement.setString(4, address.getState());
            statement.setString(5, address.getZipCode());
            statement.setString(6, address.getCountry());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                // Get the last inserted shipping address ID using a separate query
                String getLastIdSql = "SELECT shipping_addressID FROM shipping_address WHERE customerID = ? ORDER BY shipping_addressID DESC LIMIT 1";
                try (PreparedStatement getLastIdStmt = connection.prepareStatement(getLastIdSql)) {
                    getLastIdStmt.setString(1, address.getUserId());

                    try (ResultSet resultSet = getLastIdStmt.executeQuery()) {
                        if (resultSet.next()) {
                            String addressId = resultSet.getString("shipping_addressID");
                            address.setAddressId(addressId);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding address: " + e.getMessage());
            throw e;
        }
        return false;
    }

    @Override
    public boolean update(Address address) throws SQLException {
        String sql = "UPDATE shipping_address SET street = ?, city = ?, state = ?, zipCode = ?, country = ? " +
                    "WHERE shipping_addressID = ? AND customerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, address.getStreet());
            statement.setString(2, address.getCity());
            statement.setString(3, address.getState());
            statement.setString(4, address.getZipCode());
            statement.setString(5, address.getCountry());
            statement.setString(6, address.getAddressId());
            statement.setString(7, address.getUserId());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String addressId) throws SQLException {
        String sql = "DELETE FROM shipping_address WHERE shipping_addressID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, addressId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public Address findById(String addressId) throws SQLException {
        String sql = "SELECT * FROM shipping_address WHERE shipping_addressID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, addressId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSet(resultSet);
                }
                return null;
            }
        }
    }

    public Address[] findByUserId(String userId) throws SQLException {
        String countSql = "SELECT COUNT(*) AS count FROM shipping_address WHERE customerID = ?";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement countStatement = connection.prepareStatement(countSql)) {
            countStatement.setString(1, userId);

            try (ResultSet countResult = countStatement.executeQuery()) {
                if (countResult.next()) {
                    count = countResult.getInt("count");
                }
            }
        }

        String sql = "SELECT * FROM shipping_address WHERE customerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            int index = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                Address[] addresses = new Address[count];
                while (resultSet.next()) {
                    addresses[index] = mapResultSet(resultSet);
                    index++;
                }
                return addresses;
            }
        }
    }

    @Override
    protected Address mapResultSet(ResultSet resultSet) throws SQLException {
        Address address = new Address();
        address.setAddressId(resultSet.getString("shipping_addressID"));
        address.setUserId(resultSet.getString("customerID"));
        address.setStreet(resultSet.getString("street"));
        address.setCity(resultSet.getString("city"));
        address.setState(resultSet.getString("state"));
        address.setZipCode(resultSet.getString("zipCode"));
        address.setCountry(resultSet.getString("country"));
        return address;
    }
} 