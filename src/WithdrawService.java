import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WithdrawService {

    public void withdrawMoney(
            String accountNumber,
            double amount) {

        // ==========================================
        // VALIDATE AMOUNT
        // ==========================================

        if (amount <= 0) {

            System.out.println(
                    "Invalid withdrawal amount."
            );

            return;
        }


        // ==========================================
        // SQL QUERIES
        // ==========================================

        String getBalanceSQL =
                "SELECT balance " +
                "FROM accounts " +
                "WHERE account_number = ?";


        String updateBalanceSQL =
                "UPDATE accounts " +
                "SET balance = ? " +
                "WHERE account_number = ?";


        String transactionSQL =
                "INSERT INTO transactions " +
                "(transaction_reference, sender_account, " +
                "receiver_account, transaction_type, amount, " +
                "status, transaction_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";


        Connection connection = null;


        try {

            // ==========================================
            // DATABASE CONNECTION
            // ==========================================

            connection =
                    DatabaseConnection.getConnection();


            if (connection == null) {

                System.out.println(
                        "Database connection failed."
                );

                return;
            }


            // ==========================================
            // START DATABASE TRANSACTION
            // ==========================================

            connection.setAutoCommit(false);


            // ==========================================
            // GET CURRENT BALANCE
            // ==========================================

            double currentBalance;


            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    getBalanceSQL
                            )
            ) {

                statement.setString(
                        1,
                        accountNumber
                );


                ResultSet resultSet =
                        statement.executeQuery();


                if (!resultSet.next()) {

                    System.out.println(
                            "Account not found."
                    );

                    connection.rollback();

                    return;
                }


                currentBalance =
                        resultSet.getDouble(
                                "balance"
                        );
            }


            // ==========================================
            // CHECK SUFFICIENT BALANCE
            // ==========================================

            if (amount > currentBalance) {

                System.out.println(
                        "Insufficient balance."
                );

                System.out.println(
                        "Current Balance: Rs. "
                        + currentBalance
                );

                connection.rollback();

                return;
            }


            // ==========================================
            // CALCULATE NEW BALANCE
            // ==========================================

            double newBalance =
                    currentBalance - amount;


            // ==========================================
            // UPDATE ACCOUNT BALANCE
            // ==========================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    updateBalanceSQL
                            )
            ) {

                statement.setDouble(
                        1,
                        newBalance
                );

                statement.setString(
                        2,
                        accountNumber
                );


                int rowsUpdated =
                        statement.executeUpdate();


                if (rowsUpdated != 1) {

                    System.out.println(
                            "Balance update failed."
                    );

                    connection.rollback();

                    return;
                }
            }


            // ==========================================
            // CREATE TRANSACTION REFERENCE
            // ==========================================

            String transactionReference =
                    "WDR" + System.currentTimeMillis();


            // ==========================================
            // CREATE TRANSACTION DATA
            // ==========================================
            //
            // Withdrawal has no receiver account.
            //

            String transactionData =
                    transactionReference
                    + "|"
                    + accountNumber
                    + "|"
                    + ""
                    + "|"
                    + amount
                    + "|WITHDRAWAL";


            System.out.println(
                    "\nTransaction Data:"
            );

            System.out.println(
                    transactionData
            );


            // ==========================================
            // GENERATE SHA-256 HASH
            // ==========================================

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


            // ==========================================
            // SEND TRANSACTION TO BLOCKCHAIN
            // ==========================================

            System.out.println(
                    "\nSending transaction to blockchain..."
            );


            BankingTransactionService blockchainService =
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


            // ==========================================
            // RECORD TRANSACTION IN MYSQL
            // ==========================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    transactionSQL
                            )
            ) {

                statement.setString(
                        1,
                        transactionReference
                );


                // Withdrawal has no receiver
                statement.setString(
                        2,
                        accountNumber
                );


                statement.setString(
                        3,
                        null
                );


                statement.setString(
                        4,
                        "WITHDRAWAL"
                );


                statement.setDouble(
                        5,
                        amount
                );


                statement.setString(
                        6,
                        "SUCCESS"
                );


                statement.setString(
                        7,
                        transactionHash
                );


                int transactionInserted =
                        statement.executeUpdate();


                if (transactionInserted == 1) {

                    // ==================================
                    // COMMIT
                    // ==================================

                    connection.commit();


                    System.out.println(
                            "\n========================================"
                    );

                    System.out.println(
                            "      WITHDRAWAL SUCCESSFUL"
                    );

                    System.out.println(
                            "========================================"
                    );


                    System.out.println(
                            "Account Number: "
                            + accountNumber
                    );


                    System.out.println(
                            "Withdrawn Amount: Rs. "
                            + amount
                    );


                    System.out.println(
                            "Previous Balance: Rs. "
                            + currentBalance
                    );


                    System.out.println(
                            "New Balance: Rs. "
                            + newBalance
                    );


                    System.out.println(
                            "Transaction Reference: "
                            + transactionReference
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
                            "\nMySQL + Blockchain transaction completed."
                    );

                } else {

                    connection.rollback();


                    System.out.println(
                            "Transaction recording failed."
                    );


                    System.out.println(
                            "Database changes rolled back."
                    );
                }
            }


        } catch (Exception e) {

            System.out.println(
                    "\nWITHDRAWAL FAILED."
            );


            e.printStackTrace();


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
}