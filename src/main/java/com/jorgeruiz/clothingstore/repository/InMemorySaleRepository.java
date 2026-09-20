package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Sale;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemorySaleRepository implements SaleRepository{

    private final Map<Integer, Sale> sales = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Sale save(Sale sale) {
        int newId = idGenerator.getAndIncrement();
        Sale newSale = new Sale(
                newId,
                sale.getType(),
                sale.getCustomer(),
                sale.getItemSaleList()
        );
        sales.put(newId, newSale);
        return newSale;
    }

    @Override
    public Optional<Sale> findById(int id) {
        return Optional.ofNullable(sales.get(id));
    }

    @Override
    public List<Sale> findAll() {
        return List.copyOf(sales.values());
    }
}
