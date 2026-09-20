package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Sale;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SaleRepository {
    Sale save(Sale sale);

    Optional<Sale> findById(int id);

    List<Sale> findAll();
}
