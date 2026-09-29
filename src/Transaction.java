public class Transaction {

    private String transactionId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private double amount;
    private String transactionType;
    private String status;

    public Transaction(String transactionId,
                       String senderAccountNumber,
                       String receiverAccountNumber,
                       double amount,
                       String transactionType,
                       String status) {

        this.transactionId = transactionId;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.status = status;
    }

    public void displayTransactionDetails() {

        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Sender: " + senderAccountNumber);
        System.out.println("Receiver: " + receiverAccountNumber);
        System.out.println("Amount: Rs. " + amount);
        System.out.println("Type: " + transactionType);
        System.out.println("Status: " + status);
    }
}
