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

        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            return "register";
        }

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

        String insertSql =
                "INSERT INTO customers " +
                "(full_name, email, phone, password) " +
                "VALUES (?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement checkStatement =
                        connection.prepareStatement(checkSql)
        ) {

            checkStatement.setString(1, email);
            checkStatement.setString(2, phone);

            ResultSet resultSet =
                    checkStatement.executeQuery();

            if (resultSet.next()) {

                model.addAttribute(
                        "error",
                        "Email or phone number is already registered."
                );

                return "register";
            }

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Unable to check registration details."
            );

            e.printStackTrace();

            return "register";
        }

        String hashedPassword =
                PasswordUtil.hashPassword(password);

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(insertSql)
        ) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, hashedPassword);

            int rowsInserted =
                    statement.executeUpdate();

            if (rowsInserted == 1) {

                return "redirect:/?registered=true";
            }

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Registration failed. Please try again."
            );

            e.printStackTrace();

            return "register";
        }

        model.addAttribute(
                "error",
                "Registration failed."
        );

        return "register";
    }
}
