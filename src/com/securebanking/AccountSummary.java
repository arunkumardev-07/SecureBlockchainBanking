package com.securebanking;

import java.math.BigDecimal;

public class AccountSummary {

    private String accountNumber;

    private String accountType;

    private BigDecimal balance;

    public AccountSummary(
            String accountNumber,
            String accountType,
            BigDecimal balance) {

        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
