package com.jorgeruiz.clothingstore.service;

import com.jorgeruiz.clothingstore.model.Customer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportService {
    private final DebtService debtService;
    private final CustomerService customerService;

    public ReportService(DebtService debtService, CustomerService customerService) {
        this.debtService = debtService;
        this.customerService = customerService;
    }

    public List<Customer> getHighDebtCustomers(){
        Collection<Customer> allCustomers = customerService.getAllCustomers();
        List<Customer> highDebtCustomers = new ArrayList<>();
        for(Customer customer : allCustomers){
            if(debtService.hasHighDebt(customer.getDocument())){
                highDebtCustomers.add(customer);
            }
        }
        highDebtCustomers.sort(null);
        return highDebtCustomers;
    }
}
