package pchub.model;

import pchub.model.enums.PaymentType;

public class PaymentMethod {
    private int id;
    private int userId;
    private PaymentType type;
    private String details;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }


    public void setDetails(String details) {
        this.details = details;
    }

    public String getDisplayName() {
        switch (type) {
            case CREDIT_CARD:
                return "Credit Card: " + details;
            case PAYPAL:
                return "PayPal: " + details;
            case BANK_TRANSFER:
                return "Bank Transfer";
            default:
                return "Cash on Delivery";
        }
    }
}
