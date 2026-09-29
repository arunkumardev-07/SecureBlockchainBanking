import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionVerifier {

    public void verifyTransaction(String transactionReference) {

        String sql =
                "SELECT transaction_reference, sender_account, " +
                "receiver_account, amount, transaction_type, " +
                "transaction_hash " +
                "FROM transactions " +
                "WHERE transaction_reference = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, transactionReference);

            ResultSet resultSet =
                    statement.executeQuery();

            if (!resultSet.next()) {

                System.out.println(
                        "Transaction not found.");

                return;
            }

            String reference =
                    resultSet.getString("transaction_reference");

            String sender =
                    resultSet.getString("sender_account");

            String receiver =
                    resultSet.getString("receiver_account");

            double amount =
                    resultSet.getDouble("amount");

            String transactionType =
                    resultSet.getString("transaction_type");

            String storedHash =
                    resultSet.getString("transaction_hash");

            // Recreate the original transaction data
            String transactionData =
                    reference
                    + "|" + sender
                    + "|" + receiver
                    + "|" + amount
                    + "|" + transactionType;

            // Generate SHA-256 hash again
            String calculatedHash =
                    HashUtil.generateSHA256(transactionData);

            System.out.println("Transaction Reference: "
                    + reference);

            System.out.println("Transaction Data:");
            System.out.println(transactionData);

            System.out.println("\nStored Hash:");
            System.out.println(storedHash);

            System.out.println("\nCalculated Hash:");
            System.out.println(calculatedHash);

            // Compare both hashes
            if (storedHash.equals(calculatedHash)) {

                System.out.println(
                        "\nTransaction integrity verified.");

                System.out.println(
                        "Transaction data has not been changed.");

            } else {

                System.out.println(
                        "\nWARNING: Transaction integrity failed.");

                System.out.println(
                        "Transaction data may have been modified.");
            }

        } catch (SQLException e) {

            System.out.println(
                    "Transaction verification failed.");

            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        TransactionVerifier verifier =
                new TransactionVerifier();

        verifier.verifyTransaction(
                "TXN1790615485556"
        );
    }
}