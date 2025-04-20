package pchub.service;

import pchub.dao.PaymentDao;
import pchub.dao.PaymentDao;
import pchub.model.PaymentMethod;

import java.util.List;

public class PaymentService {
    private PaymentDao paymentDao;

    public PaymentService() {
        this.paymentDao = new PaymentDao();
    }

    public List<pchub.model.PaymentMethod> getPaymentMethodsByUser(int userId) {
        return paymentDao.findByUserId(userId);
    }

    public PaymentMethod getPaymentMethodById(int paymentId) {
        return paymentDao.findById(paymentId);
    }

    public boolean addPaymentMethod(PaymentMethod paymentMethod) {
        return paymentDao.save(paymentMethod);
    }

    public boolean updatePaymentMethod(PaymentMethod paymentMethod) {
        return paymentDao.update(paymentMethod);
    }

    public boolean deletePaymentMethod(String paymentId) {
        return paymentDao.delete(paymentId);
    }
}
