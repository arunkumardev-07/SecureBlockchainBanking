package com.securebanking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

    // ============================================
    // Create Bank Account
    // ============================================

    public void createAccount(
            int customerId,
            String accountNumber,
            String accountType,
            double initialBalance) {

        // ============================================
        // 1. Validate initial balance
        // ============================================

        if (initialBalance < 0) {

            System.out.println(
                    "Invalid initial balance."
            );

            return;
        }


        // ============================================
        // 2. SQL to check customer
        // ============================================

        String customerSQL =
                "SELECT customer_id, full_name " +
                "FROM customers " +
                "WHERE customer_id = ?";


        // ============================================
        // 3. SQL to create account
        // ============================================

        String accountSQL =
                "INSERT INTO accounts " +
                "(customer_id, account_number, account_type, balance) " +
                "VALUES (?, ?, ?, ?)";


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement customerStatement =
                        connection.prepareStatement(
                                customerSQL
                        )
        ) {

            // ============================================
            // 4. Check customer exists
            // ============================================

            customerStatement.setInt(
                    1,
                    customerId
            );

            ResultSet customerResult =
                    customerStatement.executeQuery();


            if (!customerResult.next()) {

                System.out.println(
                        "Customer not found."
                );

                return;
            }


            String customerName =
                    customerResult.getString(
                            "full_name"
                    );


            System.out.println(
                    "Customer found: "
                    + customerName
            );


            // ============================================
            // 5. Create account
            // ============================================

            try (
                    PreparedStatement accountStatement =
                            connection.prepareStatement(
                                    accountSQL
                            )
            ) {

                accountStatement.setInt(
                        1,
                        customerId
                );

                accountStatement.setString(
                        2,
                        accountNumber
                );

                accountStatement.setString(
                        3,
                        accountType
                );

                accountStatement.setDouble(
                        4,
                        initialBalance
                );


                int rowsInserted =
                        accountStatement.executeUpdate();


                // ============================================
                // 6. Check result
                // ============================================

                if (rowsInserted == 1) {

                    System.out.println(
                            "\n================================"
                    );

                    System.out.println(
                            "BANK ACCOUNT CREATED"
                    );

                    System.out.println(
                            "================================"
                    );

                    System.out.println(
                            "Customer ID: "
                            + customerId
                    );

                    System.out.println(
                            "Customer Name: "
                            + customerName
                    );

                    System.out.println(
                            "Account Number: "
                            + accountNumber
                    );

                    System.out.println(
                            "Account Type: "
                            + accountType
                    );

                    System.out.println(
                            "Initial Balance: Rs. "
                            + initialBalance
                    );

                } else {

                    System.out.println(
                            "Account creation failed."
                    );
                }
            }


        } catch (SQLException e) {

            System.out.println(
                    "Failed to create bank account."
            );

            e.printStackTrace();
        }
    }


    // ============================================
    // Test
    // ============================================

    public static void main(String[] args) {

        AccountDAO accountDAO =
                new AccountDAO();


        /*
         * Customer ID 3 belongs to
         * Test User created earlier.
         *
         * We use a NEW account number so
         * we don't duplicate ACC1001 or ACC1002.
         */

        accountDAO.createAccount(
                3,
                "ACC1003",
                "SAVINGS",
                5000.00
        );
    }
}
