package pchub.model;

import pchub.model.enums.PaymentType;

import java.time.LocalDate;

public class PaymentMethod {
    private String paymentMethodId;
    private String name;
    private PaymentType type;
    private String description;
    private LocalDate addedDate;

    // Getters and setters
    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public String getDisplayName() {
        switch (type) {
            case CREDIT_CARD:
                return "Credit Card: " + description;
            case PAYPAL:
                return "PayPal: " + description;
            case BANK_TRANSFER:
                return "Bank Transfer";
            default:
                return "Cash on Delivery";
        }
    }
}
