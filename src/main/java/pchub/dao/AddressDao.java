package pchub.dao;

import pchub.model.Address;
import pchub.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing addresses in the database.
 * This class provides methods for CRUD operations on addresses.
 */
public class AddressDao {
    private final Connection connection;

    /**
     * Default constructor that initializes the database connection
     */
    public AddressDao() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Saves a new address to the database
     * @param address The address to save
     * @return true if the address was saved successfully, false otherwise
     * @throws SQLException if there is a database error
     */
    public boolean save(Address address) throws SQLException {
        String sql = "INSERT INTO addresses (address_id, user_id, street, city, state, zip_code, country) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

    /**
     * Updates an existing address in the database
     * @param address The address to update
     * @return true if the address was updated successfully, false otherwise
     * @throws SQLException if there is a database error
     */
    public boolean update(Address address) throws SQLException {
        String sql = "UPDATE addresses SET street = ?, city = ?, state = ?, zip_code = ?, country = ? " +
                    "WHERE address_id = ? AND user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

    /**
     * Deletes an address from the database
     * @param addressId The ID of the address to delete
     * @return true if the address was deleted successfully, false otherwise
     * @throws SQLException if there is a database error
     */
    public boolean delete(String addressId) throws SQLException {
        String sql = "DELETE FROM addresses WHERE address_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, addressId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves an address by its ID
     * @param addressId The ID of the address to retrieve
     * @return The address if found, null otherwise
     * @throws SQLException if there is a database error
     */
    public Address findById(String addressId) throws SQLException {
        String sql = "SELECT * FROM addresses WHERE address_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, addressId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAddress(resultSet);
                }
                return null;
            }
        }
    }

    /**
     * Retrieves all addresses for a user
     * @param userId The ID of the user
     * @return Array of addresses for the user
     * @throws SQLException if there is a database error
     */
    public Address[] findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM addresses WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Address> addresses = new ArrayList<>();
                while (resultSet.next()) {
                    addresses.add(mapResultSetToAddress(resultSet));
                }
                return addresses.toArray(new Address[0]);
            }
        }
    }

    /**
     * Maps a ResultSet row to an Address object
     * @param resultSet The ResultSet containing the address data
     * @return An Address object
     * @throws SQLException if there is a database error
     */
    private Address mapResultSetToAddress(ResultSet resultSet) throws SQLException {
        Address address = new Address();
        address.setAddressId(resultSet.getString("address_id"));
        address.setUserId(resultSet.getString("user_id"));
        address.setStreet(resultSet.getString("street"));
        address.setCity(resultSet.getString("city"));
        address.setState(resultSet.getString("state"));
        address.setZipCode(resultSet.getString("zip_code"));
        address.setCountry(resultSet.getString("country"));
        return address;
    }
} 