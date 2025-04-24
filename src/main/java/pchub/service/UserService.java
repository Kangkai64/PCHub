package pchub.service;

import pchub.dao.UserDao;
import pchub.model.User;
import pchub.utils.PasswordUtils;

import java.util.List;
import java.util.Objects;
import java.sql.SQLException;

/**
 * Service class for managing users in the PC Hub system.
 * This class provides methods for user authentication, registration, and management.
 */
public class UserService {
    private final UserDao userDao;

    /**
     * Default constructor
     */
    public UserService() {
        this.userDao = new UserDao();
    }

    /**
     * Authenticates a user with username and password
     * @param username The username to authenticate
     * @param password The password to verify
     * @return The authenticated user if successful, null otherwise
     * @throws IllegalArgumentException if username or password is null or empty
     */
    public User authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            User user = userDao.findByUsername(username.trim());
            if (user != null && PasswordUtils.verifyPassword(password.trim(), user.getPassword())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate user: " + e.getMessage(), e);
        }
    }

    /**
     * Registers a new user
     * @param user The user to register
     * @return true if registration was successful, false otherwise
     * @throws IllegalArgumentException if user is null or invalid
     */
    public boolean registerUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            // Check if username or email already exists
            if (userDao.findByUsername(user.getUsername()) != null) {
                throw new IllegalStateException("Username already exists");
            }
            if (userDao.findByEmail(user.getEmail()) != null) {
                throw new IllegalStateException("Email already exists");
            }

            return userDao.insertUser(user);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a user by their ID
     * @param userId The ID of the user to retrieve
     * @return The user if found, null otherwise
     * @throws IllegalArgumentException if userId is null or empty
     */
    public User getUserById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        try {
            return userDao.findById(userId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve user: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all users
     * @return List of all users
     */
    public User[] getAllUsers() throws SQLException {
        List<User> users = userDao.findAll();
        return users.toArray(new User[0]);
    }

    /**
     * Updates an existing user
     * @param user The user to update
     * @return true if update was successful, false otherwise
     * @throws IllegalArgumentException if user is null or invalid
     */
    public boolean updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            // If updating email, check that it's not already in use
            User existingUserWithEmail = userDao.findByEmail(user.getEmail());
            if (existingUserWithEmail != null && !Objects.equals(existingUserWithEmail.getUserId(), user.getUserId())) {
                throw new IllegalStateException("Email already in use by another user");
            }

            return userDao.updateUser(user);
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user
     * @param userId The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     * @throws IllegalArgumentException if userId is null or empty
     */
    public boolean deleteUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        try {
            return userDao.deleteUser(userId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates a user's password
     * @param userId The ID of the user
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if password was updated successfully, false otherwise
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Old password cannot be null or empty");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be null or empty");
        }

        try {
            User user = userDao.findById(userId.trim());
            if (user != null && PasswordUtils.verifyPassword(oldPassword.trim(), user.getPassword())) {
                user.setPassword(newPassword.trim());
                return userDao.updateUser(user);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password: " + e.getMessage(), e);
        }
    }
}
