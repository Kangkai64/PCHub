package pchub.service;

import pchub.model.PaymentMethod;

import java.util.List;

public interface PaymentService {
    List<PaymentMethod> getPaymentMethodsByUser(String userId);
    PaymentMethod getPaymentMethodById(String paymentId);
    boolean addPaymentMethod(PaymentMethod paymentMethod);
    boolean updatePaymentMethod(PaymentMethod paymentMethod);
    boolean deletePaymentMethod(String paymentId);
}