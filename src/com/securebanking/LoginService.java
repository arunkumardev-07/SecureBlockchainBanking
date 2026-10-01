package com.securebanking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {

    // Stores the customer ID of the currently logged-in customer
    private int loggedInCustomerId;

    public boolean login(
            String email,
            String password) {

        String sql =
                "SELECT customer_id, full_name, password " +
                "FROM customers " +
                "WHERE email = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            // Put the user's email into the SQL query
            statement.setString(1, email);

            ResultSet resultSet =
                    statement.executeQuery();

            // Check whether the email exists
            if (!resultSet.next()) {

                System.out.println(
                        "Login failed."
                );

                System.out.println(
                        "Email not found."
                );

                return false;
            }

            // Get customer information from MySQL
            int customerId =
                    resultSet.getInt("customer_id");

            String fullName =
                    resultSet.getString("full_name");

            String storedPasswordHash =
                    resultSet.getString("password");

            // Remember the customer who logged in
            loggedInCustomerId = customerId;

            // Compare entered password with BCrypt hash
            boolean passwordCorrect =
                    PasswordUtil.verifyPassword(
                            password,
                            storedPasswordHash
                    );

            if (passwordCorrect) {

                System.out.println(
                        "\n================================"
                );

                System.out.println(
                        "LOGIN SUCCESSFUL"
                );

                System.out.println(
                        "================================"
                );

                System.out.println(
                        "Customer ID: " + customerId
                );

                System.out.println(
                        "Welcome, " + fullName
                );

                return true;

            } else {

                System.out.println(
                        "\nLOGIN FAILED"
                );

                System.out.println(
                        "Incorrect password."
                );

                return false;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Login operation failed."
            );

            e.printStackTrace();

            return false;
        }
    }


    // Returns the ID of the currently logged-in customer
    public int getLoggedInCustomerId() {

        return loggedInCustomerId;
    }


    public static void main(String[] args) {

        LoginService loginService =
                new LoginService();


        System.out.println(
                "===== TEST 1: CORRECT PASSWORD ====="
        );

        loginService.login(
                "testuser@example.com",
                "Test@123"
        );


        System.out.println(
                "\nLogged-in Customer ID: "
                + loginService.getLoggedInCustomerId()
        );


        System.out.println(
                "\n===== TEST 2: WRONG PASSWORD ====="
        );

        loginService.login(
                "testuser@example.com",
                "WrongPassword"
        );


        System.out.println(
                "\n===== TEST 3: UNKNOWN EMAIL ====="
        );

        loginService.login(
                "unknown@example.com",
                "Test@123"
        );
    }
}
