package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Article;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryArticleRepository implements ArticleRepository{

    private final Map<Integer, Article> articles = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    @Override
    public Article save(Article article) {
        if(article.getId() == 0 || !articles.containsKey(article.getId())){
            int newId = idGenerator.getAndIncrement();
            Article savedArticle = new Article(
                    newId,
                    article.getName(),
                    article.getSize(),
                    article.getColor(),
                    article.getPrice(),
                    article.getQuantity(),
                    article.getCategory()
            );
            articles.put(newId, savedArticle);
            return savedArticle;
        }
        articles.put(article.getId(), article);
        return article;
    }

    @Override
    public Optional<Article> findById(int id) {
        return Optional.ofNullable(articles.get(id));
    }

    @Override
    public List<Article> findAll() {
        return List.copyOf(articles.values());
    }

    @Override
    public void deleteById(int id) {
        articles.remove(id);
    }
}
