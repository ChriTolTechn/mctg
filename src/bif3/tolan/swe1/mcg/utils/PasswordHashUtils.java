package bif3.tolan.swe1.mcg.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Static class that helps with password hashing
 *
 * @author Christopher Tolan
 */
public class PasswordHashUtils {

    /**
     * Hashes a string with the SHA-256 hash algorithm
     *
     * @param passwordToHash password to be hashed
     * @return hashed password
     */
    public static String hashPassword(String passwordToHash) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update(passwordToHash.getBytes());
        return new String(messageDigest.digest());
    }

    /**
     * Hashes the entered password and compares it to another hash
     *
     * @param enteredPassword
     * @param passwordHash
     * @return True if the password matches, false if it does not
     */
    public static boolean passwordMatches(String enteredPassword, String passwordHash) {
        return hashPassword(enteredPassword).equals(passwordHash);
    }
}
