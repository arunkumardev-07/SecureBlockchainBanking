import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApplication {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        LoginService loginService = new LoginService();
        CustomerDAO customerDAO = new CustomerDAO();

        boolean running = true;

        while (running) {

            System.out.println(
                    "\n========================================"
            );

            System.out.println(
                    "       SECURE BLOCKCHAIN BANKING"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            System.out.print("\nEnter your choice: ");

            int choice;

            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {

                System.out.println(
                        "\nInvalid input. Please enter a number."
                );

                scanner.nextLine();
                continue;
            }

            switch (choice) {

                // ==========================================
                // REGISTER
                // ==========================================

                case 1:

                    System.out.println(
                            "\n========== CUSTOMER REGISTRATION =========="
                    );

                    System.out.print("Enter Full Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();

                    System.out.print("Enter Phone Number: ");
                    String phone = scanner.nextLine();

                    System.out.print("Enter Password: ");
                    String password = scanner.nextLine();

                    customerDAO.addCustomer(
                            name,
                            email,
                            phone,
                            password
                    );

                    break;


                // ==========================================
                // LOGIN
                // ==========================================

                case 2:

                    System.out.println(
                            "\n============== LOGIN =============="
                    );

                    System.out.print("Enter Email: ");
                    String loginEmail = scanner.nextLine();

                    System.out.print("Enter Password: ");
                    String loginPassword = scanner.nextLine();

                    boolean loginSuccessful =
                            loginService.login(
                                    loginEmail,
                                    loginPassword
                            );

                    if (loginSuccessful) {

                        int customerId =
                                loginService.getLoggedInCustomerId();

                        showCustomerDashboard(
                                scanner,
                                customerId
                        );
                    }

                    break;


                // ==========================================
                // EXIT
                // ==========================================

                case 3:

                    System.out.println(
                            "\nThank you for using Secure Blockchain Banking."
                    );

                    running = false;

                    break;


                default:

                    System.out.println(
                            "\nInvalid choice."
                    );
            }
        }

        scanner.close();
    }


    // =====================================================
    // CUSTOMER DASHBOARD
    // =====================================================

    public static void showCustomerDashboard(
            Scanner scanner,
            int customerId) {

        boolean loggedIn = true;

        CustomerAccountService accountService =
                new CustomerAccountService();

        DepositService depositService =
                new DepositService();

        WithdrawService withdrawService =
                new WithdrawService();

        FundTransferService transferService =
                new FundTransferService();

        TransactionHistoryService historyService =
                new TransactionHistoryService();


        while (loggedIn) {

            System.out.println(
                    "\n========================================"
            );

            System.out.println(
                    "          CUSTOMER DASHBOARD"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println("1. View My Accounts");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Fund Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Verify Blockchain Transaction");
            System.out.println("7. Logout");

            System.out.print("\nEnter your choice: ");

            int choice;

            try {

                choice = scanner.nextInt();
                scanner.nextLine();

            } catch (Exception e) {

                System.out.println(
                        "\nInvalid input. Please enter a number."
                );

                scanner.nextLine();
                continue;
            }


            switch (choice) {

                // ==========================================
                // VIEW ACCOUNTS
                // ==========================================

                case 1:

                    accountService.displayCustomerAccounts(
                            customerId
                    );

                    break;


                // ==========================================
                // DEPOSIT
                // ==========================================

                case 2:

                    System.out.println(
                            "\n========== DEPOSIT =========="
                    );

                    System.out.print(
                            "Enter Account Number: "
                    );

                    String depositAccount =
                            scanner.nextLine();

                    if (!accountService.isAccountOwnedByCustomer(
                            customerId,
                            depositAccount)) {

                        System.out.println(
                                "\nAccess denied."
                        );

                        System.out.println(
                                "This account does not belong to the logged-in customer."
                        );

                        break;
                    }

                    System.out.print(
                            "Enter Amount: "
                    );

                    double depositAmount;

                    try {

                        depositAmount =
                                scanner.nextDouble();

                        scanner.nextLine();

                    } catch (Exception e) {

                        System.out.println(
                                "Invalid amount."
                        );

                        scanner.nextLine();

                        break;
                    }

                    depositService.depositMoney(
                            depositAccount,
                            depositAmount
                    );

                    break;


                // ==========================================
                // WITHDRAW
                // ==========================================

                case 3:

                    System.out.println(
                            "\n========== WITHDRAW =========="
                    );

                    System.out.print(
                            "Enter Account Number: "
                    );

                    String withdrawAccount =
                            scanner.nextLine();

                    if (!accountService.isAccountOwnedByCustomer(
                            customerId,
                            withdrawAccount)) {

                        System.out.println(
                                "\nAccess denied."
                        );

                        System.out.println(
                                "This account does not belong to the logged-in customer."
                        );

                        break;
                    }

                    System.out.print(
                            "Enter Amount: "
                    );

                    double withdrawAmount;

                    try {

                        withdrawAmount =
                                scanner.nextDouble();

                        scanner.nextLine();

                    } catch (Exception e) {

                        System.out.println(
                                "Invalid amount."
                        );

                        scanner.nextLine();

                        break;
                    }

                    withdrawService.withdrawMoney(
                            withdrawAccount,
                            withdrawAmount
                    );

                    break;


                // ==========================================
                // FUND TRANSFER
                // ==========================================

                case 4:

                    System.out.println(
                            "\n========== FUND TRANSFER =========="
                    );

                    System.out.print(
                            "Enter Sender Account Number: "
                    );

                    String senderAccount =
                            scanner.nextLine();

                    if (!accountService.isAccountOwnedByCustomer(
                            customerId,
                            senderAccount)) {

                        System.out.println(
                                "\nAccess denied."
                        );

                        System.out.println(
                                "The sender account does not belong to you."
                        );

                        break;
                    }

                    System.out.print(
                            "Enter Receiver Account Number: "
                    );

                    String receiverAccount =
                            scanner.nextLine();

                    if (senderAccount.equals(receiverAccount)) {

                        System.out.println(
                                "\nSender and receiver accounts cannot be the same."
                        );

                        break;
                    }

                    System.out.print(
                            "Enter Amount: "
                    );

                    double transferAmount;

                    try {

                        transferAmount =
                                scanner.nextDouble();

                        scanner.nextLine();

                    } catch (Exception e) {

                        System.out.println(
                                "Invalid amount."
                        );

                        scanner.nextLine();

                        break;
                    }

                    transferService.transferMoney(
                            senderAccount,
                            receiverAccount,
                            transferAmount
                    );

                    break;


                // ==========================================
                // TRANSACTION HISTORY
                // ==========================================

                case 5:

                    historyService.displayTransactionHistory(
                            customerId
                    );

                    break;


                // ==========================================
                // BLOCKCHAIN VERIFICATION
                // ==========================================

                case 6:

                    verifyBlockchainTransaction(
                            scanner,
                            customerId
                    );

                    break;


                // ==========================================
                // LOGOUT
                // ==========================================

                case 7:

                    System.out.println(
                            "\nLogging out..."
                    );

                    loggedIn = false;

                    break;


                default:

                    System.out.println(
                            "\nInvalid choice."
                    );
            }
        }
    }


    // =====================================================
    // BLOCKCHAIN TRANSACTION VERIFICATION
    // =====================================================

    public static void verifyBlockchainTransaction(
            Scanner scanner,
            int customerId) {

        System.out.println(
                "\n=========================================="
        );

        System.out.println(
                "     BLOCKCHAIN TRANSACTION VERIFICATION"
        );

        System.out.println(
                "=========================================="
        );

        System.out.print(
                "Enter Transaction Reference: "
        );

        String transactionReference =
                scanner.nextLine();


        String transactionHash =
                getTransactionHashForCustomer(
                        transactionReference,
                        customerId
                );


        if (transactionHash == null) {

            System.out.println(
                    "\nTransaction not found."
            );

            System.out.println(
                    "Make sure the transaction belongs to your account."
            );

            return;
        }


        System.out.println(
                "\nTransaction found in MySQL."
        );

        System.out.println(
                "Stored SHA-256 Hash:"
        );

        System.out.println(
                transactionHash
        );


        BlockchainTransactionVerifier verifier =
                new BlockchainTransactionVerifier();


        try {

            verifier.verifyTransaction(
                    transactionReference,
                    transactionHash
            );

        } catch (Exception e) {

            System.out.println(
                    "\nBlockchain verification failed."
            );

            e.printStackTrace();

        } finally {

            verifier.close();
        }
    }


    // =====================================================
    // GET TRANSACTION HASH FOR CUSTOMER
    // =====================================================

    private static String getTransactionHashForCustomer(
            String transactionReference,
            int customerId) {

        String sql =
                "SELECT t.transaction_hash " +
                "FROM transactions t " +
                "WHERE t.transaction_reference = ? " +
                "AND (" +
                "t.sender_account IN " +
                "(SELECT account_number " +
                "FROM accounts " +
                "WHERE customer_id = ?) " +
                "OR " +
                "t.receiver_account IN " +
                "(SELECT account_number " +
                "FROM accounts " +
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

            if (resultSet.next()) {

                return resultSet.getString(
                        "transaction_hash"
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to retrieve transaction hash."
            );

            e.printStackTrace();
        }

        return null;
    }
}