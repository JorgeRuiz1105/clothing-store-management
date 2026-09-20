package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(int id);

    List<Payment> findAll();
}
