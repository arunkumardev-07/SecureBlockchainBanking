import java.util.ArrayList;

public class Bank {

    private ArrayList<Account> accounts;
    private ArrayList<Transaction> transactions;

    public Bank() {
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
        System.out.println("Account added successfully.");
    }

    public Account findAccount(String accountNumber) {

        for (Account account : accounts) {

            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }

        return null;
    }

    public void transferMoney(String senderAccountNumber,
                              String receiverAccountNumber,
                              double amount) {

        Account sender = findAccount(senderAccountNumber);
        Account receiver = findAccount(receiverAccountNumber);

        if (sender == null) {
            System.out.println("Sender account not found.");
            return;
        }

        if (receiver == null) {
            System.out.println("Receiver account not found.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid transfer amount.");
            return;
        }

        if (amount > sender.getBalance()) {
            System.out.println("Insufficient balance.");
            return;
        }

        sender.withdraw(amount);
        receiver.deposit(amount);

        String transactionId = "TXN" + (transactions.size() + 1001);

        Transaction transaction = new Transaction(
                transactionId,
                senderAccountNumber,
                receiverAccountNumber,
                amount,
                "FUND_TRANSFER",
                "SUCCESS"
        );

        transactions.add(transaction);

        System.out.println("Transfer successful.");
        System.out.println("Transaction ID: " + transactionId);
    }

    public void displayAllAccounts() {

        System.out.println("\n===== ALL ACCOUNTS =====");

        for (Account account : accounts) {
            account.displayAccountDetails();
            System.out.println("------------------------");
        }
    }

    public void displayAllTransactions() {

        System.out.println("\n===== TRANSACTION HISTORY =====");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactions) {
            transaction.displayTransactionDetails();
            System.out.println("------------------------");
        }
    }
}