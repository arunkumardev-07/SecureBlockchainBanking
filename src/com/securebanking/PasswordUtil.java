package com.securebanking;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // ============================================
    // Hash Password
    // ============================================

    public static String hashPassword(String password) {

        return BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );
    }


    // ============================================
    // Verify Password
    // ============================================

    public static boolean verifyPassword(
            String password,
            String hashedPassword) {

        return BCrypt.checkpw(
                password,
                hashedPassword
        );
    }


    // ============================================
    // Test
    // ============================================

    public static void main(String[] args) {

        String password = "Test@123";


        // Generate secure password hash
        String hashedPassword =
                hashPassword(password);


        System.out.println(
                "Original Password:"
        );

        System.out.println(
                password
        );


        System.out.println(
                "\nBCrypt Password Hash:"
        );

        System.out.println(
                hashedPassword
        );


        // Test correct password
        boolean correctPassword =
                verifyPassword(
                        "Test@123",
                        hashedPassword
                );


        System.out.println(
                "\nCorrect Password Test:"
        );

        System.out.println(
                correctPassword
        );


        // Test incorrect password
        boolean incorrectPassword =
                verifyPassword(
                        "WrongPassword",
                        hashedPassword
                );


        System.out.println(
                "\nWrong Password Test:"
        );

        System.out.println(
                incorrectPassword
        );
    }
}
