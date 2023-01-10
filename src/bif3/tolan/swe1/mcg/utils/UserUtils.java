package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.constants.CommonRegex;
import bif3.tolan.swe1.mcg.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {
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

    public static double calculateWinPercentage(int gamesPlayed, int gamesWon) {
        double winPercentage = 0;
        if (gamesPlayed > 0)
            winPercentage = (gamesWon * 100.0) / gamesPlayed;
        return winPercentage;
    }

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
