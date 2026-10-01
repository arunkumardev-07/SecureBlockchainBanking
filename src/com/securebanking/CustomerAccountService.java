package com.securebanking;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerAccountService {

    public void displayCustomerAccounts(int customerId) {

        String sql =
                "SELECT account_number, account_type, balance, created_at " +
                "FROM accounts " +
                "WHERE customer_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, customerId);

            ResultSet resultSet =
                    statement.executeQuery();

            boolean accountFound = false;

            System.out.println(
                    "\n================================"
            );

            System.out.println(
                    "CUSTOMER BANK ACCOUNTS"
            );

            System.out.println(
                    "================================"
            );

            while (resultSet.next()) {

                accountFound = true;

                String accountNumber =
                        resultSet.getString("account_number");

                String accountType =
                        resultSet.getString("account_type");

                double balance =
                        resultSet.getDouble("balance");

                String createdAt =
                        resultSet.getString("created_at");

                System.out.println(
                        "\nAccount Number: " + accountNumber
                );

                System.out.println(
                        "Account Type: " + accountType
                );

                System.out.println(
                        "Balance: Rs. " + balance
                );

                System.out.println(
                        "Created At: " + createdAt
                );

                System.out.println(
                        "--------------------------------"
                );
            }

            if (!accountFound) {

                System.out.println(
                        "No bank accounts found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve customer accounts."
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // CHECK ACCOUNT OWNERSHIP
    // =====================================================

    public boolean isAccountOwnedByCustomer(
            int customerId,
            String accountNumber) {

        String sql =
                "SELECT account_id " +
                "FROM accounts " +
                "WHERE customer_id = ? " +
                "AND account_number = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    customerId
            );

            statement.setString(
                    2,
                    accountNumber
            );

            ResultSet resultSet =
                    statement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {

            System.out.println(
                    "Failed to verify account ownership."
            );

            e.printStackTrace();

            return false;
        }
    }

    public AccountSummary getCustomerAccount(int customerId) {

        String sql =
                "SELECT account_number, account_type, balance " +
                "FROM accounts " +
                "WHERE customer_id = ? " +
                "LIMIT 1";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, customerId);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                String accountNumber =
                        resultSet.getString("account_number");

                String accountType =
                        resultSet.getString("account_type");

                BigDecimal balance =
                        resultSet.getBigDecimal("balance");

                return new AccountSummary(
                        accountNumber,
                        accountType,
                        balance
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve customer account."
            );

            e.printStackTrace();
        }

        return null;
    }
}

