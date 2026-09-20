package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Article;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    Article save(Article article);

    Optional<Article> findById(int id);

    List<Article> findAll();

    void deleteById(int id);
}
