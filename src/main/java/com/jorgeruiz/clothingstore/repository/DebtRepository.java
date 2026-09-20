package com.jorgeruiz.clothingstore.repository;


import com.jorgeruiz.clothingstore.model.Debt;

import java.util.List;
import java.util.Optional;

public interface DebtRepository {
    Debt save(Debt debt);

    Optional<Debt> findById(int id);

    List<Debt> findAll();
}