package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Article;
import com.jorgeruiz.clothingstore.model.Customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryCustomerRepository implements CustomerRepository{
    private final Map<String, Customer> customers = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Customer save(Customer customer) {
        if(!customers.containsKey(customer.getDocument())){
            int newId = idGenerator.getAndIncrement();
            Customer savedCustomer = new Customer(
                    newId,
                    customer.getDocument(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getAddress()
            );
            customers.put(customer.getDocument(), savedCustomer);
            return savedCustomer;
        }
        customers.put(customer.getDocument(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findByDocument(String document) {
        return Optional.ofNullable(customers.get(document));
    }

    @Override
    public List<Customer> findAll() {
        return List.copyOf(customers.values());
    }

    @Override
    public void deleteByDocument(String document) {
        customers.remove(document);
    }
}
