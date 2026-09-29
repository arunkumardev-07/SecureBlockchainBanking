# Secure Blockchain-Based Banking Transaction System

A Java-based banking transaction system that combines **MySQL database management** with **blockchain-based transaction integrity verification**.

The application provides common banking operations such as account management, deposits, withdrawals, fund transfers, and transaction history. For successful financial transactions, a SHA-256 hash is generated and recorded on a Solidity smart contract running on a local Hardhat blockchain. The stored hash can later be retrieved from the blockchain and compared with the MySQL transaction record.

---

## Project Overview

The main objective of this project is to demonstrate how a traditional Java banking application can be integrated with blockchain technology to provide an additional layer for transaction integrity verification.

The application uses:

- **Java** for the application and business logic
- **MySQL** for customer, account, and transaction data
- **JDBC** for Java–MySQL connectivity
- **Solidity** for the blockchain smart contract
- **Hardhat** for the local Ethereum-compatible blockchain
- **Web3j** for Java–blockchain communication
- **SHA-256** for transaction hashing
- **BCrypt** for password hashing
- **Git/GitHub** for version control

---

## Features

### Customer Features

- Customer registration
- Customer login
- BCrypt password verification
- Customer account viewing
- Account ownership validation
- Logout

### Banking Operations

- Deposit money
- Withdraw money
- Fund transfer
- View transaction history
- Account balance checking

### Blockchain Features

- Generate SHA-256 transaction hashes
- Record transaction references and hashes on a Solidity smart contract
- Store blockchain transaction records on a local Hardhat network
- Retrieve transaction records from the blockchain
- Compare MySQL transaction hashes with blockchain hashes
- Verify transaction integrity

### Validation and Security

- Password hashing using BCrypt
- Account ownership validation
- Invalid amount validation
- Insufficient balance validation
- Invalid receiver validation
- Same-account transfer prevention
- Parameterized SQL queries using `PreparedStatement`
- Sensitive configuration through environment variables
- Private blockchain credentials are not stored in source code

---

## System Architecture

```text
                    Java Banking Application
                              |
                              |
                     Business Logic Layer
                              |
                 +------------+------------+
                 |                         |
                 v                         v
              MySQL                  Blockchain Layer
                 |                         |
                 |                         v
                 |                 Solidity Smart Contract
                 |                         |
                 |                         v
                 |                  Hardhat Blockchain
                 |                         |
                 |                         ^
                 |                       Web3j
                 |                         |
                 +------------+------------+
                              |
                              v
                    Transaction Verification