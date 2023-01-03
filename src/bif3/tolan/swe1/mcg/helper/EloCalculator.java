package bif3.tolan.swe1.mcg.helper;

import bif3.tolan.swe1.mcg.model.User;

// Elo System based on https://de.wikipedia.org/wiki/Elo-Zahl
// We assume that all users are over 18 years old
public final class EloCalculator {
    public static void calculateNewElo(User winner, User loser, boolean draw) {
        int winnerElo = winner.getElo();
        int loserElo = loser.getElo();

        int winnerGamesPlayed = winner.getGamesPlayed();
        int loserGamesPlayed = loser.getGamesPlayed();

        double winnerWinningProbability = 1 / (1 + Math.pow(10, (loserElo - winnerElo) / 400));
        double loserWinningProbability = 1 - winnerWinningProbability;

        int k = getKValue(winnerElo, loserElo, winnerGamesPlayed, loserGamesPlayed);

        double winnerPoint = draw ? 0.5 : 1;
        double loserPoint = draw ? 0.5 : 0;

        int newWinnerElo = (int) Math.rint(winnerElo + k * (winnerPoint - winnerWinningProbability));
        int newLoserElo = (int) Math.rint(loserElo + k * (loserPoint - loserWinningProbability));

        winner.setElo(newWinnerElo);
        loser.setElo(newLoserElo);
    }

    public static void calculateNewElo(User winner, User loser) {
        calculateNewElo(winner, loser, false);
    }

    private static int getKValue(int winnerElo, int loserElo, int winnerGames, int loserGames) {
        // K value determined by the stats of the weaker player

        if (winnerGames < 30 || loserGames < 30) {
            // If any of the users has less than 30 registered games
            return 40;
        } else if (winnerElo < 2400 || loserElo < 2400) {
            // If any of the users has less than 2400 elo
            return 20;
        } else {
            // In all other occassions (both users over 2400 elo)
            return 10;
        }
    }
}
