package com.jorgeruiz.clothingstore.store;

import com.jorgeruiz.clothingstore.exception.EntityAlreadyExistsException;
import com.jorgeruiz.clothingstore.exception.EntityNotFoundException;
import com.jorgeruiz.clothingstore.exception.InsufficientStockException;
import com.jorgeruiz.clothingstore.exception.PaymentExceedsDebtException;
import com.jorgeruiz.clothingstore.model.*;
import com.jorgeruiz.clothingstore.model.size.Size;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Store {
    private Map<Integer,Article> inventory;
    private Map<String,Customer> customers;
    private Map<Integer,Sale> sales;
    private Map<Integer,Debt> debts;
    private Map<Integer,Payment> payments;

    private final AtomicInteger articleIdGenerator = new AtomicInteger(1);
    private final AtomicInteger customersIdGenerator = new AtomicInteger(1);
    private final AtomicInteger salesIdGenerator = new AtomicInteger(1);
    private final AtomicInteger itemSalesIdGenerator = new AtomicInteger(1);
    private final AtomicInteger debtsIdGenerator = new AtomicInteger(1);
    private final AtomicInteger paymentsIdGenerator = new AtomicInteger(1);

    private static final BigDecimal HIGH_DEBT_VALUE = BigDecimal.valueOf(500000);

    public Store() {
        this.inventory = new HashMap<>();
        this.customers = new HashMap<>();
        this.sales = new HashMap<>();
        this.debts = new HashMap<>();
        this.payments = new HashMap<>();
    }

    //Articles methods
    public void createArticle(String name, Size size, String color, BigDecimal price,
                              int quantity, ArticleCategories category){
        int id  = articleIdGenerator.getAndIncrement();
        Article article = new Article(id, name, size, color, price, quantity, category);
        inventory.put(id, article);
    }

    public void modifyArticle(int id, String name, Size size, String color, BigDecimal price,
                              ArticleCategories category){
        Article article = searchArticle(id);

        article.setName(name);
        article.setColor(color);
        article.setPrice(price);

        article.updateSizeAndCategory(size, category);
    }

    public Article searchArticle(int id){
        if(!inventory.containsKey(id)){
            throw new EntityNotFoundException("El articulo con id: " + id +  ", no existe!");
        }
        return inventory.get(id);
    }

    public Collection<Article> searchAllArticles(){
        return List.copyOf(inventory.values());
    }

    public void deleteArticle(int id){
        searchArticle(id);
        inventory.remove(id);
    }

    public void inventoryIntake(int id, int quantity){
        Article article = searchArticle(id);
        article.addQuantity(quantity);
    }

    public void inventoryOutflow(int id, int quantity){
        Article article = searchArticle(id);
        article.reduceQuantity(quantity);
    }

    //Customers methods
    public void createCustomer(String document, String name, String phone, String address){
        if(customers.containsKey(document)){
            throw new EntityAlreadyExistsException("El cliente que intenta agregar ya existe!");
        }
        int id  = customersIdGenerator.getAndIncrement();
        Customer customer = new Customer(id, document, name, phone, address);
        customers.put(document, customer);
    }

    public void modifyCustomer(String document, String name, String phone, String address){
        Customer customer  = searchCustomer(document);

        customer.setName(name);
        customer.setPhone(phone);
        customer.setAddress(address);
    }

    public Customer searchCustomer(String document){
        if(!customers.containsKey(document)){
            throw new EntityNotFoundException("El cliente con cedula: " + document +  ", no existe!");
        }
        return customers.get(document);
    }

    public Collection<Customer> searchAllCustomers(){
        return List.copyOf(customers.values());
    }

    public void deleteCustomer(String document){
        searchCustomer(document);
        customers.remove(document);
    }

    //Sales methods
    public Sale recordSale(SaleTypes type, String document, Map<Integer, Integer> items){
        if(items == null || items.isEmpty()){
            throw new IllegalArgumentException("La lista de items esta vacia!");
        }

        Customer customer = searchCustomer(document);
        int saleId  = salesIdGenerator.getAndIncrement();
        List<ItemSale> itemSaleList = new ArrayList<>();

        if(hasHighDebt(document)){
            System.out.println("¡ALERTA! el cliente actualmente tiene deudas altas activas.");
        }

        for(Map.Entry<Integer,Integer> entry : items.entrySet()){
            int articleId = entry.getKey();
            int articleAmount = entry.getValue();
            int itemSaleId = itemSalesIdGenerator.getAndIncrement();
            Article article = searchArticle(articleId);
            ItemSale itemSale = new ItemSale(itemSaleId, article, articleAmount);
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
            article.reduceQuantity(itemSale.getAmount());
        }

        Sale sale = new Sale(saleId, type, customer, itemSaleList);
        sales.put(saleId, sale);

        if(type == SaleTypes.FIADA){
            recordDebt(customer, sale);
        }

        return sale;
    }

    public Sale searchSale(int id){
        if(!sales.containsKey(id)){
            throw new EntityNotFoundException("La factura de venta con id: " + id +  ", no existe!");
        }
        return sales.get(id);
    }

    public Collection<Sale> searchAllSales(){
        return List.copyOf(sales.values());
    }

    //Debts methods
    private void recordDebt(Customer customer, Sale sale){
        int id  = debtsIdGenerator.getAndIncrement();
        Debt debt = new Debt(id, customer, sale);
        debts.put(id, debt);
    }

    public Debt searchDebt(int id){
        if(!debts.containsKey(id)){
            throw new EntityNotFoundException("La deuda con id: " + id +  ", no existe!");
        }
        return debts.get(id);
    }

    public Collection<Debt> searchAllDebts(){
        return List.copyOf(debts.values());
    }

    public Debt getOldestDebt(String document){
        Collection<Debt> allDebts = searchAllDebts();
        Debt oldest = null;
        for(Debt debt : allDebts){
            Customer c = debt.getCustomer();
            if(c.getDocument().equals(document)){
                BigDecimal totalPaid = calcTotalPaid(debt);
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

    public boolean hasHighDebt(String document){
        Collection<Debt> allDebts = searchAllDebts();
        for(Debt debt : allDebts){
            Customer c = debt.getCustomer();
            if(c.getDocument().equals(document)){
                BigDecimal totalPaid = calcTotalPaid(debt);
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

    //Payments methods
    public Payment recordPayment(BigDecimal amount, String document){
        Debt debt = getOldestDebt(document);
        int id  = paymentsIdGenerator.getAndIncrement();
        BigDecimal outstanding = debt.calcOutstanding(calcTotalPaid(debt));
        if(amount.compareTo(outstanding) > 0){
            throw new PaymentExceedsDebtException("El abono no puede ser mayor a la cantidad que debe!");
        }

        Payment payment = new Payment(id,amount,debt);
        payments.put(id, payment);
        return payment;
    }

    public Payment searchPayment(int id){
        if(!payments.containsKey(id)){
            throw new EntityNotFoundException("El abono (pago) con id: " + id +  ", no existe!");
        }
        return payments.get(id);
    }

    public Collection<Payment> searchAllPayments(){
        return List.copyOf(payments.values());
    }

    private BigDecimal calcTotalPaid(Debt debt){
        Collection<Payment> allPayments = searchAllPayments();
        BigDecimal totalPaid = BigDecimal.ZERO;
        for(Payment payment : allPayments){
            Debt paymentDebt = payment.getDebt();
            if(paymentDebt == debt){
                totalPaid = totalPaid.add(payment.getAmount());
            }
        }
        return totalPaid;
    }
}
