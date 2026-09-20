package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Debt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryDebtRepository implements DebtRepository{
    private final Map<Integer, Debt> debts = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Debt save(Debt debt) {
        int newId = idGenerator.getAndIncrement();
        Debt newDebt = new Debt(
                newId,
                debt.getCustomer(),
                debt.getSale()
        );
        debts.put(newId, newDebt);
        return newDebt;
    }

    @Override
    public Optional<Debt> findById(int id) {
        return Optional.ofNullable(debts.get(id));
    }

    @Override
    public List<Debt> findAll() {
        return List.copyOf(debts.values());
    }
}
