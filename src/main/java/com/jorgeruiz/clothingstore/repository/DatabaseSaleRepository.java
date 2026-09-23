package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.*;
import com.jorgeruiz.clothingstore.model.size.Size;

import java.util.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseSaleRepository implements SaleRepository{
    @Override
    public Sale save(Sale sale) {
        String insertSaleSQL = "INSERT INTO sale (type, date, customer_id) VALUES (?, ?, ?)";
        String insertItemSQL = "INSERT INTO item_sale (sale_id, article_id, amount, unit_price) VALUES (?, ?, ?, ?)";

        Sale savedSale = sale;

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try {
                int saleId;

                try (PreparedStatement stmtSale = conn.prepareStatement(insertSaleSQL, Statement.RETURN_GENERATED_KEYS)) {
                    stmtSale.setString(1, sale.getType().name());
                    stmtSale.setString(2, sale.getDate().toString());
                    stmtSale.setInt(3, sale.getCustomer().getId());

                    stmtSale.executeUpdate();

                    try (ResultSet generatedKeys = stmtSale.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            saleId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("No se pudo obtener el ID de la venta.");
                        }
                    }
                }

                try (PreparedStatement stmtItem = conn.prepareStatement(insertItemSQL)) {
                    for (ItemSale item : sale.getItemSaleList()) {
                        stmtItem.setInt(1, saleId);
                        stmtItem.setInt(2, item.getArticle().getId());
                        stmtItem.setInt(3, item.getAmount());
                        stmtItem.setLong(4, item.getArticle().getPrice().longValueExact());
                        stmtItem.addBatch();
                    }
                    stmtItem.executeBatch();
                }

                savedSale = new Sale(saleId, sale.getType(), sale.getCustomer(), sale.getItemSaleList());

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al guardar la venta: " + e.getMessage());
        }

        return savedSale;
    }

    @Override
    public Optional<Sale> findById(int id) {
        String findSQL = "SELECT s.id AS sale_id, s.type, s.date AS sale_date, " +
                "c.id AS customer_id, c.document, c.name AS customer_name, c.phone, c.address " +
                "FROM sale s " +
                "INNER JOIN customer c ON s.customer_id = c.id " +
                "WHERE s.id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(findSQL)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("document"),
                            rs.getString("customer_name"),
                            rs.getString("phone"),
                            rs.getString("address")
                    );

                    SaleTypes type = SaleTypes.valueOf(rs.getString("type"));
                    LocalDateTime date = rs.getObject("sale_date", LocalDateTime.class);
                    List<ItemSale> items = findItemsBySaleId(conn, id);

                    return Optional.of(new Sale(id, type, customer, items, date));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ocurrió un error al buscar la venta: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<Sale> findAll() {
        String findSQL = "SELECT s.id AS sale_id, s.type, s.date AS sale_date, " +
                "c.id AS customer_id, c.document, c.name AS customer_name, c.phone, c.address " +
                "FROM sale s " +
                "INNER JOIN customer c ON s.customer_id = c.id";

        List<Sale> sales = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(findSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int saleId = rs.getInt("sale_id");
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("document"),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("address")
                );

                SaleTypes type = SaleTypes.valueOf(rs.getString("type"));
                LocalDateTime date = rs.getObject("sale_date", LocalDateTime.class);
                List<ItemSale> items = findItemsBySaleId(conn, saleId);

                sales.add(new Sale(saleId, type, customer, items, date));
            }
        } catch (SQLException e) {
            System.out.println("Ocurrió un error al consultar las ventas: " + e.getMessage());
        }

        return sales;
    }

    private List<ItemSale> findItemsBySaleId(Connection conn, int saleId) throws SQLException {
        String findItemsSQL = "SELECT i.amount, i.unit_price, " +
                "a.id AS article_id, a.name AS article_name, a.size, a.color, a.price, a.quantity, a.category " +
                "FROM item_sale i " +
                "INNER JOIN article a ON i.article_id = a.id " +
                "WHERE i.sale_id = ?";

        List<ItemSale> items = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(findItemsSQL)) {
            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArticleCategories category = ArticleCategories.valueOf(rs.getString("category"));
                    Size size = category.createSize(rs.getString("size"));

                    Article article = new Article(
                            rs.getInt("article_id"),
                            rs.getString("article_name"),
                            size,
                            rs.getString("color"),
                            BigDecimal.valueOf(rs.getLong("price")),
                            rs.getInt("quantity"),
                            category
                    );

                    int amount = rs.getInt("amount");
                    items.add(new ItemSale(article, amount, article.getPrice()));
                }
            }
        }

        return items;
    }
}
