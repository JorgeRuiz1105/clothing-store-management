package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);

    Optional<Customer> findByDocument(String document);

    List<Customer> findAll();

    void deleteByDocument(String document);
}
