package com.securebanking;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String generateSHA256(String data) {

        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hashBytes =
                    messageDigest.digest(
                            data.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hash = new StringBuilder();

            for (byte b : hashBytes) {

                String hexadecimal =
                        Integer.toHexString(0xff & b);

                if (hexadecimal.length() == 1) {
                    hash.append('0');
                }

                hash.append(hexadecimal);
            }

            return hash.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(
                    "SHA-256 algorithm not available.",
                    e
            );
        }
    }

    public static void main(String[] args) {

        String data =
                "TXN1001|ACC1001|ACC1002|1000.00";

        String hash = generateSHA256(data);

        System.out.println("Original Data:");
        System.out.println(data);

        System.out.println("\nSHA-256 Hash:");
        System.out.println(hash);
    }
}
