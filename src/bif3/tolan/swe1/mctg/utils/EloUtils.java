package bif3.tolan.swe1.mctg.utils;

/**
 * Class the helps calculate elo after battles
 *
 * @author Christopher Tolan
 */
public class EloUtils {
    /**
     * Calculates new elo of winner and loser based on https://de.wikipedia.org/wiki/Elo-Zahl with the assumption that all users are over 18 years old
     *
     * @param winnerElo         current elo of winning user
     * @param loserElo          current elo of losing user
     * @param winnerGamesPlayed current game count of winning user
     * @param loserGamesPlayed  current game count of losing user
     * @param draw              describes if the game ended in a draw
     * @return Returns the new EloValues for winner and loser
     */
    public static NewEloValues calculateNewElo(int winnerElo, int loserElo, int winnerGamesPlayed, int loserGamesPlayed, boolean draw) {

        double winnerWinningProbability = 1 / (1 + Math.pow(10, (loserElo - winnerElo) / 400));
        double loserWinningProbability = 1 - winnerWinningProbability;

        int k = getKValue(winnerElo, loserElo, winnerGamesPlayed, loserGamesPlayed);

        double winnerPoint = draw ? 0.5 : 1;
        double loserPoint = draw ? 0.5 : 0;

        int newWinnerElo = (int) Math.rint(winnerElo + k * (winnerPoint - winnerWinningProbability));
        int newLoserElo = (int) Math.rint(loserElo + k * (loserPoint - loserWinningProbability));

        return new NewEloValues(newWinnerElo, newLoserElo);
    }

    /**
     * Gets the k-Value based on the users stats that is applied during elo calculation
     *
     * @param winnerElo   current elo of the winning user
     * @param loserElo    current elo of the losing user
     * @param winnerGames current game count of the winning user
     * @param loserGames  current game count of the losing user
     * @return the calculated k value
     */
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

    public record NewEloValues(int winnerElo, int loserElo) {
    }
}
