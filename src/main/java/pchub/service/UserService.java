package pchub.service;

import java.util.List;

public interface UserService {
    pchub.model.User authenticateUser(String username, String password);
    boolean registerUser(pchub.model.User user);
    pchub.model.User getUserById(int userId);
    List<pchub.model.User> getAllUsers();
    boolean updateUser(pchub.model.User user);
    boolean deleteUser(int userId);
    boolean updatePassword(int userId, String oldPassword, String newPassword);
}
