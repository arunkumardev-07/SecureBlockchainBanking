package com.securebanking.web;

import com.securebanking.DatabaseConnection;
import com.securebanking.PasswordUtil;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
public class RegistrationController {

    @GetMapping("/register")
    public String registerPage() {

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        fullName = fullName.trim();
        email = email.trim();
        phone = phone.trim();

        // Validate required fields
        if (fullName.isBlank() ||
                email.isBlank() ||
                phone.isBlank() ||
                password.isBlank()) {

            model.addAttribute(
                    "error",
                    "All fields are required."
            );

            return "register";
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            return "register";
        }

        // Validate password length
        if (password.length() < 8) {

            model.addAttribute(
                    "error",
                    "Password must contain at least 8 characters."
            );

            return "register";
        }

        String checkSql =
                "SELECT customer_id " +
                "FROM customers " +
                "WHERE email = ? OR phone = ?";

        String insertCustomerSql =
                "INSERT INTO customers " +
                "(full_name, email, phone, password) " +
                "VALUES (?, ?, ?, ?)";

        String insertAccountSql =
                "INSERT INTO accounts " +
                "(customer_id, account_number, account_type, balance) " +
                "VALUES (?, ?, 'SAVINGS', 0.00)";

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            if (connection == null) {

                model.addAttribute(
                        "error",
                        "Unable to connect to the database."
                );

                return "register";
            }

            /*
             * Start database transaction.
             *
             * Customer creation and account creation
             * must both succeed.
             */
            connection.setAutoCommit(false);

            // ------------------------------------------------
            // STEP 1: Check duplicate email or phone
            // ------------------------------------------------

            try (
                    PreparedStatement checkStatement =
                            connection.prepareStatement(checkSql)
            ) {

                checkStatement.setString(1, email);
                checkStatement.setString(2, phone);

                ResultSet resultSet =
                        checkStatement.executeQuery();

                if (resultSet.next()) {

                    connection.rollback();

                    model.addAttribute(
                            "error",
                            "Email or phone number is already registered."
                    );

                    return "register";
                }
            }

            // ------------------------------------------------
            // STEP 2: Create customer
            // ------------------------------------------------

            int customerId;

            try (
                    PreparedStatement customerStatement =
                            connection.prepareStatement(
                                    insertCustomerSql,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                customerStatement.setString(
                        1,
                        fullName
                );

                customerStatement.setString(
                        2,
                        email
                );

                customerStatement.setString(
                        3,
                        phone
                );

                customerStatement.setString(
                        4,
                        hashedPassword
                );

                int rowsInserted =
                        customerStatement.executeUpdate();

                if (rowsInserted != 1) {

                    connection.rollback();

                    model.addAttribute(
                            "error",
                            "Customer registration failed."
                    );

                    return "register";
                }

                // Get the newly generated customer ID
                try (
                        ResultSet generatedKeys =
                                customerStatement.getGeneratedKeys()
                ) {

                    if (!generatedKeys.next()) {

                        connection.rollback();

                        model.addAttribute(
                                "error",
                                "Unable to create customer ID."
                        );

                        return "register";
                    }

                    customerId =
                            generatedKeys.getInt(1);
                }
            }

            // ------------------------------------------------
            // STEP 3: Generate bank account number
            // ------------------------------------------------

            String accountNumber =
                    String.format(
                            "ACC%07d",
                            customerId
                    );

            // ------------------------------------------------
            // STEP 4: Create Savings Account
            // ------------------------------------------------

            try (
                    PreparedStatement accountStatement =
                            connection.prepareStatement(
                                    insertAccountSql
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

                int accountCreated =
                        accountStatement.executeUpdate();

                if (accountCreated != 1) {

                    connection.rollback();

                    model.addAttribute(
                            "error",
                            "Unable to create bank account."
                    );

                    return "register";
                }
            }

            // ------------------------------------------------
            // STEP 5: Commit everything
            // ------------------------------------------------

            connection.commit();

            System.out.println(
                    "================================"
            );

            System.out.println(
                    "REGISTRATION SUCCESSFUL"
            );

            System.out.println(
                    "Customer ID: " + customerId
            );

            System.out.println(
                    "Account Number: " + accountNumber
            );

            System.out.println(
                    "Account Type: SAVINGS"
            );

            System.out.println(
                    "Initial Balance: ₹0.00"
            );

            System.out.println(
                    "================================"
            );

            return "redirect:/?registered=true";

        } catch (Exception e) {

            try {

                if (connection != null) {
                    connection.rollback();
                }

            } catch (Exception rollbackException) {

                rollbackException.printStackTrace();
            }

            System.out.println(
                    "Registration failed."
            );

            e.printStackTrace();

            model.addAttribute(
                    "error",
                    "Registration failed. Please try again."
            );

            return "register";

        } finally {

            try {

                if (connection != null) {

                    connection.setAutoCommit(true);
                    connection.close();
                }

            } catch (Exception closeException) {

                closeException.printStackTrace();
            }
        }
    }
}