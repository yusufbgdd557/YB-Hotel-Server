package dev.yusuf.bookingProject.security.jwt;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtKeyGenerator {

    public static void main(String[] args) {

        String key = generateRandomKey(256);

        System.out.println("Generated JWT Key: " + key);
    }

    private static String generateRandomKey(int keySize) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[keySize / 8];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }


}
