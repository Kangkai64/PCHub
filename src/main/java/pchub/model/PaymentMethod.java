package pchub.model;

import java.time.LocalDate;

public class PaymentMethod {
    private String paymentMethodId;
    private String name;
    private String description;
    private LocalDate addedDate;

    // Default constructor
    public PaymentMethod() {
        this.addedDate = LocalDate.now();
    }

    // Default constructor
    public PaymentMethod(String name) {
        this.addedDate = LocalDate.now();
        this.name = name;
    }

    // Parameterized constructor
    public PaymentMethod( String name, String description) {
        this.name = name;
        this.description = description;
        this.addedDate = LocalDate.now();
    }

    // Full parameterized constructor
    public PaymentMethod(String paymentMethodId, String name, String description, LocalDate addedDate) {
        this.paymentMethodId = paymentMethodId;
        this.name = name;
        this.description = description;
        this.addedDate = addedDate;
    }

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

    @Override
    public String toString() {
        return "Payment Method ID: " + paymentMethodId + ", Name: " + name + ", Description: " +
                description + ", Added Date: " + addedDate;
    }
}
