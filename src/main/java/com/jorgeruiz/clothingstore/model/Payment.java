package com.jorgeruiz.clothingstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private int id;
    private BigDecimal amount;
    private LocalDateTime date;
    private Debt debt;

    public Payment(int id, BigDecimal amount, Debt debt) {
        this.id = id;
        this.amount = validateAmount(amount);
        this.date = LocalDateTime.now();
        this.debt = validateDebt(debt);
    }

    public Payment(BigDecimal amount, Debt debt){
        this.amount = validateAmount(amount);
        this.date = LocalDateTime.now();
        this.debt = validateDebt(debt);
    }

    public Payment(int id, BigDecimal amount, Debt debt, LocalDateTime date){
        this.id = id;
        this.amount = validateAmount(amount);
        this.debt = validateDebt(debt);
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Debt getDebt() {
        return debt;
    }

    private BigDecimal validateAmount(BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Ha introducido un valor de abono invalido!");
        }
        return amount;
    }

    private Debt validateDebt(Debt debt){
        if(debt == null){
            throw new IllegalArgumentException("Debe especificar la deuda asociada al abono!");
        }
        return debt;
    }
}
