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

    
}
