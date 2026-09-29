import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankDAO {

    public void transferMoney(String senderAccount,
                              String receiverAccount,
                              double amount) {

        String getBalanceSQL =
                "SELECT balance FROM accounts WHERE account_number = ?";

        String updateBalanceSQL =
                "UPDATE accounts SET balance = ? WHERE account_number = ?";

        String transactionSQL =
                "INSERT INTO transactions " +
                "(transaction_reference, sender_account, receiver_account, " +
                "transaction_type, amount, status, transaction_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection connection = null;

        try {

            // Connect to MySQL
            connection =
                    DatabaseConnection.getConnection();

            if (connection == null) {

                System.out.println(
                        "Database connection failed."
                );

                return;
            }

            // Start database transaction
            connection.setAutoCommit(false);


            // ------------------------------------------------
            // 1. GET SENDER BALANCE
            // ------------------------------------------------

            try (PreparedStatement getBalanceStatement =
                         connection.prepareStatement(
                                 getBalanceSQL)) {

                getBalanceStatement.setString(
                        1, senderAccount);

                ResultSet senderResult =
                        getBalanceStatement.executeQuery();

                if (!senderResult.next()) {

                    System.out.println(
                            "Sender account not found."
                    );

                    connection.rollback();
                    return;
                }

                double senderBalance =
                        senderResult.getDouble("balance");


                // ------------------------------------------------
                // 2. GET RECEIVER BALANCE
                // ------------------------------------------------

                getBalanceStatement.setString(
                        1, receiverAccount);

                ResultSet receiverResult =
                        getBalanceStatement.executeQuery();

                if (!receiverResult.next()) {

                    System.out.println(
                            "Receiver account not found."
                    );

                    connection.rollback();
                    return;
                }

                double receiverBalance =
                        receiverResult.getDouble("balance");


                // ------------------------------------------------
                // 3. VALIDATE AMOUNT
                // ------------------------------------------------

                if (amount <= 0) {

                    System.out.println(
                            "Invalid transfer amount."
                    );

                    connection.rollback();
                    return;
                }


                // ------------------------------------------------
                // 4. CHECK SENDER BALANCE
                // ------------------------------------------------

                if (senderBalance < amount) {

                    System.out.println(
                            "Insufficient balance."
                    );

                    connection.rollback();
                    return;
                }


                // ------------------------------------------------
                // 5. CALCULATE NEW BALANCES
                // ------------------------------------------------

                double newSenderBalance =
                        senderBalance - amount;

                double newReceiverBalance =
                        receiverBalance + amount;


                // ------------------------------------------------
                // 6. UPDATE SENDER BALANCE
                // ------------------------------------------------

                try (PreparedStatement updateBalanceStatement =
                             connection.prepareStatement(
                                     updateBalanceSQL)) {

                    updateBalanceStatement.setDouble(
                            1, newSenderBalance);

                    updateBalanceStatement.setString(
                            2, senderAccount);

                    int senderUpdated =
                            updateBalanceStatement.executeUpdate();


                    // ------------------------------------------------
                    // 7. UPDATE RECEIVER BALANCE
                    // ------------------------------------------------

                    updateBalanceStatement.setDouble(
                            1, newReceiverBalance);

                    updateBalanceStatement.setString(
                            2, receiverAccount);

                    int receiverUpdated =
                            updateBalanceStatement.executeUpdate();


                    // Check both updates
                    if (senderUpdated != 1 ||
                        receiverUpdated != 1) {

                        connection.rollback();

                        System.out.println(
                                "Balance update failed."
                        );

                        System.out.println(
                                "Transaction rolled back."
                        );

                        return;
                    }
                }


                // ------------------------------------------------
                // 8. GENERATE TRANSACTION REFERENCE
                // ------------------------------------------------

                String transactionReference =
                        "TXN" + System.currentTimeMillis();


                // ------------------------------------------------
                // 9. CREATE TRANSACTION DATA
                // ------------------------------------------------

                String transactionData =
                        transactionReference
                        + "|" + senderAccount
                        + "|" + receiverAccount
                        + "|" + amount
                        + "|FUND_TRANSFER";


                System.out.println(
                        "\nTransaction Data:"
                );

                System.out.println(
                        transactionData
                );


                // ------------------------------------------------
                // 10. GENERATE SHA-256 HASH
                // ------------------------------------------------

                String transactionHash =
                        HashUtil.generateSHA256(
                                transactionData
                        );


                System.out.println(
                        "\nSHA-256 Transaction Hash:"
                );

                System.out.println(
                        transactionHash
                );


                // ------------------------------------------------
                // 11. SEND HASH TO BLOCKCHAIN
                // ------------------------------------------------

                System.out.println(
                        "\nSending transaction to blockchain..."
                );

                BankingTransactionService
                        blockchainService =
                        new BankingTransactionService();

                String blockchainTransactionHash;

                try {

                    blockchainTransactionHash =
                            blockchainService.recordTransaction(
                                    transactionReference,
                                    transactionHash
                            );

                } finally {

                    blockchainService.close();
                }


                System.out.println(
                        "Transaction recorded on blockchain."
                );

                System.out.println(
                        "Blockchain Transaction Hash: "
                        + blockchainTransactionHash
                );


                // ------------------------------------------------
                // 12. RECORD TRANSACTION IN MYSQL
                // ------------------------------------------------

                try (PreparedStatement transactionStatement =
                             connection.prepareStatement(
                                     transactionSQL)) {

                    transactionStatement.setString(
                            1, transactionReference);

                    transactionStatement.setString(
                            2, senderAccount);

                    transactionStatement.setString(
                            3, receiverAccount);

                    transactionStatement.setString(
                            4, "FUND_TRANSFER");

                    transactionStatement.setDouble(
                            5, amount);

                    transactionStatement.setString(
                            6, "SUCCESS");

                    transactionStatement.setString(
                            7, transactionHash);


                    int transactionInserted =
                            transactionStatement.executeUpdate();


                    // ------------------------------------------------
                    // 13. COMMIT MYSQL TRANSACTION
                    // ------------------------------------------------

                    if (transactionInserted == 1) {

                        connection.commit();

                        System.out.println(
                                "\n================================"
                        );

                        System.out.println(
                                "TRANSFER SUCCESSFUL"
                        );

                        System.out.println(
                                "================================"
                        );

                        System.out.println(
                                "Transaction Reference: "
                                + transactionReference
                        );

                        System.out.println(
                                "Sender: "
                                + senderAccount
                        );

                        System.out.println(
                                "Receiver: "
                                + receiverAccount
                        );

                        System.out.println(
                                "Amount: Rs. "
                                + amount
                        );

                        System.out.println(
                                "SHA-256 Hash: "
                                + transactionHash
                        );

                        System.out.println(
                                "Blockchain TX Hash: "
                                + blockchainTransactionHash
                        );

                        System.out.println(
                                "MySQL + Blockchain transaction completed."
                        );

                    } else {

                        connection.rollback();

                        System.out.println(
                                "Transaction recording failed."
                        );

                        System.out.println(
                                "MySQL changes rolled back."
                        );
                    }
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "\nTRANSFER FAILED."
            );

            e.printStackTrace();

            // Roll back MySQL changes
            if (connection != null) {

                try {

                    connection.rollback();

                    System.out.println(
                            "MySQL transaction rolled back."
                    );

                } catch (SQLException rollbackException) {

                    rollbackException.printStackTrace();
                }
            }

        } finally {

            // Restore auto-commit and close connection
            if (connection != null) {

                try {

                    connection.setAutoCommit(true);
                    connection.close();

                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {

        BankDAO bankDAO =
                new BankDAO();

        bankDAO.transferMoney(
                "ACC1001",
                "ACC1002",
                1000.00
        );
    }
}