# Secure Blockchain-Based Banking Transaction System

A secure banking web application built using Java, Spring Boot, MySQL, JDBC, Solidity, Web3j, and Hardhat.

## Overview

This project combines traditional banking operations with blockchain-based transaction integrity verification.

Customer, account, and transaction information is stored in MySQL. A SHA-256 hash of each banking transaction is recorded on a Solidity smart contract running on a local Hardhat blockchain.

The application can compare the transaction hash stored in MySQL with the hash recorded on the blockchain.

## Features

### Authentication
- Customer login
- Customer registration
- BCrypt password hashing
- Session-based authentication
- Logout

### Account Management
- View customer account
- Account ownership validation
- Balance management

### Banking Operations
- Deposit money
- Withdraw money
- Fund transfer
- Balance validation
- Insufficient balance protection
- Receiver account validation

### Transaction Management
- Transaction reference generation
- Transaction history
- Transaction status
- Transaction amount
- SHA-256 transaction hash

### Blockchain
- Solidity smart contract
- Web3j integration
- Hardhat local blockchain
- Transaction hash recording
- MySQL vs blockchain hash verification

## Technology Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot |
| Frontend | HTML, CSS, Thymeleaf |
| Database | MySQL |
| Database Connectivity | JDBC |
| Password Security | BCrypt |
| Hashing | SHA-256 |
| Smart Contract | Solidity |
| Blockchain Library | Web3j |
| Local Blockchain | Hardhat |
| Development | VS Code |
| Build Tool | Maven |
| Version Control | Git & GitHub |

## Architecture

```text
Browser
   |
   v
Spring Boot Web Application
   |
   v
Java Business Logic
   |
   +------------------+
   |                  |
   v                  v
MySQL              Web3j
Database              |
                      v
              Solidity Smart Contract
                      |
                      v
               Hardhat Blockchain