package com.jorgeruiz.clothingstore.service;

import com.jorgeruiz.clothingstore.exception.EntityNotFoundException;
import com.jorgeruiz.clothingstore.model.Customer;
import com.jorgeruiz.clothingstore.model.Debt;
import com.jorgeruiz.clothingstore.model.DebtStates;
import com.jorgeruiz.clothingstore.repository.DebtRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class DebtService {
    private final DebtRepository debtRepository;
    private final PaymentService paymentService;

    private static final BigDecimal HIGH_DEBT_VALUE = BigDecimal.valueOf(500000);

    public DebtService(DebtRepository debtRepository, PaymentService paymentService) {
        this.debtRepository = debtRepository;
        this.paymentService = paymentService;
    }

    public Debt addDebt(Debt debt){
        if(debt == null){
            throw new IllegalArgumentException("La deuda no puede ser nula!");
        }
        return debtRepository.save(debt);
    }

    public Debt getDebtById(int id){
        return debtRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("No se encontró la deuda con ID: " + id));
    }

    public List<Debt> getAllDebts(){
        return debtRepository.findAll();
    }

    public boolean hasHighDebt(String document){
        Collection<Debt> allDebts = getAllDebts();
        for(Debt debt : allDebts){
            Customer c = debt.getCustomer();
            if(c.getDocument().equals(document)){
                BigDecimal totalPaid = paymentService.calcTotalPaid(debt);
                if(debt.getState(totalPaid) == DebtStates.PENDIENTE){
                    BigDecimal outstanding = debt.calcOutstanding(totalPaid);
                    if(outstanding.compareTo(HIGH_DEBT_VALUE) > 0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Debt getOldestDebt(String document){
        Collection<Debt> allDebts = getAllDebts();
        Debt oldest = null;
        for(Debt debt : allDebts){
            Customer c = debt.getCustomer();
            if(c.getDocument().equals(document)){
                BigDecimal totalPaid = paymentService.calcTotalPaid(debt);
                if(debt.getState(totalPaid) == DebtStates.PENDIENTE){
                    if(oldest == null || debt.getDate().isBefore(oldest.getDate())){
                        oldest = debt;
                    }
                }
            }
        }
        if(oldest == null){
            throw new EntityNotFoundException("El cliente no tiene deudas pendientes!");
        }
        return oldest;
    }
}
