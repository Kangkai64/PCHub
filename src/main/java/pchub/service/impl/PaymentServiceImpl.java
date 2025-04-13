package pchub.service.impl;

package com.pchub.service.impl;

import pchub.dao.PaymentDao;
import pchub.dao.impl.PaymentDaoImpl;
import pchub.service.PaymentService;
import pchub.model.PaymentMethod;

import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    private PaymentDao paymentDao;

    public PaymentServiceImpl() {
        this.paymentDao = new PaymentDaoImpl();
    }

    @Override
    public List<pchub.model.PaymentMethod> getPaymentMethodsByUser(int userId) {
        return paymentDao.findByUserId(userId);
    }

    @Override
    public pchub.model.PaymentMethod getPaymentMethodById(int paymentId) {
        return paymentDao.findById(paymentId);
    }

    @Override
    public boolean addPaymentMethod(pchub.model.PaymentMethod paymentMethod) {
        return paymentDao.save(paymentMethod);
    }

    @Override
    public boolean updatePaymentMethod(pchub.model.PaymentMethod paymentMethod) {
        return paymentDao.update(paymentMethod);
    }

    @Override
    public boolean deletePaymentMethod(int paymentId) {
        return paymentDao.delete(paymentId);
    }
}
