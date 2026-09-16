package com.jorgeruiz.clothingstore.exception;

public class PaymentExceedsDebtException extends RuntimeException {
    public PaymentExceedsDebtException(String message) {
        super(message);
    }
}
