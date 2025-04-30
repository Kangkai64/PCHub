package pchub.model;

import pchub.model.enums.UserRole;

import java.util.Date;

public class Customer extends User {
    Address address;

    public Customer(User user) {
        super(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRegistrationDate(),
                user.getLastLogin(), user.getStatus(), user.getFullName(), user.getPhone(), user.getRole());
        address = new Address();
    }

    public Customer(Address address) {
        super();
        this.address = address;
    }
}