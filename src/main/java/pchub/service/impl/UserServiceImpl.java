package pchub.service.impl;

import pchub.dao.UserDao;
import pchub.dao.impl.UserDaoImpl;
import pchub.model.User;
import pchub.service.UserService;
import pchub.utils.PasswordUtils;

import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public User authenticateUser(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
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

    @Override
    public User getUserById(String userId) {
        return userDao.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public boolean updateUser(User user) {
        // If updating email, check that it's not already in use
        User existingUserWithEmail = userDao.findByEmail(user.getEmail());
        if (existingUserWithEmail != null && existingUserWithEmail.getUserId() != user.getUserId()) {
            return false;
        }

        return userDao.updateUser(user);
    }

    @Override
    public boolean deleteUser(String userId) {
        return userDao.deleteUser(userId);
    }

    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        pchub.model.User user = userDao.findById(userId);
        if (user != null && PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
            return userDao.updateUser(user);
        }
        return false;
    }
}
