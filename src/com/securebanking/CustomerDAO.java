package com.securebanking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDAO {

    public void addCustomer(
            String name,
            String email,
            String phone,
            String password) {

        // ============================================
        // 1. Hash the password
        // ============================================

        String hashedPassword =
                PasswordUtil.hashPassword(password);


        String sql =
                "INSERT INTO customers " +
                "(full_name, email, phone, password) " +
                "VALUES (?, ?, ?, ?)";


        // ============================================
        // 2. Connect to database
        // ============================================

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {


            // ============================================
            // 3. Set customer information
            // ============================================

            statement.setString(1, name);

            statement.setString(2, email);

            statement.setString(3, phone);


            // Store HASH instead of plain password
            statement.setString(4, hashedPassword);


            // ============================================
            // 4. Insert customer
            // ============================================

            statement.executeUpdate();


            System.out.println(
                    "Customer added successfully."
            );

            System.out.println(
                    "Password stored securely using BCrypt."
            );


        } catch (SQLException e) {

            System.out.println(
                    "Failed to add customer."
            );

            e.printStackTrace();
        }
    }


    // ============================================
    // Test
    // ============================================

    
}
