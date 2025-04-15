package pchub.utils;

/**
 * Utility class for handling password hashing and verification
 * using the BCrypt algorithm.
 */
public class PasswordUtils {

    /**
     * Default log rounds for BCrypt hashing.
     * This controls the computational complexity.
     * Higher values are more secure but slower.
     */
    private static final int DEFAULT_LOG_ROUNDS = 12;

    /**
     * Hashes a password using BCrypt with a randomly generated salt.
     *
     * @param password The plaintext password to hash
     * @return The BCrypt hashed password including salt and parameters
     */
    public static String hashPassword(String password) {
        // Generate a salt with the default strength
        String salt = BCrypt.gensalt(DEFAULT_LOG_ROUNDS);

        // Hash the password with the generated salt
        return BCrypt.hashpw(password, salt);
    }

    /**
     * Hashes a password using BCrypt with a specified log rounds value.
     *
     * @param password The plaintext password to hash
     * @param logRounds The computational complexity parameter (4-30)
     * @return The BCrypt hashed password including salt and parameters
     */
    public static String hashPassword(String password, int logRounds) {
        if (logRounds < 4 || logRounds > 30) {
            throw new IllegalArgumentException("Log rounds must be between 4 and 30");
        }

        // Generate a salt with the specified strength
        String salt = BCrypt.gensalt(logRounds);

        // Hash the password with the generated salt
        return BCrypt.hashpw(password, salt);
    }

    /**
     * Verifies a plaintext password against a previously hashed one.
     *
     * @param password The plaintext password to verify
     * @param storedHash The previously hashed password
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }
}