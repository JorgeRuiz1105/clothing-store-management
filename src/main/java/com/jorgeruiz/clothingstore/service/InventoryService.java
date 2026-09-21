package com.jorgeruiz.clothingstore.service;

import com.jorgeruiz.clothingstore.exception.EntityNotFoundException;
import com.jorgeruiz.clothingstore.model.Article;
import com.jorgeruiz.clothingstore.repository.ArticleRepository;

import java.util.List;

public class InventoryService {

    private final ArticleRepository articleRepository;

    public InventoryService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article addArticle(Article article){
        if(article == null){
            throw new IllegalArgumentException("El articulo no puede ser nulo!");
        }
        return articleRepository.save(article);
    }

    public Article getArticleById(int id){
        return articleRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("No se encontró el articulo con ID: " + id));
    }

    public List<Article> getAllArticles(){
        return articleRepository.findAll();
    }

    public void deleteArticle(int id){
        getArticleById(id);
        articleRepository.deleteById(id);
    }

    public void inventoryIntake(int id, int quantity){
        Article article = getArticleById(id);
        article.addQuantity(quantity);
        articleRepository.save(article);
    }

    public void inventoryOutflow(int id, int quantity){
        Article article = getArticleById(id);
        article.reduceQuantity(quantity);
        articleRepository.save(article);
    }
}
