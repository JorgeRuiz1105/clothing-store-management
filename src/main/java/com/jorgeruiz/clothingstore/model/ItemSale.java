package com.jorgeruiz.clothingstore.model;

import java.math.BigDecimal;

public class ItemSale {
    private int id;
    private Article article;
    private int amount;

    public ItemSale(int id, Article article, int amount) {
        this.id = id;
        this.article = validateArticle(article);
        this.amount = validateAmount(amount);
    }

    public int getId() {
        return id;
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
}
