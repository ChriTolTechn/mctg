package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.constants.CommonRegex;
import bif3.tolan.swe1.mcg.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {
    public static String getUsernameFromToken(String token) {
        if (token == null) return "";

        Pattern pattern = Pattern.compile(CommonRegex.TOKEN_READ_REGEX);
        Matcher matcher = pattern.matcher(token);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public static String getUserStatsAsFormattedString(User user) {
        StringBuilder stringBuilder = new StringBuilder("------- Stats for user " + user.getUsername() + " -------\n");
        stringBuilder.append("Elo:            " + user.getElo() + "\n");
        stringBuilder.append("Games played:   " + user.getGamesPlayed() + "\n");
        stringBuilder.append("Wins:           " + user.getWins() + "\n");

        double winPercentage = 0;
        if (user.getGamesPlayed() > 0)
            winPercentage = (user.getWins() * 100.0) / user.getGamesPlayed();

        stringBuilder.append("Win-Percentage: " + String.format(CommonRegex.WIN_PERCENTAGE_FORMAT_REGEX, winPercentage));
        return stringBuilder.toString();
    }
}
