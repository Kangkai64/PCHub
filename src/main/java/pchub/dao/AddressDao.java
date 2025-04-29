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
        String sql = "INSERT INTO shipping_address (shipping_addressID, customerID, street, city, state, zipCode, country) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, address.getAddressId());
            statement.setString(2, address.getUserId());
            statement.setString(3, address.getStreet());
            statement.setString(4, address.getCity());
            statement.setString(5, address.getState());
            statement.setString(6, address.getZipCode());
            statement.setString(7, address.getCountry());

            return statement.executeUpdate() > 0;
        }
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
        String sql = "SELECT * FROM shipping_address WHERE customerID = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            int index = 0;
            try (ResultSet resultSet = statement.executeQuery()) {
                Address[] addresses = new Address[10];
                while (resultSet.next()) {
                    addresses[index] = mapResultSet(resultSet);
                    index++;
                }
                return addresses;
            }
        }
    }

    @Override
    public Address mapResultSet(ResultSet resultSet) throws SQLException {
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