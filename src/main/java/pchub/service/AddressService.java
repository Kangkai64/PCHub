package pchub.service;

import pchub.dao.AddressDao;
import pchub.dao.impl.AddressDaoImpl;
import pchub.model.Address;

import java.util.List;

public class AddressService {
    private AddressDao addressDao;

    public AddressService() {
        this.addressDao = new AddressDaoImpl();
    }

    public List<pchub.model.Address> getAddressesByUser(int userId) {
        return addressDao.findByUserId(userId);
    }

    public pchub.model.Address getAddressById(int addressId) {
        return addressDao.findById(addressId);
    }

    public boolean addAddress(Address address) {
        return addressDao.save(address);
    }

    public boolean updateAddress(pchub.model.Address address) {
        return addressDao.update(address);
    }

    public boolean deleteAddress(int addressId) {
        return addressDao.delete(addressId);
    }
}