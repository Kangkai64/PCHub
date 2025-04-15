package pchub.service;

import pchub.model.Address;

import java.util.List;

public interface AddressService {
    List<pchub.model.Address> getAddressesByUser(String userId);
    Address getAddressById(int addressId);
    boolean addAddress(Address address);
    boolean updateAddress(Address address);
    boolean deleteAddress(int addressId);
}