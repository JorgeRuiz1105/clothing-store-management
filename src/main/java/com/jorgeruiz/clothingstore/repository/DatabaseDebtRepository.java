package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.*;
import com.jorgeruiz.clothingstore.model.size.Size;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseDebtRepository implements DebtRepository{

    @Override
    public Debt save(Debt debt) {
        String insertSQL = "INSERT INTO debt (date, customer_id, sale_id) VALUES (?, ?, ?)";

        Debt savedDebt = debt;

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, debt.getDate().toString());
                    stmt.setInt(2, debt.getCustomer().getId());
                    stmt.setInt(3, debt.getSale().getId());

                    stmt.executeUpdate();

                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newId = generatedKeys.getInt(1);
                            savedDebt = new Debt(newId, debt.getCustomer(), debt.getSale());
                        }else {
                            throw new SQLException("No se pudo obtener el ID de la deuda.");
                        }
                    }
                }
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al guardar la deuda: " + e.getMessage());
        }

        return savedDebt;
    }

    @Override
    public Optional<Debt> findById(int id) {
        String findSQL = "SELECT d.id AS debt_id, d.date AS debt_date, "
                + "c.id AS customer_id, c.document, c.name AS customer_name, c.phone AS customer_phone, c.address AS customer_address, "
                + "s.id AS sale_id, s.type AS sale_type, s.date AS sale_date "
                + "FROM debt d "
                + "INNER JOIN customer c ON d.customer_id = c.id "
                + "INNER JOIN sale s ON d.sale_id = s.id "
                + "WHERE d.id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(findSQL)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Debt debt = mapDebtFromResultSet(conn, rs);
                    return Optional.of(debt);
                }
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al buscar la deuda: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Debt> findAll() {
        String findAllSQL = "SELECT d.id AS debt_id, d.date AS debt_date, "
                + "c.id AS customer_id, c.document, c.name AS customer_name, c.phone AS customer_phone, c.address AS customer_address, "
                + "s.id AS sale_id, s.type AS sale_type, s.date AS sale_date "
                + "FROM debt d "
                + "INNER JOIN customer c ON d.customer_id = c.id "
                + "INNER JOIN sale s ON d.sale_id = s.id";

        List<Debt> debts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(findAllSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                debts.add(mapDebtFromResultSet(conn, rs));
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al listar las deudas: " + e.getMessage());
        }

        return debts;
    }

    private Debt mapDebtFromResultSet(Connection conn, ResultSet rs) throws SQLException {
        int debtId = rs.getInt("debt_id");
        LocalDateTime debtDate = rs.getObject("debt_date", LocalDateTime.class);

        Customer customer = new Customer(
                rs.getInt("customer_id"),
                rs.getString("document"),
                rs.getString("customer_name"),
                rs.getString("customer_phone"),
                rs.getString("customer_address")
        );

        int saleId = rs.getInt("sale_id");
        SaleTypes saleType = SaleTypes.valueOf(rs.getString("sale_type"));
        LocalDateTime saleDate = rs.getObject("sale_date", LocalDateTime.class);
        List<ItemSale> items = findItemsBySaleId(conn, saleId);

        Sale sale = new Sale(saleId, saleType, customer, items, saleDate);

        return new Debt(debtId, customer, sale, saleDate);
    }

    private List<ItemSale> findItemsBySaleId(Connection conn, int saleId) throws SQLException {
        String findItemsSQL = "SELECT i.amount, i.unit_price, "
                + "a.id AS article_id, a.name AS article_name, a.size, a.color, a.price, a.quantity, a.category "
                + "FROM item_sale i "
                + "INNER JOIN article a ON i.article_id = a.id "
                + "WHERE i.sale_id = ?";

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

                    items.add(new ItemSale(article, rs.getInt("amount"), article.getPrice()));
                }
            }
        }

        return items;
    }
}
