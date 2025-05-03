package pchub.model;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import pchub.dao.TransactionDao;
import pchub.model.enums.PaymentStatus;

/**
 * Represents a payment transaction in the PC Hub system.
 * This class contains information about a transaction including its ID, bill details,
 * amount, payment method, status, and timestamps.
 */
public class Transaction {
    private String transactionId;
    private Bill bill;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private Date transactionDate;
    private Date lastModifiedDate;
    private String description;

    private static final TransactionDao TRANSACTION_DAO = new TransactionDao();

    /**
     * Default constructor
     */
    public Transaction() {
        this.transactionDate = new Date();
        this.lastModifiedDate = new Date();
        this.status = PaymentStatus.PENDING;
    }

    /**
     * Constructor with bill
     * @param bill The bill associated with this transaction
     */
    public Transaction(Bill bill) {
        this();
        if (bill == null) {
            throw new IllegalArgumentException("Bill cannot be null");
        }
        this.bill = bill;
        this.amount = bill.getTotalAmount();
        this.paymentMethod = bill.getPaymentMethod();
        this.status = PaymentStatus.PAID;
        this.transactionDate = new Date();
        this.lastModifiedDate = new Date();
        this.description = "Payment for Order #" + bill.getOrder().getOrderId();
        try {
            this.insert();
        } catch (SQLException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
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

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        if (bill == null) {
            throw new IllegalArgumentException("Bill cannot be null");
        }
        this.bill = bill;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
        this.lastModifiedDate = new Date();
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }
        this.transactionDate = transactionDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        if (lastModifiedDate == null) {
            throw new IllegalArgumentException("Last modified date cannot be null");
        }
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Save this transaction to the database
     * @return true if the save operation was successful
     * @throws SQLException if a database access error occurs
     */
    public boolean insert() throws SQLException {
        if (this.transactionId == null || this.transactionId.trim().isEmpty()) {
            return TRANSACTION_DAO.insert(this);
        } else {
            return TRANSACTION_DAO.update(this);
        }
    }

    /**
     * Load a transaction from the database by ID
     * @param transactionId the ID of the transaction to load
     * @return the loaded Transaction object or null if not found
     * @throws SQLException if a database access error occurs
     */
    public static Transaction load(String transactionId) throws SQLException {
        return TRANSACTION_DAO.findById(transactionId);
    }

    /**
     * Delete this transaction from the database
     * @return true if the deletion was successful
     * @throws SQLException if a database access error occurs
     */
    public boolean delete() throws SQLException {
        if (this.transactionId == null || this.transactionId.trim().isEmpty()) {
            throw new IllegalStateException("Cannot delete a transaction that hasn't been saved");
        }
        return TRANSACTION_DAO.delete(this.transactionId);
    }

    /**
     * Find all transactions for a specific bill
     * @param billId the bill ID
     * @return an array of Transaction objects
     * @throws SQLException if a database access error occurs
     */
    public static Transaction[] findByBillId(String billId) throws SQLException {
        return TRANSACTION_DAO.findByBillId(billId);
    }

    /**
     * Find transactions by their payment status
     * @param status the payment status
     * @return an array of Transaction objects
     * @throws SQLException if a database access error occurs
     */
    public static Transaction[] findByStatus(PaymentStatus status) throws SQLException {
        return TRANSACTION_DAO.findByStatus(status);
    }

    /**
     * Find transactions by payment method ID
     * @param paymentMethodId the payment method ID
     * @return an array of Transaction objects
     * @throws SQLException if a database access error occurs
     */
    public static Transaction[] findByPaymentMethodId(String paymentMethodId) throws SQLException {
        return TRANSACTION_DAO.findByPaymentMethodId(paymentMethodId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", billId='" + (bill != null ? bill.getBillId() : "null") + '\'' +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", transactionDate=" + transactionDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId.equals(that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}