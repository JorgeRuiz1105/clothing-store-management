package com.jorgeruiz.clothingstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private int id;
    private SaleTypes type;
    private LocalDateTime date;
    private Customer customer;
    private List<ItemSale> itemSaleList;

    public Sale(int id, SaleTypes type, Customer customer, List<ItemSale> itemSaleList) {
        this.id = id;
        this.type = validateType(type);
        this.date = LocalDateTime.now();
        this.customer = validateCustomer(customer);
        this.itemSaleList = validateItemSaleList(itemSaleList);
    }

    public Sale(SaleTypes type, Customer customer, List<ItemSale> itemSaleList){
        this.type = validateType(type);
        this.date = LocalDateTime.now();
        this.customer = validateCustomer(customer);
        this.itemSaleList = validateItemSaleList(itemSaleList);
    }

    public int getId() {
        return id;
    }

    public SaleTypes getType() {
        return type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<ItemSale> getItemSaleList() {
        return this.itemSaleList;
    }

    public BigDecimal calcTotalPrice(){
        BigDecimal total = BigDecimal.valueOf(0);
        for(ItemSale itemSale : this.itemSaleList){
            total = total.add(itemSale.calcSubtotalPrice());
        }
        return total;
    }

    private SaleTypes validateType(SaleTypes type){
        if(type == null){
            throw new IllegalArgumentException("Debe ingresar el tipo de venta!");
        }
        return type;
    }

    private Customer validateCustomer(Customer customer){
        if(customer == null){
            throw new IllegalArgumentException("Debe ingresar el cliente asociado a la venta!");
        }
        return customer;
    }

    private List validateItemSaleList(List<ItemSale> itemSaleList){
        if(itemSaleList == null || itemSaleList.isEmpty()){
            throw new IllegalArgumentException("Debe agregar articulos a la venta!");
        }
        return List.copyOf(itemSaleList);
    }


}
