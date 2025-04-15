package pchub.service.impl;

import pchub.dao.AddressDao;
import pchub.dao.impl.AddressDaoImpl;
import pchub.model.Address;
import pchub.service.AddressService;

import java.util.List;

public class AddressServiceImpl implements AddressService {
    private AddressDao addressDao;

    public AddressServiceImpl() {
        this.addressDao = new AddressDaoImpl();
    }

    @Override
    public List<pchub.model.Address> getAddressesByUser(int userId) {
        return addressDao.findByUserId(userId);
    }

    @Override
    public pchub.model.Address getAddressById(int addressId) {
        return addressDao.findById(addressId);
    }

    @Override
    public boolean addAddress(Address address) {
        return addressDao.save(address);
    }

    @Override
    public boolean updateAddress(pchub.model.Address address) {
        return addressDao.update(address);
    }

    @Override
    public boolean deleteAddress(int addressId) {
        return addressDao.delete(addressId);
    }
}