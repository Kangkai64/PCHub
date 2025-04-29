package pchub.model;

import pchub.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

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
                Address shippingAddress, PaymentMethod paymentMethod) {
        setBillId(billId);
        setOrderId(orderId);
        setCustomerId(customerId);
        setCustomerName(customerName);
        setShippingAddress(shippingAddress);
        setPaymentMethod(paymentMethod);
        this.issueDate = new Date();
        this.items = new OrderItem[MAX_ITEMS];
        this.subtotal = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.shippingCost = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
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
}