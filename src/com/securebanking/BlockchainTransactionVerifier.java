package com.securebanking;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;


public class BlockchainTransactionVerifier {

    private static final String RPC_URL =
            "http://127.0.0.1:8545";

    private static final String CONTRACT_ADDRESS =
            "0x5FbDB2315678afecb367f032d93F642f64180aa3";

    private final Web3j web3j;


    // ============================================
    // Constructor
    // ============================================

    public BlockchainTransactionVerifier() {

        web3j = Web3j.build(
                new HttpService(RPC_URL)
        );
    }


    // ============================================
    // Verify Transaction
    // ============================================

    public void verifyTransaction(
            String transactionReference,
            String mysqlHash)
            throws Exception {


        System.out.println(
                "\n======================================"
        );

        System.out.println(
                "BLOCKCHAIN TRANSACTION VERIFICATION"
        );

        System.out.println(
                "======================================"
        );


        // ============================================
        // 1. Create getTransaction() function
        // ============================================

        Function function =
                new Function(
                        "getTransaction",

                        Collections.singletonList(
                                new Utf8String(
                                        transactionReference
                                )
                        ),

                        Arrays.asList(

                                new TypeReference<Utf8String>() {
                                },

                                new TypeReference<Utf8String>() {
                                },

                                new TypeReference<
                                        org.web3j.abi.datatypes.generated.Uint256>() {
                                },

                                new TypeReference<Address>() {
                                }
                        )
                );


        // ============================================
        // 2. Encode Solidity function
        // ============================================

        String encodedFunction =
                FunctionEncoder.encode(function);


        // ============================================
        // 3. Read data from blockchain
        // ============================================

        EthCall response =
                web3j.ethCall(

                        Transaction.createEthCallTransaction(
                                null,
                                CONTRACT_ADDRESS,
                                encodedFunction
                        ),

                        DefaultBlockParameterName.LATEST

                ).send();


        // ============================================
        // 4. Check blockchain response
        // ============================================

        if (response.hasError()) {

            throw new RuntimeException(
                    "Blockchain read failed: "
                    + response.getError().getMessage()
            );
        }


        // ============================================
        // 5. Decode blockchain response
        // ============================================

        List<org.web3j.abi.datatypes.Type> decoded =
                FunctionReturnDecoder.decode(
                        response.getValue(),
                        function.getOutputParameters()
                );


        // ============================================
        // 6. Check whether transaction exists
        // ============================================

        if (decoded.size() != 4) {

            System.out.println(
                    "Transaction not found on blockchain."
            );

            return;
        }


        // ============================================
        // 7. Extract blockchain data
        // ============================================

        String blockchainReference =
                decoded.get(0)
                        .getValue()
                        .toString();


        String blockchainHash =
                decoded.get(1)
                        .getValue()
                        .toString();


        BigInteger timestamp =
                (BigInteger)
                        decoded.get(2)
                                .getValue();


        String recordedBy =
                decoded.get(3)
                        .getValue()
                        .toString();


        // ============================================
        // 8. Display transaction information
        // ============================================

        System.out.println(
                "\nTransaction Reference:"
        );

        System.out.println(
                transactionReference
        );


        System.out.println(
                "\nMySQL SHA-256 Hash:"
        );

        System.out.println(
                mysqlHash
        );


        System.out.println(
                "\nBlockchain Transaction Reference:"
        );

        System.out.println(
                blockchainReference
        );


        System.out.println(
                "\nBlockchain Stored Hash:"
        );

        System.out.println(
                blockchainHash
        );


        System.out.println(
                "\nBlockchain Timestamp:"
        );

        System.out.println(
                timestamp
        );


        System.out.println(
                "\nRecorded By:"
        );

        System.out.println(
                recordedBy
        );


        // ============================================
        // 9. Compare MySQL and Blockchain hashes
        // ============================================

        System.out.println(
                "\n--------------------------------------"
        );


        if (mysqlHash.equals(blockchainHash)) {

            System.out.println(
                    "HASH VERIFICATION SUCCESSFUL"
            );

            System.out.println(
                    "MySQL hash matches blockchain hash."
            );

            System.out.println(
                    "Transaction integrity verified."
            );

        } else {

            System.out.println(
                    "HASH VERIFICATION FAILED"
            );

            System.out.println(
                    "MySQL hash does NOT match blockchain hash."
            );

            System.out.println(
                    "Transaction integrity may have been compromised."
            );
        }


        System.out.println(
                "--------------------------------------"
        );
    }


    // ============================================
    // Close Blockchain Connection
    // ============================================

    public void close() {

        web3j.shutdown();
    }


    // ============================================
    // Main Method
    // ============================================

    public static void main(String[] args) {

        BlockchainTransactionVerifier verifier =
                new BlockchainTransactionVerifier();


        try {

            /*
             * Transaction created by the
             * successful BankDAO test.
             */

            String transactionReference =
                    "TXN1790666728740";


            /*
             * SHA-256 hash generated by BankDAO.
             */

            String mysqlHash =
                    "71c00135dff3a605f71d3f5b2573b8d4d46233dcab3e72965b6722803568d62b";


            verifier.verifyTransaction(
                    transactionReference,
                    mysqlHash
            );


        } catch (Exception e) {

            System.out.println(
                    "\nVerification failed."
            );

            e.printStackTrace();


        } finally {

            verifier.close();
        }
    }
}

