// SPDX-License-Identifier: MIT

pragma solidity ^0.8.20;

contract BankingTransaction {

    struct TransactionRecord {
        string transactionReference;
        string transactionHash;
        uint256 timestamp;
        address recordedBy;
    }

    mapping(string => TransactionRecord) private transactions;

    event TransactionRecorded(
        string transactionReference,
        string transactionHash,
        uint256 timestamp,
        address recordedBy
    );

    function recordTransaction(
        string memory transactionReference,
        string memory transactionHash
    ) public {

        require(
            bytes(transactionReference).length > 0,
            "Transaction reference is required"
        );

        require(
            bytes(transactionHash).length > 0,
            "Transaction hash is required"
        );

        require(
            bytes(transactions[transactionReference].transactionReference).length == 0,
            "Transaction already recorded"
        );

        transactions[transactionReference] = TransactionRecord(
            transactionReference,
            transactionHash,
            block.timestamp,
            msg.sender
        );

        emit TransactionRecorded(
            transactionReference,
            transactionHash,
            block.timestamp,
            msg.sender
        );
    }

    function getTransaction(
        string memory transactionReference
    )
        public
        view
        returns (
            string memory,
            string memory,
            uint256,
            address
        )
    {
        TransactionRecord memory transaction =
            transactions[transactionReference];

        require(
            bytes(transaction.transactionReference).length > 0,
            "Transaction not found"
        );

        return (
            transaction.transactionReference,
            transaction.transactionHash,
            transaction.timestamp,
            transaction.recordedBy
        );
    }
}