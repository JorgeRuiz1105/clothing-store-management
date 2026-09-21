package com.jorgeruiz.clothingstore.service;

import com.jorgeruiz.clothingstore.exception.EntityNotFoundException;
import com.jorgeruiz.clothingstore.exception.PaymentExceedsDebtException;
import com.jorgeruiz.clothingstore.model.Debt;
import com.jorgeruiz.clothingstore.model.Payment;
import com.jorgeruiz.clothingstore.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment addPayment(Payment payment){
        if(payment == null){
            throw new IllegalArgumentException("El abono no puede ser nulo!");
        }
        BigDecimal outstanding = payment.getDebt().calcOutstanding(calcTotalPaid(payment.getDebt()));
        if(payment.getAmount().compareTo(outstanding) > 0){
            throw new PaymentExceedsDebtException("El abono no puede ser mayor a la cantidad que debe!");
        }
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(int id){
        return paymentRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("No se encontró el abono con ID: " + id));
    }

    public List<Payment> getAllPayments(){
        return paymentRepository.findAll();
    }

    public BigDecimal calcTotalPaid(Debt debt){
        Collection<Payment> allPayments = getAllPayments();
        BigDecimal totalPaid = BigDecimal.ZERO;
        for(Payment payment : allPayments){
            Debt paymentDebt = payment.getDebt();
            if(paymentDebt.equals(debt)){
                totalPaid = totalPaid.add(payment.getAmount());
            }
        }
        return totalPaid;
    }
}
