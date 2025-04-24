package pchub.service;

import pchub.dao.AddressDao;
import pchub.model.Address;

import java.sql.SQLException;

/**
 * Service class for managing addresses in the PC Hub system.
 * This class provides methods for address management operations.
 */
public class AddressService {
    private final AddressDao addressDao;

    /**
     * Default constructor
     */
    public AddressService() {
        this.addressDao = new AddressDao();
    }

    /**
     * Retrieves all addresses for a user
     * @param userId The ID of the user
     * @return Array of addresses for the user
     * @throws IllegalArgumentException if userId is null or empty
     * @throws SQLException if there is a database error
     */
    public Address[] getAddressesByUser(String userId) throws SQLException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        try {
            return addressDao.findByUserId(userId.trim());
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve addresses: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves an address by its ID
     * @param addressId The ID of the address to retrieve
     * @return The address if found, null otherwise
     * @throws IllegalArgumentException if addressId is null or empty
     * @throws SQLException if there is a database error
     */
    public Address getAddressById(String addressId) throws SQLException {
        if (addressId == null || addressId.trim().isEmpty()) {
            throw new IllegalArgumentException("Address ID cannot be null or empty");
        }

        try {
            return addressDao.findById(addressId.trim());
        } catch (SQLException e) {
            throw new SQLException("Failed to retrieve address: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a new address
     * @param address The address to add
     * @return true if the address was added successfully, false otherwise
     * @throws IllegalArgumentException if address is null or invalid
     * @throws SQLException if there is a database error
     */
    public boolean addAddress(Address address) throws SQLException {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (address.getUserId() == null || address.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        try {
            return addressDao.save(address);
        } catch (SQLException e) {
            throw new SQLException("Failed to add address: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing address
     * @param address The address to update
     * @return true if the address was updated successfully, false otherwise
     * @throws IllegalArgumentException if address is null or invalid
     * @throws SQLException if there is a database error
     */
    public boolean updateAddress(Address address) throws SQLException {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (address.getAddressId() == null || address.getAddressId().trim().isEmpty()) {
            throw new IllegalArgumentException("Address ID cannot be null or empty");
        }

        try {
            return addressDao.update(address);
        } catch (SQLException e) {
            throw new SQLException("Failed to update address: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an address
     * @param addressId The ID of the address to delete
     * @return true if the address was deleted successfully, false otherwise
     * @throws IllegalArgumentException if addressId is null or empty
     * @throws SQLException if there is a database error
     */
    public boolean deleteAddress(String addressId) throws SQLException {
        if (addressId == null || addressId.trim().isEmpty()) {
            throw new IllegalArgumentException("Address ID cannot be null or empty");
        }

        try {
            return addressDao.delete(addressId.trim());
        } catch (SQLException e) {
            throw new SQLException("Failed to delete address: " + e.getMessage(), e);
        }
    }
}