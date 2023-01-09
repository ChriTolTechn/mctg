package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.utils.EloUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EloUtilsTest {
    @Test
    public void testWinner() {
        int winnerElo = 1000;
        int loserElo = 500;
        int winnerGamesPlayed = 10;
        int loserGamesPlayed = 5;
        boolean draw = false;

        EloUtils.NewEloValues newEloValues = EloUtils.calculateNewElo(winnerElo, loserElo, winnerGamesPlayed, loserGamesPlayed, draw);

        Assertions.assertEquals(1004, newEloValues.winnerElo());
        Assertions.assertEquals(496, newEloValues.loserElo());
    }

    @Test
    public void testLoser() {
        int winnerElo = 500;
        int loserElo = 1000;
        int winnerGamesPlayed = 5;
        int loserGamesPlayed = 10;
        boolean draw = false;

        EloUtils.NewEloValues newEloValues = EloUtils.calculateNewElo(winnerElo, loserElo, winnerGamesPlayed, loserGamesPlayed, draw);

        Assertions.assertEquals(536, newEloValues.winnerElo());
        Assertions.assertEquals(964, newEloValues.loserElo());
    }

    @Test
    public void testDraw() {
        int winnerElo = 1000;
        int loserElo = 1000;
        int winnerGamesPlayed = 10;
        int loserGamesPlayed = 10;
        boolean draw = true;

        EloUtils.NewEloValues newEloValues = EloUtils.calculateNewElo(winnerElo, loserElo, winnerGamesPlayed, loserGamesPlayed, draw);

        Assertions.assertEquals(1000, newEloValues.winnerElo());
        Assertions.assertEquals(1000, newEloValues.loserElo());
    }
}
