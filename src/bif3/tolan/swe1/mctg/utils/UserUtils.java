package bif3.tolan.swe1.mctg.utils;

import bif3.tolan.swe1.mctg.constants.CommonRegex;
import bif3.tolan.swe1.mctg.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Util card used for users
 *
 * @author Christopher Tolan
 */
public class UserUtils {
    /**
     * Extracts the username from a token received at login
     *
     * @param token token of the user
     * @return username as string
     */
    public static String extractUsernameFromToken(String token) {
        if (token == null) return "";

        Pattern pattern = Pattern.compile(CommonRegex.USERNAME_FROM_TOKEN);
        Matcher matcher = pattern.matcher(token);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    /**
     * Calculates the win percentage based on games played and games won
     *
     * @param gamesPlayed amount of games played
     * @param gamesWon    amount of games won
     * @return winrate in percent
     */
    public static double calculateWinPercentage(int gamesPlayed, int gamesWon) {
        double winPercentage = 0;
        if (gamesPlayed > 0)
            winPercentage = (gamesWon * 100.0) / gamesPlayed;
        return winPercentage;
    }

    /**
     * Checks if a user is valid for saving to the database
     *
     * @param user User to be validated
     * @return True if it is valid
     */
    public static boolean isValidNewUser(User user) {
        if (user == null) return false;
        if (user.getUsername() == null || user.getName() == null || user.getBio() == null || user.getImage() == null)
            return false;
        if (user.getUsername().length() > 50 || user.getName().length() > 50) return false;
        if (user.getUsername().isEmpty()) return false;
        if (user.getName().length() > 255 || user.getBio().length() > 255) return false;

        return true;
    }
}
