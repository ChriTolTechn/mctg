package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.constants.CommonRegex;
import bif3.tolan.swe1.mcg.model.User;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserUtils {
    public static String extractUsernameFromToken(String token) {
        if (token == null) return "";

        Pattern pattern = Pattern.compile(CommonRegex.TOKEN_READ_REGEX);
        Matcher matcher = pattern.matcher(token);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public static String getUserStats(User user) {
        StringBuilder stringBuilder = new StringBuilder("------- Stats for user " + user.getUsername() + " -------\n");
        stringBuilder.append("Elo:            " + user.getElo() + "\n");
        stringBuilder.append("Games played:   " + user.getGamesPlayed() + "\n");
        stringBuilder.append("Wins:           " + user.getWins() + "\n");
        double winPercentage = calculateWinPercentage(user.getGamesPlayed(), user.getWins());
        stringBuilder.append("Win-Percentage: " + String.format(CommonRegex.WIN_PERCENTAGE_FORMAT_REGEX, winPercentage));
        stringBuilder.append("-----------------------\n");
        return stringBuilder.toString();
    }

    public static String getUserProfile(User user) {
        StringBuilder stringBuilder = new StringBuilder("------- Profile of " + user.getUsername() + " -------\n");
        stringBuilder.append("Username:     " + user.getUsername() + "\n");
        stringBuilder.append("Name:         " + user.getName() + "\n");
        stringBuilder.append("Bio:          " + user.getBio() + "\n");
        stringBuilder.append("Image:        " + user.getImage() + "\n");
        stringBuilder.append("Coins:        " + user.getCoins() + "\n");
        stringBuilder.append("Elo:          " + user.getElo() + "\n");
        stringBuilder.append("Games played: " + user.getGamesPlayed() + "\n");
        stringBuilder.append("Wins:         " + user.getWins() + "\n");
        stringBuilder.append("-----------------------\n");
        return stringBuilder.toString();
    }

    public static String getScoreboard(List<User> users) {
        StringBuilder stringBuilder = new StringBuilder();
        int place = 1;

        stringBuilder.append("------- SCOREBOARD -------\n");
        for (User user : users) {
            stringBuilder.append("#" + place + " ");
            stringBuilder.append("Username: " + user.getUsername() + ", ");
            stringBuilder.append("Elo: " + user.getElo() + ", ");
            stringBuilder.append("Games played: " + user.getGamesPlayed() + ", ");
            stringBuilder.append("Wins: " + user.getWins() + ", ");

            double winPercentage = calculateWinPercentage(user.getGamesPlayed(), user.getWins());

            stringBuilder.append("Win-Percentage: " + String.format(CommonRegex.WIN_PERCENTAGE_FORMAT_REGEX, winPercentage));
            stringBuilder.append("\n");

            place++;
        }
        stringBuilder.append("-----------------------\n");
        return stringBuilder.toString();
    }

    private static double calculateWinPercentage(int gamesPlayed, int gamesWon) {
        double winPercentage = 0;
        if (gamesPlayed > 0)
            winPercentage = (gamesWon * 100.0) / gamesPlayed;
        return winPercentage;
    }
}
