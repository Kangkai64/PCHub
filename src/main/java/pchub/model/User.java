package pchub.model;

import pchub.model.enums.UserRole;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;
import pchub.dao.UserDao;
import pchub.utils.PasswordUtils;
import java.sql.SQLException;

/**
 * Represents a user in the PC Hub system.
 * This class contains information about a user including their ID, username, email,
 * password, registration details, and personal information.
 */
public class User {
    private String userId;
    private String username;
    private String email;
    private String password;
    private Date registrationDate;
    private Date lastLogin;
    private String status;
    private String fullName;
    private String phone;
    private UserRole role;
    private static final UserDao userDao = new UserDao();

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    // Phone number validation pattern (basic)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{2}-[0-9]{7,8}$");

    /**
     * Default constructor
     */
    public User() {
        this.role = UserRole.CUSTOMER; // Default role
        this.registrationDate = new Date();
        this.status = "ACTIVE";
    }

    /**
     * Parameterized constructor
     *
     * @param userId           The unique identifier for the user
     * @param username         The username of the user
     * @param email            The email address of the user
     * @param password         The password of the user
     * @param registrationDate The registration date of the user
     * @param lastLogin        The last login time of the user
     * @param status           The account status of the user (ACTIVE, INACTIVE, BANNED)
     * @param fullName         The full name of the user
     * @param phone            The phone number of the user
     * @param role             The role of the user
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public User(String userId, String username, String email, String password,
               Date registrationDate, Date lastLogin, String status,
                String fullName, String phone, UserRole role) {
        setUserId(userId);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setRegistrationDate(registrationDate);
        setLastLogin(lastLogin);
        setStatus(status);
        setFullName(fullName);
        setPhone(phone);
        setRole(role);
        this.registrationDate = new Date();
        this.status = "ACTIVE";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        this.userId = userId.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.length() < 3 || username.length() > 20) {
            throw new IllegalArgumentException("Username must be between 3 and 20 characters");
        }
        this.username = username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = password.trim();
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        if (registrationDate == null) {
            throw new IllegalArgumentException("Registration date cannot be null");
        }
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        this.status = status.trim();
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }
        this.fullName = fullName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.phone = phone.trim();
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", registrationDate=" + registrationDate +
                ", lastLogin=" + lastLogin +
                ", status='" + status + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    /**
     * Authenticates a user with username and password
     * @param username The username to authenticate
     * @param password The password to verify
     * @return The authenticated user if successful, null otherwise
     * @throws IllegalArgumentException if username or password is null or empty
     */
    public static User authenticateUser(String username, String password) {
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
    public static boolean registerUser(User user) {
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

            return userDao.insert(user);
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
    public static User getUserById(String userId) {
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
    public static User[] getAllUsers() throws SQLException {
        User[] users = userDao.findAll();
        return users;
    }

    /**
     * Updates an existing user
     * @param user The user to update
     * @return true if update was successful, false otherwise
     * @throws IllegalArgumentException if user is null or invalid
     */
    public static boolean updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        try {
            // If updating email, check that it's not already in use
            User existingUserWithEmail = userDao.findByEmail(user.getEmail());
            if (existingUserWithEmail != null && !Objects.equals(existingUserWithEmail.getUserId(), user.getUserId())) {
                throw new IllegalStateException("Email already in use by another user");
            }

            return userDao.update(user);
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
    public static boolean deleteUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        try {
            return userDao.delete(userId.trim());
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
    public static boolean updatePassword(String userId, String oldPassword, String newPassword) {
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
                return userDao.update(user);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update password: " + e.getMessage(), e);
        }
    }
}