package pchub.service;

import pchub.dao.UserDao;
import pchub.model.User;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public void updateAllPasswords(String password) {
        try {
            userDao.updateAllPasswords(password);
        } catch (Exception e) {
            throw new RuntimeException("Error updating passwords: " + e.getMessage(), e);
        }
    }

    public User authenticateUser(String username, String password) {
        try {
            return User.authenticateUser(username, password);
        } catch (Exception e) {
            throw new RuntimeException("Error authenticating user: " + e.getMessage(), e);
        }
    }

    public boolean registerUser(User user) {
        try {
            return User.registerUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Error registering user: " + e.getMessage(), e);
        }
    }
}