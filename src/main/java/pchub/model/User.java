package pchub.model;

import pchub.model.enums.UserRole;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

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
     * @param userId The unique identifier for the user
     * @param username The username of the user
     * @param email The email address of the user
     * @param password The password of the user
     * @param fullName The full name of the user
     * @param phone The phone number of the user
     * @param role The role of the user
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public User(String userId, String username, String email, String password, 
               String fullName, String phone, UserRole role) {
        setUserId(userId);
        setUsername(username);
        setEmail(email);
        setPassword(password);
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
}
