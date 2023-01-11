package bif3.tolan.swe1.mctg.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that helps with password hashing
 *
 * @author https://www.baeldung.com/sha-256-hashing-java
 */
public class PasswordHashUtils {
    /**
     * Hashes a string with the SHA-256 hash algorithm
     *
     * @param passwordToHash password to be hashed
     * @return hashed password
     */
    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(passwordToHash.getBytes("UTF-8"));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a byte hash to a hex string
     *
     * @param hash hashed byte array
     * @return hex string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
