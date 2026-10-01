package com.securebanking;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

import org.web3j.crypto.Credentials;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;

import org.web3j.tx.RawTransactionManager;

public class BankingTransactionService {

    // Hardhat local blockchain
    private static final String RPC_URL =
            "http://127.0.0.1:8545";

    // Your deployed BankingTransaction contract
    private static final String CONTRACT_ADDRESS =
            "0x5FbDB2315678afecb367f032d93F642f64180aa3";

    /*
     * IMPORTANT:
     * Put your LOCAL Hardhat Account #0 private key here.
     *
     * Do NOT use a real MetaMask/private wallet key.
     * Do NOT upload this file containing the key to GitHub.
     */
    private static final String PRIVATE_KEY =
            "0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efcae784d7bf4f2ff80";

    // Hardhat default local chain ID
    private static final long CHAIN_ID = 31337L;

    private Web3j web3j;
    private Credentials credentials;
    private RawTransactionManager transactionManager;


    // Constructor
    public BankingTransactionService() {

        web3j = Web3j.build(
                new HttpService(RPC_URL)
        );

        credentials = Credentials.create(
                PRIVATE_KEY
        );

        transactionManager =
                new RawTransactionManager(
                        web3j,
                        credentials,
                        CHAIN_ID
                );
    }


    // Display blockchain connection information
    public void displayConnectionDetails()
            throws Exception {

        String clientVersion =
                web3j.web3ClientVersion()
                        .send()
                        .getWeb3ClientVersion();

        System.out.println(
                "Connected to Hardhat blockchain."
        );

        System.out.println(
                "Client: " + clientVersion
        );

        System.out.println(
                "Account: " + credentials.getAddress()
        );

        System.out.println(
                "Contract: " + CONTRACT_ADDRESS
        );
    }


    // Record a transaction hash on the blockchain
    public String recordTransaction(
            String transactionReference,
            String transactionHash)
            throws Exception {

        /*
         * Solidity function:
         *
         * recordTransaction(
         *     string transactionReference,
         *     string transactionHash
         * )
         */

        Function function =
                new Function(
                        "recordTransaction",

                        Arrays.asList(
                                new Utf8String(
                                        transactionReference
                                ),
                                new Utf8String(
                                        transactionHash
                                )
                        ),

                        Collections.emptyList()
                );


        // Convert function call into encoded data
        String encodedFunction =
                FunctionEncoder.encode(function);


        // Gas settings for local Hardhat blockchain
        BigInteger gasPrice =
                BigInteger.valueOf(1_000_000_000L);

        BigInteger gasLimit =
                BigInteger.valueOf(500_000);


        // Send transaction to the smart contract
        EthSendTransaction response =
                transactionManager.sendTransaction(
                        gasPrice,
                        gasLimit,
                        CONTRACT_ADDRESS,
                        encodedFunction,
                        BigInteger.ZERO
                );


        // Check whether transaction was rejected
        if (response.hasError()) {

            throw new RuntimeException(
                    "Blockchain transaction failed: "
                    + response.getError().getMessage()
            );
        }


        String transactionHashOnBlockchain =
                response.getTransactionHash();


        System.out.println(
                "Blockchain transaction sent."
        );

        System.out.println(
                "Transaction Hash: "
                + transactionHashOnBlockchain
        );


        return transactionHashOnBlockchain;
    }


    // Read transaction information from blockchain
    public void getTransaction(
            String transactionReference)
            throws Exception {

        /*
         * Solidity function:
         *
         * getTransaction(
         *     string transactionReference
         * )
         */

        Function function =
                new Function(
                        "getTransaction",

                        Collections.singletonList(
                                new Utf8String(
                                        transactionReference
                                )
                        ),

                        Arrays.asList(
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Utf8String>() {},
                                new TypeReference<Uint256>() {},
                                new TypeReference<Address>() {}
                        )
                );


        String encodedFunction =
                FunctionEncoder.encode(function);


        EthCall response =
                web3j.ethCall(
                        org.web3j.protocol.core.methods.request.Transaction
                                .createEthCallTransaction(
                                        credentials.getAddress(),
                                        CONTRACT_ADDRESS,
                                        encodedFunction
                                ),
                        DefaultBlockParameterName.LATEST
                ).send();


        if (response.hasError()) {

            throw new RuntimeException(
                    "Blockchain read failed: "
                    + response.getError().getMessage()
            );
        }


        String result =
                response.getValue();


        List<Type> decoded =
                FunctionReturnDecoder.decode(
                        result,
                        function.getOutputParameters()
                );


        if (decoded.size() != 4) {

            System.out.println(
                    "Transaction not found on blockchain."
            );

            return;
        }


        String reference =
                decoded.get(0).getValue().toString();

        String storedHash =
                decoded.get(1).getValue().toString();

        BigInteger timestamp =
                (BigInteger) decoded.get(2).getValue();

        String recordedBy =
                decoded.get(3).getValue().toString();


        System.out.println(
                "\n===== BLOCKCHAIN TRANSACTION ====="
        );

        System.out.println(
                "Transaction Reference: "
                + reference
        );

        System.out.println(
                "Stored Transaction Hash: "
                + storedHash
        );

        System.out.println(
                "Blockchain Timestamp: "
                + timestamp
        );

        System.out.println(
                "Recorded By: "
                + recordedBy
        );
    }


    // Close Web3j connection
    public void close() {

        web3j.shutdown();
    }


    public static void main(String[] args) {

        BankingTransactionService service =
                new BankingTransactionService();

        try {

            // Test blockchain connection
            service.displayConnectionDetails();

            /*
             * TEST TRANSACTION
             *
             * This will store the following information
             * on your local blockchain.
             */
            String transactionReference =
                    "TXN1790666728740";

            String transactionHash =
                    "71c00135dff3a605f71d3f5b2573b8d4d46233dcab3e72965b6722803568d62b";


            // Send transaction to Solidity contract
            service.recordTransaction(
                    transactionReference,
                    transactionHash
            );


            // Read it back from blockchain
            service.getTransaction(
                    transactionReference
            );


        } catch (Exception e) {

            System.out.println(
                    "\nBlockchain operation failed."
            );

            e.printStackTrace();

        } finally {

            service.close();
        }
    }
}

