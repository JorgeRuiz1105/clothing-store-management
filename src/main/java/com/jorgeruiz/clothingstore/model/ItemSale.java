package com.jorgeruiz.clothingstore.model;

import java.math.BigDecimal;

public class ItemSale {
    private Article article;
    private int amount;
    private BigDecimal unitPrice;

    public ItemSale(Article article, int amount, BigDecimal unitPrice) {
        this.article = validateArticle(article);
        this.amount = validateAmount(amount);
        this.unitPrice = validateUnitPrice(unitPrice);
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = validateArticle(article);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = validateAmount(amount);
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = validateUnitPrice(unitPrice);
    }

    public BigDecimal calcSubtotalPrice(){
        return this.article.getPrice().multiply(BigDecimal.valueOf(this.amount));
    }

    private Article validateArticle(Article article){
        if(article == null){
            throw new IllegalArgumentException("Debe especificar el artículo a añadir!");
        }
        return article;
    }

    private int validateAmount(int amount){
        if(amount <= 0){
            throw new IllegalArgumentException("La cantidad por articulo debe ser mayor a 0!");
        }
        return amount;
    }

    private BigDecimal validateUnitPrice(BigDecimal unitPrice){
        if(unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Se ha introducido un valor de precio unitario invalido!");
        }
        return unitPrice;
    }
}
