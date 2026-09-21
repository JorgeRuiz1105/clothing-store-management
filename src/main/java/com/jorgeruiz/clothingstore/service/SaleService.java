package com.jorgeruiz.clothingstore.service;

import com.jorgeruiz.clothingstore.exception.EntityNotFoundException;
import com.jorgeruiz.clothingstore.exception.InsufficientStockException;
import com.jorgeruiz.clothingstore.model.*;
import com.jorgeruiz.clothingstore.repository.SaleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaleService {
    private final SaleRepository saleRepository;
    private final InventoryService inventoryService;
    private final CustomerService customerService;
    private final DebtService debtService;

    public SaleService(SaleRepository saleRepository, InventoryService inventoryService, CustomerService customerService, DebtService debtService) {
        this.saleRepository = saleRepository;
        this.inventoryService = inventoryService;
        this.customerService = customerService;
        this.debtService = debtService;
    }

    public Sale addSale(SaleTypes type, String document, Map<Integer, Integer> items){
        if(items == null || items.isEmpty()){
            throw new IllegalArgumentException("La lista de items esta vacia!");
        }

        Customer customer = customerService.getCustomerByDocument(document);
        List<ItemSale> itemSaleList = new ArrayList<>();

        for(Map.Entry<Integer,Integer> entry : items.entrySet()){
            int articleId = entry.getKey();
            int articleAmount = entry.getValue();
            Article article = inventoryService.getArticleById(articleId);
            ItemSale itemSale = new ItemSale(article, articleAmount);
            itemSaleList.add(itemSale);
        }

        for(ItemSale itemSale : itemSaleList){
            Article article = itemSale.getArticle();
            if(!article.hasEnoughStock(itemSale.getAmount())){
                throw new InsufficientStockException("No hay suficiente stock para: " + article.getName());
            }
        }

        for(ItemSale itemSale : itemSaleList){
            Article article = itemSale.getArticle();
            inventoryService.inventoryOutflow(article.getId(), itemSale.getAmount());
        }

        Sale sale = new Sale(type, customer, itemSaleList);

        if(type == SaleTypes.FIADA){
            debtService.addDebt(new Debt(customer, sale));
        }

        return saleRepository.save(sale);
    }

    public Sale getSaleById(int id){
        return saleRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("No se encontró la venta con ID: " + id));
    }

    public List<Sale> getAllSales(){
        return saleRepository.findAll();
    }
}
