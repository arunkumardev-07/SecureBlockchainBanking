package com.securebanking;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionSummary {

    private String reference;
    private String sender;
    private String receiver;
    private String type;
    private BigDecimal amount;
    private String status;
    private String hash;
    private Timestamp createdAt;

    public TransactionSummary(
            String reference,
            String sender,
            String receiver,
            String type,
            BigDecimal amount,
            String status,
            String hash,
            Timestamp createdAt) {

        this.reference = reference;
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.amount = amount;
        this.status = status;
        this.hash = hash;
        this.createdAt = createdAt;
    }

    public String getReference() {
        return reference;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getHash() {
        return hash;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
