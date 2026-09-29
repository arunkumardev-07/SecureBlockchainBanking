import { buildModule } from "@nomicfoundation/hardhat-ignition/modules";

const BankingTransactionModule = buildModule("BankingTransactionModule", (m) => {

    const bankingTransaction = m.contract("BankingTransaction");

    return {
        bankingTransaction
    };
});

export default BankingTransactionModule;