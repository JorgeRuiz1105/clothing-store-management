package com.jorgeruiz.clothingstore.service;

import com.jorgeruiz.clothingstore.exception.EntityNotFoundException;
import com.jorgeruiz.clothingstore.model.Customer;
import com.jorgeruiz.clothingstore.repository.CustomerRepository;

import java.util.List;

public class CustomerService {
    private final CustomerRepository customerRepository;


    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer){
        if(customer == null){
            throw new IllegalArgumentException("El cliente no puede ser nulo!");
        }
        return customerRepository.save(customer);
    }

    public Customer getCustomerByDocument(String document){
        return customerRepository.findByDocument(document).orElseThrow(()
                -> new EntityNotFoundException("No se encontró el cliente con cedula: " + document));
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public void deleteCustomer(String document){
        getCustomerByDocument(document);
        customerRepository.deleteByDocument(document);
    }
}
