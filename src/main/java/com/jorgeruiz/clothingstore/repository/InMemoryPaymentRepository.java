package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Debt;
import com.jorgeruiz.clothingstore.model.Payment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPaymentRepository implements PaymentRepository{

    private final Map<Integer, Payment> payments = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Payment save(Payment payment) {
        int newId = idGenerator.getAndIncrement();
        Payment newPayment = new Payment(
                newId,
            payment.getAmount(),
            payment.getDebt()
        );
        payments.put(newId, newPayment);
        return newPayment;
    }

    @Override
    public Optional<Payment> findById(int id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public List<Payment> findAll() {
        return List.copyOf(payments.values());
    }
}
