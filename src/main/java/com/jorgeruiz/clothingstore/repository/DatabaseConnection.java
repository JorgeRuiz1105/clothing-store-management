package com.jorgeruiz.clothingstore.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:data/store.db?foreign_keys=true";

    public DatabaseConnection() {
    }

    public static Connection connect() throws SQLException {
        //To make sure data directory exists before trying to connect
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initDatabase(){
        String articleTable = "CREATE TABLE IF NOT EXISTS article ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "size TEXT NOT NULL,"
                + "color TEXT NOT NULL,"
                + "price INTEGER NOT NULL,"
                + "quantity INTEGER NOT NULL,"
                + "category TEXT NOT NULL"
                + ");";

        String customerTable = "CREATE TABLE IF NOT EXISTS customer ("
                + "id INTEGER PRIMARY KEY,"
                + "document TEXT NOT NULL UNIQUE,"
                + "name TEXT NOT NULL,"
                + "phone TEXT NOT NULL,"
                + "address TEXT NOT NULL"
                + ");";

        String saleTable = "CREATE TABLE IF NOT EXISTS sale ("
                + "id INTEGER PRIMARY KEY,"
                + "type TEXT NOT NULL,"
                + "date TEXT NOT NULL,"
                + "customer_id INTEGER NOT NULL,"
                + "FOREIGN KEY (customer_id) REFERENCES customer(id)"
                + ");";

        String itemSaleTable = "CREATE TABLE IF NOT EXISTS item_sale ("
                + "sale_id INTEGER NOT NULL,"
                + "article_id INTEGER NOT NULL,"
                + "amount INTEGER NOT NULL,"
                + "unit_price INTEGER NOT NULL,"
                + "PRIMARY KEY (sale_id, article_id),"
                + "FOREIGN KEY (sale_id) REFERENCES sale(id),"
                + "FOREIGN KEY (article_id) REFERENCES article(id)"
                + ");";

        String debtTable = "CREATE TABLE IF NOT EXISTS debt ("
                + "id INTEGER PRIMARY KEY,"
                + "date TEXT NOT NULL,"
                + "customer_id INTEGER NOT NULL,"
                + "sale_id INTEGER NOT NULL,"
                + "FOREIGN KEY (customer_id) REFERENCES customer(id),"
                + "FOREIGN KEY (sale_id) REFERENCES sale(id)"
                + ");";

        String paymentTable = "CREATE TABLE IF NOT EXISTS payment ("
                + "id INTEGER PRIMARY KEY,"
                + "amount INTEGER NOT NULL,"
                + "date TEXT NOT NULL,"
                + "debt_id INTEGER NOT NULL,"
                + "FOREIGN KEY (debt_id) REFERENCES debt(id)"
                + ");";

        try(Connection conn = connect();
            Statement stmt = conn.createStatement()){

            stmt.execute(articleTable);
            stmt.execute(customerTable);
            stmt.execute(saleTable);
            stmt.execute(itemSaleTable);
            stmt.execute(debtTable);
            stmt.execute(paymentTable);

            System.out.println("Base de datos inicializada!");
        }catch(SQLException e){
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}
