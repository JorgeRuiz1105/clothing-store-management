package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.*;
import com.jorgeruiz.clothingstore.model.size.Size;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabasePaymentRepository implements PaymentRepository{
    @Override
    public Payment save(Payment payment) {
        String insertSQL = "INSERT INTO payment (amount, date, debt_id) VALUES (?, ?, ?)";

        Payment savedPayment = payment;

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setLong(1, payment.getAmount().longValueExact());
                    stmt.setString(2, payment.getDate().toString());
                    stmt.setInt(3, payment.getDebt().getId());

                    stmt.executeUpdate();

                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newId = generatedKeys.getInt(1);
                            savedPayment = new Payment(newId, payment.getAmount(), payment.getDebt());
                        }else {
                            throw new SQLException("No se pudo obtener el ID del abono.");
                        }
                    }
                }
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al guardar el abono: " + e.getMessage());
        }

        return savedPayment;
    }

    @Override
    public Optional<Payment> findById(int id) {
        String findSQL = "SELECT p.id AS payment_id, p.amount AS payment_amount, p.date AS payment_date, "
                + "d.id AS debt_id, d.date AS debt_date, "
                + "c.id AS customer_id, c.document, c.name AS customer_name, c.phone AS customer_phone, c.address AS customer_address, "
                + "s.id AS sale_id, s.type AS sale_type, s.date AS sale_date "
                + "FROM payment p "
                + "INNER JOIN debt d ON p.debt_id = d.id "
                + "INNER JOIN customer c ON d.customer_id = c.id "
                + "INNER JOIN sale s ON d.sale_id = s.id "
                + "WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(findSQL)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapPaymentFromResultSet(conn, rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al buscar el abono: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Payment> findAll() {
        String findSQL = "SELECT p.id AS payment_id, p.amount AS payment_amount, p.date AS payment_date, "
                + "d.id AS debt_id, d.date AS debt_date, "
                + "c.id AS customer_id, c.document, c.name AS customer_name, c.phone AS customer_phone, c.address AS customer_address, "
                + "s.id AS sale_id, s.type AS sale_type, s.date AS sale_date "
                + "FROM payment p "
                + "INNER JOIN debt d ON p.debt_id = d.id "
                + "INNER JOIN customer c ON d.customer_id = c.id "
                + "INNER JOIN sale s ON d.sale_id = s.id";

        List<Payment> payments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(findSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                payments.add(mapPaymentFromResultSet(conn, rs));
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al listar los pagos: " + e.getMessage());
        }

        return payments;
    }

    @Override
    public BigDecimal sumAmountByDebtId(int debtId) {
        String sumSQL = "SELECT COALESCE(SUM(amount), 0) AS total FROM payment WHERE debt_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sumSQL)) {

            stmt.setInt(1, debtId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return BigDecimal.valueOf(rs.getLong("total"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al sumar los abonos de la deuda: " + e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    private Payment mapPaymentFromResultSet(Connection conn, ResultSet rs) throws SQLException {
        int paymentId = rs.getInt("payment_id");
        BigDecimal amount = BigDecimal.valueOf(rs.getLong("payment_amount"));
        LocalDateTime paymentDate = rs.getObject("payment_date", LocalDateTime.class);

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
        Debt debt = new Debt(debtId, customer, sale, debtDate);

        return new Payment(paymentId, amount, debt, paymentDate);
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
