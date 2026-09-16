package com.jorgeruiz.clothingstore.model;

import com.jorgeruiz.clothingstore.exception.InsufficientStockException;
import com.jorgeruiz.clothingstore.model.size.Size;

import java.math.BigDecimal;

public class Article {

    private int id;
    private String name;
    private Size size;
    private String color;
    private BigDecimal price;
    private int quantity;
    private ArticleCategories category;

    public Article(int id, String name, Size size, String color, BigDecimal price, int quantity, ArticleCategories category) {
        validateCategorySizeCorrelation(category,size);
        this.id = id;
        this.name = validateName(name);
        this.size = size;
        this.color = validateColor(color);
        this.price = validatePrice(price);
        this.quantity = validateQuantity(quantity);
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = validateName(name);
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        validateCategorySizeCorrelation(this.category, size);
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = validateColor(color);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if(price == null || price.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Ha introducido un valor de precio invalido!");
        }
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity(int quantity){
        if(quantity <= 0){
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a 0!");
        }
        if(this.quantity < quantity){
            throw new InsufficientStockException("Stock insuficiente en el inventario!");
        }
        this.quantity -= quantity;
    }

    public void addQuantity(int quantity){
        if(quantity <= 0){
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a 0!");
        }
        this.quantity += quantity;
    }

    public boolean hasEnoughStock(int quantity){
        return this.quantity >= quantity;
    }

    public ArticleCategories getCategory() {
        return category;
    }

    public void setCategory(ArticleCategories category) {
        validateCategorySizeCorrelation(category, this.size);
        this.category = category;
    }

    //auxiliar method to modify the article from the store
    public void updateSizeAndCategory(Size size, ArticleCategories category){
        validateCategorySizeCorrelation(category, size);
        this.category = category;
        this.size = size;
    }

    private void validateCategorySizeCorrelation(ArticleCategories category, Size size){
        if(!category.isValidSize(size)){
            throw new IllegalArgumentException("La talla no corresponde a la categoria del artículo!");
        }
    }

    private String validateName(String name){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Debe ingresar el nombre del artículo!");
        }
        return name;
    }

    private String validateColor(String color){
        if(color == null || color.isBlank()){
            throw new IllegalArgumentException("Debe ingresar el color del artículo!");
        }
        return color;
    }

    private BigDecimal validatePrice(BigDecimal price){
        if(price == null || price.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Ha introducido un valor de precio invalido!");
        }
        return price;
    }

    private int validateQuantity(int quantity){
        if(quantity < 0){
            throw new IllegalArgumentException("La cantidad por articulo debe ser mayor o igual 0!");
        }
        return quantity;
    }
}
