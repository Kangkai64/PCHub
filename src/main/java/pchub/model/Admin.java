package pchub.model;

public class Admin extends User {

    public Admin(User user) {
        super(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRegistrationDate(),
                user.getLastLogin(), user.getStatus(), user.getFullName(), user.getPhone(), user.getRole());
    }
}
