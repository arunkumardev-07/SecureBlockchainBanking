package com.securebanking.web;

import com.securebanking.BlockchainTransactionVerifier;
import com.securebanking.DatabaseConnection;

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
public class BlockchainVerificationController {

    @GetMapping("/blockchain-verification")
    public String verificationPage(
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        return "blockchain-verification";
    }

    @PostMapping("/blockchain-verification")
    public String verifyTransaction(
            @RequestParam String transactionReference,
            HttpSession session,
            Model model) {

        Object customerIdObject =
                session.getAttribute("customerId");

        if (customerIdObject == null) {

            return "redirect:/";
        }

        int customerId =
                (Integer) customerIdObject;

        if (transactionReference == null ||
                transactionReference.isBlank()) {

            model.addAttribute(
                    "error",
                    "Transaction reference is required."
            );

            return "blockchain-verification";
        }

        String mysqlHash = null;

        String sql =
                "SELECT t.transaction_hash " +
                "FROM transactions t " +
                "WHERE t.transaction_reference = ? " +
                "AND (" +
                "t.sender_account IN (" +
                "SELECT account_number FROM accounts " +
                "WHERE customer_id = ?) " +
                "OR t.receiver_account IN (" +
                "SELECT account_number FROM accounts " +
                "WHERE customer_id = ?)" +
                ")";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    transactionReference
            );

            statement.setInt(
                    2,
                    customerId
            );

            statement.setInt(
                    3,
                    customerId
            );

            ResultSet resultSet =
                    statement.executeQuery();

            if (!resultSet.next()) {

                model.addAttribute(
                        "error",
                        "Transaction not found or you are not authorized to verify it."
                );

                return "blockchain-verification";
            }

            mysqlHash =
                    resultSet.getString(
                            "transaction_hash"
                    );

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Unable to read transaction from database."
            );

            e.printStackTrace();

            return "blockchain-verification";
        }

        try {

            BlockchainTransactionVerifier verifier =
                    new BlockchainTransactionVerifier();

            verifier.verifyTransaction(
                    transactionReference,
                    mysqlHash
            );

            model.addAttribute(
                    "success",
                    "Blockchain verification completed successfully. MySQL hash matches the blockchain hash."
            );

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Blockchain verification failed: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }

        model.addAttribute(
                "transactionReference",
                transactionReference
        );

        return "blockchain-verification";
    }
}
