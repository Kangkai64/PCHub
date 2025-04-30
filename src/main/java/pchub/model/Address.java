package pchub.model;

import pchub.dao.AddressDao;
import java.sql.SQLException;

public class Address {
    private String addressId;
    private String userId;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    private static final AddressDao addressDao = new AddressDao();

    public Address() {
    }

    public Address(String addressId, String userId, String street, String city, String state, String zipCode, String country) {
        this.addressId = addressId;
        this.userId = userId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    // Getters and setters
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFormattedAddress() {
        return street + ", " + city + ", " + state + " " + zipCode + ", " + country;
    }

    /**
     * Retrieves all addresses for a user
     * @param userId The ID of the user
     * @return Array of addresses for the user
     * @throws IllegalArgumentException if userId is null or empty
     * @throws SQLException if there is a database error
     */
    public static Address[] getAddressesByUser(String userId) throws SQLException {
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
    public static Address getAddressById(String addressId) throws SQLException {
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
    public static boolean addAddress(Address address) throws SQLException {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (address.getUserId() == null || address.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        try {
            return addressDao.insert(address);
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
    public static boolean updateAddress(Address address) throws SQLException {
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
    public static boolean deleteAddress(String addressId) throws SQLException {
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