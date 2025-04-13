package pchub.dao;

import pchub.model.User;
import java.util.List;

public interface UserDao {
    User findById(String id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
    boolean save(User user);
    boolean update(User user);
    boolean delete(String id);
}
