package pchub.model;

import java.time.LocalDateTime;

public class UserPaymentMethod {
    private String userPaymentMethodId;
    private String userId;
    private String paymentMethodId;
    private String details;
    private boolean isDefault;
    private LocalDateTime addedDate;

    public String getUserPaymentMethodId() {
        return userPaymentMethodId;
    }

    public void setUserPaymentMethodId(String userPaymentMethodId) {
        if (userPaymentMethodId == null || userPaymentMethodId.trim().isEmpty()) {
            throw new IllegalArgumentException("User payment method ID cannot be null or empty");
        }
        this.userPaymentMethodId = userPaymentMethodId.trim();
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

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method ID cannot be null or empty");
        }
        this.paymentMethodId = paymentMethodId.trim();
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        if (addedDate == null) {
            throw new IllegalArgumentException("Added date cannot be null");
        }
        this.addedDate = addedDate;
    }
} 