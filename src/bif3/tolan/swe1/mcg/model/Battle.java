package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.exceptions.InvalidDeckException;
import bif3.tolan.swe1.mcg.utils.BattleUtils;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for battling
 *
 * @author Christopher Tolan
 */
public class Battle {

    private final User user1;
    private final User user2;
    private int round;
    private boolean gameFinished;
    private User winner;
    private User loser;
    private ConcurrentHashMap<String, Card> user1Deck;
    private ConcurrentHashMap<String, Card> user2Deck;
    private StringBuilder battleLog;
    private boolean draw;
    private Random random;

    public Battle(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.round = 0;
        this.gameFinished = false;
        this.draw = false;
        this.battleLog = new StringBuilder();
        this.winner = null;
        this.loser = null;
        this.random = new Random();

        prepareBattle();
    }

    /**
     * Extracts the users decks and prepares all missing parameters for the battle
     *
     * @throws InvalidDeckException if any of the decks is null
     */
    private void prepareBattle() {
        user1Deck = new ConcurrentHashMap<>(user1.getDeck());
        user2Deck = new ConcurrentHashMap<>(user2.getDeck());

        battleLog.append("------- Battle: " + user1.getUsername() + " VS " + user2.getUsername() + "-------\n");
    }

    /**
     * Picks a random card from each users deck and calculates damage for each card
     * Then the values are passed to a method that concludes the round and logs everything
     * At the end it is checked wether the game has finished or continues
     */
    public void nextRound() {
        if (gameFinished == false) {
            // Advance Round Count
            round++;

            // Choose random card from the users deck
            Card user1Card = BattleUtils.getRandomCard(user1Deck);
            Card user2Card = BattleUtils.getRandomCard(user2Deck);

            // Calculate damage dealt for each card
            float damageU1 = BattleUtils.calculateDamage(user1Card, user2Card);
            float damageU2 = BattleUtils.calculateDamage(user2Card, user1Card);

            // Handle critical strikes
            boolean didU1Crit = BattleUtils.didCrit();
            boolean didU2Crit = BattleUtils.didCrit();

            if (didU1Crit)
                damageU1 = BattleUtils.calculateCritDamage(damageU1);
            if (didU2Crit)
                damageU2 = BattleUtils.calculateCritDamage(damageU2);

            concludeRound(user1Card, user2Card, damageU1, damageU2, didU1Crit, didU2Crit);

            checkVictoryAndSetParameters();
        }
    }

    /**
     * Concludes the round by comparing the final damage values of each card
     * Everything is logged
     *
     * @param user1Card Card of user 1
     * @param user2Card Card of user 2
     * @param damageU1  Final damage of userCard1
     * @param damageU2  Final damage of userCard2
     */
    private void concludeRound(Card user1Card, Card user2Card, float damageU1, float damageU2, boolean didU1Crit, boolean didU2Crit) {
        // Prepare message for log
        StringBuilder logMessage = new StringBuilder("Round " + round + " - " + user1.getUsername() + "(" + user1Deck.size() + ")" + ": " + user1Card.getName() + " (" + user1Card.getDamage() + " BaseDamage" + ")" + " vs " +
                user2.getUsername() + "(" + user2Deck.size() + ")" + ": " + user2Card.getName() + " (" + user2Card.getDamage() + " BaseDamage" + ")" + " => ");

        // Transfer losing card to the winners deck
        if (damageU1 > damageU2) {
            user2Deck.remove(user2Card.getCardId());
            user1Deck.put(user2Card.getCardId(), user2Card);

            logMessage.append(user1Card.getName() + " (" + damageU1 + " FinalDamage" + ")" + " defeats " +
                    user2Card.getName() + " (" + damageU2 + " FinalDamage" + ")");
        } else if (damageU2 > damageU1) {
            user1Deck.remove(user1Card.getCardId());
            user2Deck.put(user1Card.getCardId(), user1Card);

            logMessage.append(user2Card.getName() + " (" + damageU2 + " FinalDamage" + ")" + " defeats " +
                    user1Card.getName() + " (" + damageU1 + " FinalDamage" + ")");
        } else {
            // In case of a draw (damageU1 == damageU2) nothing happens
            logMessage.append(user2Card.getName() + " (" + damageU2 + " FinalDamage" + ")" + " is in draw with " +
                    user1Card.getName() + " (" + damageU1 + " FinalDamage" + ")");
        }

        battleLog.append(logMessage + "\n");

        if (didU1Crit)
            battleLog.append(user1Card.getName() + " hit a critical strike!\n");
        if (didU2Crit)
            battleLog.append(user2Card.getName() + " hit a critical strike!\n");
    }

    /**
     * @return Returns the battle log as a string
     */
    public String getBattleLog() {
        return battleLog.toString();
    }

    /**
     * @return Returns the game state
     */
    public boolean getGameFinished() {
        return gameFinished;
    }


    /**
     * Checks if one of the users has already won the game by checking if their decks are empty.
     * A game ends in a draw if the round count is over 99
     */
    private void checkVictoryAndSetParameters() {
        if (round > 99 || user1Deck.isEmpty() || user2Deck.isEmpty()) {
            if (round > 99) {
                //Draw
                winner = user1;
                loser = user2;
                draw = true;
            } else if (user2Deck.isEmpty()) {
                // User 1 wins
                winner = user1;
                loser = user2;
            } else {
                // User 2 wins
                winner = user2;
                loser = user1;
            }

            concludeGame(winner, loser);
        }
    }

    /**
     * The elo of the users gets set if the game ended
     * The gameCount of the users get increased if the game ended
     * Game is set as finished
     *
     * @param winner User that won the game
     */
    private void concludeGame(User winner, User loser) {
        this.winner = winner;
        this.loser = loser;

        battleLog.append("--------------\n");
        if (draw) {
            battleLog.append("Game ended in a draw\n");
        } else {
            battleLog.append("Winner:" + winner.getUsername() + "\n");
        }
        battleLog.append("--------------\n");

        gameFinished = true;
    }

    /**
     * @return The name of the user that has won
     */
    public User getWinner() {
        return winner;
    }

    public User getLoser() {
        return loser;
    }

    public boolean isDraw() {
        return draw;
    }
}
