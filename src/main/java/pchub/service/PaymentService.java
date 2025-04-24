package pchub.service;

import pchub.dao.PaymentDao;
import pchub.dao.PaymentMethodDao;
import pchub.model.PaymentMethod;
import pchub.model.enums.PaymentStatus;
import pchub.model.enums.PaymentType;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing payments and payment methods in the PC Hub system.
 * This class provides methods for payment processing and payment method management.
 */
public class PaymentService {
    private final PaymentDao paymentDao;
    private final PaymentMethodDao paymentMethodDao;

    /**
     * Default constructor
     */
    public PaymentService() {
        this.paymentDao = new PaymentDao();
        this.paymentMethodDao = new PaymentMethodDao();
    }

    /**
     * Retrieves all payment methods
     * @return List of all payment methods
     */
    public List<PaymentMethod> getAllPaymentMethods() {
        try {
            return paymentMethodDao.getAllPaymentMethods();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve payment methods: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a payment method by its ID
     * @param paymentMethodId The ID of the payment method
     * @return The payment method if found, null otherwise
     * @throws IllegalArgumentException if paymentMethodId is invalid
     */
    public PaymentMethod getPaymentMethodById(String paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method ID cannot be null or empty");
        }

        try {
            return paymentMethodDao.getPaymentMethodById(paymentMethodId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a new payment method
     * @param name The name of the payment method
     * @param type The type of payment method
     * @param description The description of the payment method
     * @return true if the payment method was added successfully, false otherwise
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public boolean addPaymentMethod(String name, PaymentType type, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        try {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setName(name.trim());
            paymentMethod.setType(type);
            paymentMethod.setDescription(description.trim());
            paymentMethod.setAddedDate(LocalDate.now());
            return paymentMethodDao.save(paymentMethod);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing payment method
     * @param paymentMethodId The ID of the payment method to update
     * @param name The new name of the payment method
     * @param type The new type of payment method
     * @param description The new description of the payment method
     * @return true if the payment method was updated successfully, false otherwise
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public boolean updatePaymentMethod(String paymentMethodId, String name, PaymentType type, String description) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        try {
            PaymentMethod paymentMethod = paymentMethodDao.getPaymentMethodById(paymentMethodId.trim());
            if (paymentMethod == null) {
                return false;
            }

            paymentMethod.setName(name.trim());
            paymentMethod.setType(type);
            paymentMethod.setDescription(description.trim());
            return paymentMethodDao.update(paymentMethod);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a payment method
     * @param paymentMethodId The ID of the payment method to delete
     * @return true if the payment method was deleted successfully, false otherwise
     * @throws IllegalArgumentException if paymentMethodId is invalid
     */
    public boolean deletePaymentMethod(String paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method ID cannot be null or empty");
        }

        try {
            return paymentMethodDao.delete(paymentMethodId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Processes a payment
     * @param paymentId The ID of the payment to process
     * @return true if the payment was processed successfully, false otherwise
     * @throws IllegalArgumentException if paymentId is invalid
     */
    public boolean processPayment(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be null or empty");
        }

        try {
            return paymentDao.updatePaymentStatus(paymentId.trim(), PaymentStatus.COMPLETED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    /**
     * Refunds a payment
     * @param paymentId The ID of the payment to refund
     * @return true if the refund was processed successfully, false otherwise
     * @throws IllegalArgumentException if paymentId is invalid
     */
    public boolean refundPayment(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be null or empty");
        }

        try {
            return paymentDao.updatePaymentStatus(paymentId.trim(), PaymentStatus.REFUNDED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to refund payment: " + e.getMessage(), e);
        }
    }
}
