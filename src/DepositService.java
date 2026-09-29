import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositService {

    public void depositMoney(
            String accountNumber,
            double amount) {

        // ==========================================
        // VALIDATE AMOUNT
        // ==========================================

        if (amount <= 0) {

            System.out.println(
                    "Invalid deposit amount."
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
            // CALCULATE NEW BALANCE
            // ==========================================

            double newBalance =
                    currentBalance + amount;


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
                    "DEP" + System.currentTimeMillis();


            // ==========================================
            // CREATE TRANSACTION DATA
            // ==========================================
            //
            // Deposit has no sender account.
            // Therefore sender is represented as empty.
            //

            String transactionData =
                    transactionReference
                    + "|"
                    + ""
                    + "|"
                    + accountNumber
                    + "|"
                    + amount
                    + "|DEPOSIT";


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
            // SEND HASH TO BLOCKCHAIN
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


                // Deposit has no sender
                statement.setString(
                        2,
                        null
                );


                statement.setString(
                        3,
                        accountNumber
                );


                statement.setString(
                        4,
                        "DEPOSIT"
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
                            "       DEPOSIT SUCCESSFUL"
                    );

                    System.out.println(
                            "========================================"
                    );


                    System.out.println(
                            "Account Number: "
                            + accountNumber
                    );


                    System.out.println(
                            "Deposited Amount: Rs. "
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
                    "\nDEPOSIT FAILED."
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