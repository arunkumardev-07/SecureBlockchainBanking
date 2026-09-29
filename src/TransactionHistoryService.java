import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionHistoryService {

    public void displayTransactionHistory(
            int customerId) {

        String sql =
                "SELECT t.transaction_reference, " +
                "t.sender_account, " +
                "t.receiver_account, " +
                "t.transaction_type, " +
                "t.amount, " +
                "t.status, " +
                "t.transaction_hash, " +
                "t.created_at " +
                "FROM transactions t " +
                "WHERE t.sender_account IN " +
                "(SELECT account_number " +
                "FROM accounts " +
                "WHERE customer_id = ?) " +
                "OR t.receiver_account IN " +
                "(SELECT account_number " +
                "FROM accounts " +
                "WHERE customer_id = ?) " +
                "ORDER BY t.created_at DESC";


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            // ==========================================
            // SET CUSTOMER ID
            // ==========================================

            statement.setInt(
                    1,
                    customerId
            );

            statement.setInt(
                    2,
                    customerId
            );


            ResultSet resultSet =
                    statement.executeQuery();


            boolean transactionFound = false;


            System.out.println(
                    "\n========================================"
            );

            System.out.println(
                    "         TRANSACTION HISTORY"
            );

            System.out.println(
                    "========================================"
            );


            while (resultSet.next()) {

                transactionFound = true;


                // ======================================
                // GET TRANSACTION DETAILS
                // ======================================

                String reference =
                        resultSet.getString(
                                "transaction_reference"
                        );


                String sender =
                        resultSet.getString(
                                "sender_account"
                        );


                String receiver =
                        resultSet.getString(
                                "receiver_account"
                        );


                String type =
                        resultSet.getString(
                                "transaction_type"
                        );


                double amount =
                        resultSet.getDouble(
                                "amount"
                        );


                String status =
                        resultSet.getString(
                                "status"
                        );


                String hash =
                        resultSet.getString(
                                "transaction_hash"
                        );


                String createdAt =
                        resultSet.getString(
                                "created_at"
                        );


                // ======================================
                // DISPLAY TRANSACTION
                // ======================================

                System.out.println(
                        "\nTransaction Reference: "
                        + reference
                );


                // ======================================
                // DEPOSIT
                // ======================================

                if ("DEPOSIT".equals(type)) {

                    System.out.println(
                            "Transaction Type: DEPOSIT"
                    );

                    System.out.println(
                            "Account: " + receiver
                    );

                }


                // ======================================
                // WITHDRAWAL
                // ======================================

                else if ("WITHDRAWAL".equals(type)) {

                    System.out.println(
                            "Transaction Type: WITHDRAWAL"
                    );

                    System.out.println(
                            "Account: " + sender
                    );

                }


                // ======================================
                // FUND TRANSFER
                // ======================================

                else {

                    System.out.println(
                            "Transaction Type: "
                            + type
                    );

                    System.out.println(
                            "Sender: " + sender
                    );

                    System.out.println(
                            "Receiver: " + receiver
                    );
                }


                // ======================================
                // COMMON TRANSACTION DETAILS
                // ======================================

                System.out.println(
                        "Amount: Rs. " + amount
                );


                System.out.println(
                        "Status: " + status
                );


                System.out.println(
                        "SHA-256 Hash: " + hash
                );


                System.out.println(
                        "Created At: " + createdAt
                );


                System.out.println(
                        "----------------------------------------"
                );
            }


            // ==========================================
            // NO TRANSACTIONS
            // ==========================================

            if (!transactionFound) {

                System.out.println(
                        "No transactions found."
                );
            }


        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve transaction history."
            );

            e.printStackTrace();
        }
    }
}