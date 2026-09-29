public class Main {

    public static void main(String[] args) {

        Bank bank = new Bank();

        Account account1 = new Account("ACC1001", "Arun Kumar");
        Account account2 = new Account("ACC1002", "Praveen Kumar");

        account1.deposit(10000);
        account2.deposit(20000);

        bank.addAccount(account1);
        bank.addAccount(account2);

        System.out.println("\n===== BEFORE TRANSFER =====");
        bank.displayAllAccounts();

        bank.transferMoney("ACC1001", "ACC1002", 2000);

        System.out.println("\n===== AFTER TRANSFER =====");
        bank.displayAllAccounts();

        bank.displayAllTransactions();
    }
}