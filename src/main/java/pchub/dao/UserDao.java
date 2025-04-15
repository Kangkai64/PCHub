package pchub.dao;

import pchub.model.User;
import java.util.List;

public interface UserDao {
    User findById(String userId);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
    boolean insertUser(User user);
    boolean updateUser(User user);
    boolean updateAllPasswords(String newPassword);
    boolean deleteUser(String userId);
}
