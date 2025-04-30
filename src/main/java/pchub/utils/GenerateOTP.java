package pchub.utils;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GenerateOTP {
    private static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRY_MINUTES = 5;
    
    // Store OTPs with their expiration times - in production, use a proper database
    private Map<String, OTPData> otpStorage = new HashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // Inner class to store OTP with additional metadata
    private static class OTPData {
        String otp;
        long expiryTime;
        
        OTPData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
    
    // Generate and store OTP for a user identifier (email/phone)
    public String generateOTP(String userIdentifier) {
        // Generate random OTP
        String otp = generateRandomOTP();
        
        // Calculate expiry time
        long expiryTimeMillis = System.currentTimeMillis() + (OTP_EXPIRY_MINUTES * 60 * 1000);
        
        // Store OTP with expiry
        otpStorage.put(userIdentifier, new OTPData(otp, expiryTimeMillis));
        
        // Schedule removal after expiry
        scheduler.schedule(() -> {
            otpStorage.remove(userIdentifier);
        }, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        return otp;
    }
    
    // Generate random numeric OTP
    private String generateRandomOTP() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }
    
    // Verify the OTP submitted by user
    public boolean verifyOTP(String userIdentifier, String submittedOTP) {
        OTPData otpData = otpStorage.get(userIdentifier);
        
        if (otpData == null) {
            return false; // No OTP found for this user
        }
        
        if (System.currentTimeMillis() > otpData.expiryTime) {
            otpStorage.remove(userIdentifier);
            System.out.println("OTP has expired");
            return false; // OTP has expired
        }
        
        if (otpData.otp.equals(submittedOTP)) {
            // OTP is valid, remove it after successful verification (one-time use)
            otpStorage.remove(userIdentifier);
            return true;
        }
        
        System.out.println("Invalid OTP. Order has been cancelled.");
        return false; // Invalid OTP
    }
    
    // Get remaining time in seconds for an OTP
    public long getRemainingTimeSeconds(String userIdentifier) {
        OTPData otpData = otpStorage.get(userIdentifier);
        if (otpData == null) {
            return 0;
        }
        
        long remainingTimeMillis = otpData.expiryTime - System.currentTimeMillis();
        return remainingTimeMillis > 0 ? remainingTimeMillis / 1000 : 0;
    }
}
