package com.jorgeruiz.clothingstore.model;

import java.math.BigDecimal;
    import java.time.LocalDateTime;

    public class Debt {
        private int id;
        private LocalDateTime date;
        private Customer customer;
        private Sale sale;

        public Debt(int id, Customer customer, Sale sale) {
            this.id = id;
            this.date = LocalDateTime.now();
            this.customer = validateCustomer(customer);
            this.sale = validateSale(sale);
        }

        public Debt(Customer customer, Sale sale){
            this.date = LocalDateTime.now();
            this.customer = validateCustomer(customer);
            this.sale = validateSale(sale);
        }

        public int getId() {
            return id;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public Customer getCustomer() {
            return customer;
        }

        public Sale getSale() {
            return sale;
        }

        public BigDecimal calcTotalAmount(){
            return this.sale.calcTotalPrice();
        }

        public BigDecimal calcOutstanding(BigDecimal totalPaid){
            return this.calcTotalAmount().subtract(totalPaid);
        }

        public DebtStates getState(BigDecimal totalPaid){
            if(this.calcOutstanding(totalPaid).compareTo(BigDecimal.ZERO) <= 0){
                return DebtStates.PAGADA;
            }
            return DebtStates.PENDIENTE;
        }

        private Customer validateCustomer(Customer customer){
            if(customer == null){
                throw new IllegalArgumentException("Debe especificar el cliente asociado a la deuda!");
            }
            return customer;
        }

        private Sale validateSale(Sale sale){
            if(sale == null){
                throw new IllegalArgumentException("Debe especificar la factura asociada a la deuda!");
            }
            return sale;
        }
    }
