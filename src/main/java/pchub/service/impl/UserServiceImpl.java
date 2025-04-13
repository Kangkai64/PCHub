package pchub.service.impl;

import pchub.dao.UserDao;
import pchub.dao.impl.UserDaoImpl;
import pchub.service.UserService;
import pchub.utils.PasswordUtils;

import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public pchub.model.User authenticateUser(String username, String password) {
        pchub.model.User user = userDao.findByUsername(username);
        if (user != null && pchub.PasswordUtils.verifyPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean registerUser(pchub.model.User user) {
        // Check if username or email already exists
        if (userDao.findByUsername(user.getUsername()) != null) {
            return false;
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            return false;
        }

        // Hash password
        String hashedPassword = pchub.PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        return userDao.save(user);
    }

    @Override
    public pchub.model.User getUserById(int userId) {
        return userDao.findById(userId);
    }

    @Override
    public List<pchub.model.User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public boolean updateUser(pchub.model.User user) {
        // If updating email, check that it's not already in use
        pchub.model.User existingUserWithEmail = userDao.findByEmail(user.getEmail());
        if (existingUserWithEmail != null && existingUserWithEmail.getId() != user.getId()) {
            return false;
        }

        return userDao.update(user);
    }

    @Override
    public boolean deleteUser(int userId) {
        return userDao.delete(userId);
    }

    @Override
    public boolean updatePassword(int userId, String oldPassword, String newPassword) {
        pchub.model.User user = userDao.findById(userId);
        if (user != null && pchub.PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
            String hashedNewPassword = pchub.PasswordUtils.hashPassword(newPassword);
            user.setPassword(hashedNewPassword);
            return userDao.update(user);
        }
        return false;
    }
}
