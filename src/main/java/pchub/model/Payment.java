package pchub.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import pchub.model.enums.PaymentStatus;

/**
 * Represents a payment in the PC Hub system.
 * This class contains information about a payment including its bill ID, order ID,
 * amount, payment method, transaction details, status, and date.
 */
public class Payment {
    private String billId;
    private String orderId;
    private BigDecimal amount;
    private String paymentMethodId;
    private String transactionId;
    private PaymentStatus status;
    private LocalDateTime date;

    /**
     * Default constructor
     */
    public Payment() {
        this.status = PaymentStatus.PENDING;
        this.date = LocalDateTime.now();
    }

    /**
     * Parameterized constructor
     * @param billId The unique identifier for the bill
     * @param orderId The ID of the associated order
     * @param amount The payment amount
     * @param paymentMethodId The ID of the payment method used
     * @param transactionId The transaction ID from the payment processor
     * @param status The status of the payment
     * @param date The date and time of the payment
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Payment(String billId, String orderId, BigDecimal amount, String paymentMethodId,
                  String transactionId, PaymentStatus status, LocalDateTime date) {
        setBillId(billId);
        setOrderId(orderId);
        setAmount(amount);
        setPaymentMethodId(paymentMethodId);
        setTransactionId(transactionId);
        setStatus(status);
        setDate(date);
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        if (billId == null || billId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill ID cannot be null or empty");
        }
        this.billId = billId.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        this.orderId = orderId.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
        this.transactionId = transactionId.trim();
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("Payment date cannot be null");
        }
        this.date = date;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "billId='" + billId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", paymentMethodId='" + paymentMethodId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", status=" + status +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return billId.equals(payment.billId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId);
    }
}