package pchub.model;

import pchub.dao.BillDao;
import pchub.dao.PaymentMethodDao;
import pchub.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.time.LocalDate;

/**
 * Represents a bill in the PC Hub system.
 * This class contains information about a bill including its ID, order details,
 * customer information, items, financial details, and payment status.
 */
public class Bill {
    private String billId;
    private String orderId;
    private String customerId;
    private String customerName;
    private Address shippingAddress;
    private OrderItem[] items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private Date issueDate;
    private PaymentStatus paymentStatus;
    private String transactionId;

    private static final int MAX_ITEMS = 30;
    private static final BillDao billDao = new BillDao();
    private static final PaymentMethodDao paymentMethodDao = new PaymentMethodDao();

    /**
     * Default constructor
     */
    public Bill() {
        this.issueDate = new Date();
        this.items = new OrderItem[MAX_ITEMS];
        this.subtotal = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.shippingCost = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    /**
     * Parameterized constructor
     * @param billId The unique identifier for the bill
     * @param orderId The ID of the associated order
     * @param customerId The ID of the customer
     * @param customerName The name of the customer
     * @param shippingAddress The shipping address for the order
     * @param paymentMethod The payment method used
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Bill(String billId, String orderId, String customerId, String customerName,
                Address shippingAddress, PaymentMethod paymentMethod, OrderItem[] items,
                BigDecimal subtotal, BigDecimal tax, BigDecimal shippingCost, BigDecimal totalAmount, PaymentStatus paymentStatus) {
        this.billId = billId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.issueDate = new Date();
        this.items = items;
        this.subtotal = subtotal;
        this.tax = tax;
        this.shippingCost = shippingCost;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        this.customerId = customerId.trim();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        this.customerName = customerName.trim();
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address cannot be null");
        }
        this.shippingAddress = shippingAddress;
    }

    public OrderItem[] getItems() {
        return items;
    }

    public void setItems(OrderItem[] items) {
        if (items == null) {
            throw new IllegalArgumentException("Items array cannot be null");
        }
        if (items.length > MAX_ITEMS) {
            throw new IllegalArgumentException("Items array cannot exceed " + MAX_ITEMS + " items");
        }
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        if (subtotal == null) {
            throw new IllegalArgumentException("Subtotal cannot be null");
        }
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        if (tax == null) {
            throw new IllegalArgumentException("Tax cannot be null");
        }
        if (tax.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax cannot be negative");
        }
        this.tax = tax;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        if (shippingCost == null) {
            throw new IllegalArgumentException("Shipping cost cannot be null");
        }
        if (shippingCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Shipping cost cannot be negative");
        }
        this.shippingCost = shippingCost;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null) {
            throw new IllegalArgumentException("Total amount cannot be null");
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        this.totalAmount = totalAmount;
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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        if (issueDate == null) {
            throw new IllegalArgumentException("Issue date cannot be null");
        }
        this.issueDate = issueDate;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            throw new IllegalArgumentException("Payment status cannot be null or empty");
        }
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Adds an item to the bill
     * @param item The order item to add
     * @throws IllegalArgumentException if the item is null
     * @throws IllegalStateException if the items array is full
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                items[i] = item;
                return;
            }
        }
        throw new IllegalStateException("Cannot add more items: bill is full");
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", shippingAddress=" + shippingAddress +
                ", items=" + Arrays.toString(items) +
                ", subtotal=" + subtotal +
                ", tax=" + tax +
                ", shippingCost=" + shippingCost +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                ", issueDate=" + issueDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return billId.equals(bill.billId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId);
    }

    /**
     * Retrieves all payment methods
     * @return Array of all payment methods
     */
    public static PaymentMethod[] getAllPaymentMethods() {
        try {
            return paymentMethodDao.findAll();
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
    public static PaymentMethod getPaymentMethodById(String paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method ID cannot be null or empty");
        }

        try {
            return paymentMethodDao.findById(paymentMethodId.trim());
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Adds a new payment method
     * @param name The name of the payment method
     * @param description The description of the payment method
     * @return true if the payment method was added successfully, false otherwise
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static boolean addPaymentMethod(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        try {
            PaymentMethod paymentMethod = new PaymentMethod();
            paymentMethod.setName(name.trim());
            paymentMethod.setDescription(description.trim());
            paymentMethod.setAddedDate(LocalDate.now());
            return paymentMethodDao.insert(paymentMethod);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add payment method: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing payment method
     * @param paymentMethodId The ID of the payment method to update
     * @param name The new name of the payment method
     * @param description The new description of the payment method
     * @return true if the payment method was updated successfully, false otherwise
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static boolean updatePaymentMethod(String paymentMethodId, String name, String description) {
        if (paymentMethodId == null || paymentMethodId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }

        try {
            PaymentMethod paymentMethod = paymentMethodDao.findById(paymentMethodId.trim());
            if (paymentMethod == null) {
                return false;
            }

            paymentMethod.setName(name.trim());
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
    public static boolean deletePaymentMethod(String paymentMethodId) {
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
     * Processes a bill
     * @param bill The bill to process
     * @return true if the bill was processed successfully, false otherwise
     * @throws IllegalArgumentException if bill is invalid
     */
    public static boolean processPayment(Bill bill) {
        if (bill == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        try {
            return billDao.update(bill);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process bill: " + e.getMessage(), e);
        }
    }

    /**
     * Refunds a payment
     * @param bill The payment to refund
     * @return true if the refund was processed successfully, false otherwise
     * @throws IllegalArgumentException if payment is invalid
     */
    public static boolean refundPayment(Bill bill) {
        if (bill == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        try {
            return billDao.update(bill);
        } catch (Exception e) {
            throw new RuntimeException("Failed to refund payment: " + e.getMessage(), e);
        }
    }
}