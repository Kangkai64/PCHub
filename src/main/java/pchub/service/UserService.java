package pchub.service;

import pchub.dao.UserDao;
import pchub.model.User;
import pchub.utils.PasswordUtils;

import java.util.List;

public class UserService {
    private UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public User authenticateUser(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean registerUser(User user) {
        // Check if username or email already exists
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false;
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            return false;
        }

        return userDao.insertUser(user);
    }

    public User getUserById(String userId) {
        return userDao.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public boolean updateUser(User user) {
        // If updating email, check that it's not already in use
        User existingUserWithEmail = userDao.findByEmail(user.getEmail());
        if (existingUserWithEmail != null && existingUserWithEmail.getUserId() != user.getUserId()) {
            return false;
        }

        return userDao.updateUser(user);
    }

    public boolean deleteUser(String userId) {
        return userDao.deleteUser(userId);
    }

    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        pchub.model.User user = userDao.findById(userId);
        if (user != null && PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
            return userDao.updateUser(user);
        }
        return false;
    }
}
