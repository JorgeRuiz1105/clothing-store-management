package com.jorgeruiz.clothingstore.repository;

import com.jorgeruiz.clothingstore.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseCustomerRepository implements CustomerRepository{

    @Override
    public Customer save(Customer customer) {
        String insertSQL = "INSERT INTO customer (document, name, phone, address) VALUES (?, ?, ?, ?)";
        String updateSQL = "UPDATE customer SET name = ?, phone = ?, address = ? WHERE document = ?";

        Customer savedCustomer = customer;

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);

            try {
                if (customer.getId() == 0) {
                    try (PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, customer.getDocument());
                        stmt.setString(2, customer.getName());
                        stmt.setString(3, customer.getPhone());
                        stmt.setString(4, customer.getAddress());

                        stmt.executeUpdate();

                        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int newId = generatedKeys.getInt(1);
                                savedCustomer = new Customer(newId, customer.getDocument(), customer.getName(),
                                        customer.getPhone(), customer.getAddress());
                            }else {
                                throw new SQLException("No se pudo obtener el ID del cliente.");
                            }
                        }
                    }
                } else {
                    try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                        stmt.setString(1, customer.getName());
                        stmt.setString(2, customer.getPhone());
                        stmt.setString(3, customer.getAddress());

                        stmt.executeUpdate();
                    }
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Ocurrio un error al registrar el cliente: " + e.getMessage());
        }

        return savedCustomer;
    }

    @Override
    public Optional<Customer> findByDocument(String document) {
        String findSQL = "SELECT * FROM customer WHERE document = ?";

        try(Connection conn = DatabaseConnection.connect()){
            try(PreparedStatement stmt = conn.prepareStatement(findSQL)){
                stmt.setString(1, document);
                try(ResultSet rs = stmt.executeQuery()){
                    if(rs.next()){
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String phone = rs.getString("phone");
                        String address = rs.getString("address");

                        return Optional.of(new Customer(id, document, name, phone, address));
                    }
                }
            }
        }catch (SQLException e){
            System.out.println("Ocurrio un error al buscar el cliente: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        String findAllSQL = "SELECT * FROM customer";
        List<Customer> customers = new ArrayList<>();

        try(Connection conn = DatabaseConnection.connect()){
            try(PreparedStatement stmt = conn.prepareStatement(findAllSQL);
                ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt("id");
                    String document = rs.getString("document");
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String address = rs.getString("address");

                    Customer customer;
                    customer = new Customer(id, document, name, phone, address);
                    customers.add(customer);
                }
            }
        }catch (SQLException e){
            System.out.println("Ocurrio un error al buscar los clientes: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public void deleteByDocument(String document) {
        String deleteSQL = "DELETE FROM customer WHERE document = ?";

        try(Connection conn = DatabaseConnection.connect()){

            conn.setAutoCommit(false);

            try{
                try (PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
                    stmt.setString(1,document);

                    stmt.executeUpdate();
                }
                conn.commit();
            }catch (SQLException e){
                conn.rollback();
                throw e;
            }
        }catch (SQLException e){
            System.out.println("Ocurrio un error al eliminar el cliente: " + e.getMessage());
        }
    }
}
