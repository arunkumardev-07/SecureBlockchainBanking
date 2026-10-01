package com.securebanking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    public void recordTransaction(String transactionReference,
                                   String senderAccount,
                                   String receiverAccount,
                                   String transactionType,
                                   double amount,
                                   String status,
                                   String transactionHash) {

        String sql = "INSERT INTO transactions " +
                     "(transaction_reference, sender_account, receiver_account, " +
                     "transaction_type, amount, status, transaction_hash) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transactionReference);
            statement.setString(2, senderAccount);
            statement.setString(3, receiverAccount);
            statement.setString(4, transactionType);
            statement.setDouble(5, amount);
            statement.setString(6, status);
            statement.setString(7, transactionHash);

            statement.executeUpdate();

            System.out.println("Transaction recorded successfully.");

        } catch (SQLException e) {

            System.out.println("Failed to record transaction.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        TransactionDAO transactionDAO = new TransactionDAO();

        transactionDAO.recordTransaction(
                "TXN1001",
                "ACC1001",
                "ACC1002",
                "FUND_TRANSFER",
                2000.00,
                "SUCCESS",
                "TEST_HASH_123456"
        );
    }
}

