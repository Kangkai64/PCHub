package pchub.service;

import pchub.model.User;

import java.util.List;

public interface UserService {
    User authenticateUser(String username, String password);
    boolean registerUser(User user);
    User getUserById(String userId);
    List<User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUser(String userId);
    boolean updatePassword(String userId, String oldPassword, String newPassword);
}
