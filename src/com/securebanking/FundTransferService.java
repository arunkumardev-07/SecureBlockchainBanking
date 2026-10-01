package com.securebanking;

public class FundTransferService {

    public void transferMoney(
            String senderAccount,
            String receiverAccount,
            double amount) {

        // ==========================================
        // CHECK ACCOUNT NUMBERS
        // ==========================================

        if (senderAccount == null ||
            senderAccount.isBlank() ||
            receiverAccount == null ||
            receiverAccount.isBlank()) {

            System.out.println(
                    "Account numbers cannot be empty."
            );

            return;
        }


        // ==========================================
        // CHECK SAME ACCOUNT
        // ==========================================

        if (senderAccount.equals(receiverAccount)) {

            System.out.println(
                    "Sender and receiver accounts cannot be the same."
            );

            return;
        }


        // ==========================================
        // CHECK AMOUNT
        // ==========================================

        if (amount <= 0) {

            System.out.println(
                    "Invalid transfer amount."
            );

            return;
        }


        // ==========================================
        // CHECK RECEIVER ACCOUNT
        // ==========================================

        if (!accountExists(receiverAccount)) {

            System.out.println(
                    "Receiver account not found."
            );

            return;
        }


        // ==========================================
        // PERFORM TRANSFER
        // ==========================================

        BankDAO bankDAO =
                new BankDAO();

        bankDAO.transferMoney(
                senderAccount,
                receiverAccount,
                amount
        );
    }


    // ==============================================
    // CHECK WHETHER ACCOUNT EXISTS
    // ==============================================

    private boolean accountExists(
            String accountNumber) {

        String sql =
                "SELECT account_id " +
                "FROM accounts " +
                "WHERE account_number = ?";

        try (
                java.sql.Connection connection =
                        DatabaseConnection.getConnection();

                java.sql.PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    accountNumber
            );

            java.sql.ResultSet resultSet =
                    statement.executeQuery();

            return resultSet.next();

        } catch (java.sql.SQLException e) {

            System.out.println(
                    "Failed to verify receiver account."
            );

            e.printStackTrace();

            return false;
        }
    }
}
