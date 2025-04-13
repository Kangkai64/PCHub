package pchub.service;

import pchub.model.PaymentMethod;
import pchub.model.PaymentMethod;

import java.util.List;

public interface PaymentService {
    List<pchub.model.PaymentMethod> getPaymentMethodsByUser(int userId);
    pchub.model.PaymentMethod getPaymentMethodById(int paymentId);
    boolean addPaymentMethod(pchub.model.PaymentMethod paymentMethod);
    boolean updatePaymentMethod(pchub.model.PaymentMethod paymentMethod);
    boolean deletePaymentMethod(int paymentId);
}