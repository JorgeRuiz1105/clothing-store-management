package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Article;
import com.jorgeruiz.clothingstore.model.ArticleCategories;
import com.jorgeruiz.clothingstore.model.size.Size;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseArticleRepository implements ArticleRepository{

    @Override
    public Article save(Article article) {
        String insertSQL = "INSERT INTO article (name, size, color, price, quantity, category) VALUES (?, ?, ?, ?, ?, ?)";
        String updateSQL = "UPDATE article SET name = ?, size = ?, color = ?, price = ?, quantity = ?, category = ? WHERE id = ?";

        Article savedArticle = article;

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try {
                if (article.getId() == 0) {
                    try (PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, article.getName());
                        stmt.setString(2, article.getSize().getRepresentation());
                        stmt.setString(3, article.getColor());
                        stmt.setLong(4, article.getPrice().longValueExact());
                        stmt.setInt(5, article.getQuantity());
                        stmt.setString(6, article.getCategory().name());

                        stmt.executeUpdate();

                        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int newId = generatedKeys.getInt(1);
                                savedArticle = new Article(newId, article.getName(), article.getSize(),
                                        article.getColor(), article.getPrice(),
                                        article.getQuantity(), article.getCategory());
                            }else {
                                throw new SQLException("No se pudo obtener el ID autogenerado del articulo.");
                            }
                        }
                    }
                } else {
                    try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                        stmt.setString(1, article.getName());
                        stmt.setString(2, article.getSize().getRepresentation());
                        stmt.setString(3, article.getColor());
                        stmt.setLong(4, article.getPrice().longValueExact());
                        stmt.setInt(5, article.getQuantity());
                        stmt.setString(6, article.getCategory().name());
                        stmt.setInt(7, article.getId());

                        stmt.executeUpdate();
                    }
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al guardar el articulo: " + e.getMessage());
        }

        return savedArticle;
    }

    @Override
    public Optional<Article> findById(int id){
        String findSQL = "SELECT * FROM article WHERE id = ?";

        try(Connection conn = DatabaseConnection.connect()){
            try(PreparedStatement stmt = conn.prepareStatement(findSQL)){
                stmt.setInt(1, id);
                try(ResultSet rs = stmt.executeQuery()){
                    if(rs.next()){
                        String name = rs.getString("name");
                        ArticleCategories category = ArticleCategories.valueOf(rs.getString("category"));
                        Size size = category.createSize(rs.getString("size"));
                        String color = rs.getString("color");
                        BigDecimal price = BigDecimal.valueOf(rs.getLong("price"));
                        int quantity = rs.getInt("quantity");
                        return Optional.of(new Article(id, name, size, color, price, quantity, category));
                    }
                }
            }
        }catch (SQLException e){
            System.out.println("Ocurrio un error al buscar el articulo: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Article> findAll() {
        String findAllSQL = "SELECT * FROM article";
        List<Article> articles = new ArrayList<>();

        try(Connection conn = DatabaseConnection.connect()){
            try(PreparedStatement stmt = conn.prepareStatement(findAllSQL);
                ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    ArticleCategories category = ArticleCategories.valueOf(rs.getString("category"));
                    Size size = category.createSize(rs.getString("size"));
                    String color = rs.getString("color");
                    BigDecimal price = BigDecimal.valueOf(rs.getLong("price"));
                    int quantity = rs.getInt("quantity");

                    Article article;
                    article = new Article(id, name, size, color, price, quantity, category);
                    articles.add(article);
                }
            }
        }catch (SQLException e){
            System.out.println("Ocurrio un error al buscar los articulos: " + e.getMessage());
        }
        return articles;
    }

    @Override
    public void deleteById(int id) {
        String deleteSQL = "DELETE FROM article WHERE id = ?";

        try(Connection conn = DatabaseConnection.connect()){

            conn.setAutoCommit(false);

            try{
                try (PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
                    stmt.setInt(1,id);

                    stmt.executeUpdate();
                }
                conn.commit();
            }catch (SQLException e){
                conn.rollback();
                throw e;
            }
        }catch (SQLException e){
            System.out.println("Ocurrio un error al eliminar el articulo: " + e.getMessage());
        }
    }
}
