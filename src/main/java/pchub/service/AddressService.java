package pchub.service;

import java.util.List;

public interface AddressService {
    List<pchub.model.Address> getAddressesByUser(int userId);
    pchub.model.Address getAddressById(int addressId);
    boolean addAddress(pchub.model.Address address);
    boolean updateAddress(pchub.model.Address address);
    boolean deleteAddress(int addressId);
}