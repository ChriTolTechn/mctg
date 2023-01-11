package bif3.tolan.swe1.mctg.model;

import bif3.tolan.swe1.mctg.model.jsonViews.BattleViews;
import bif3.tolan.swe1.mctg.model.jsonViews.UserViews;
import bif3.tolan.swe1.mctg.utils.BattleUtils;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for battling
 *
 * @author Christopher Tolan
 */
@JsonClassDescription("Battle")
public class Battle {
    @JsonIgnore
    @JsonView(BattleViews.ReadBattle.class)
    private final User user1;
    @JsonIgnore
    private final User user2;
    @JsonProperty("TotalRounds")
    @JsonView(BattleViews.ReadBattle.class)
    private int round;
    @JsonIgnore
    private boolean gameFinished;
    @JsonProperty("Winner")
    @JsonView(UserViews.ReadBattleUser.class)
    private User winner;
    @JsonProperty("Loser")
    @JsonView(UserViews.ReadBattleUser.class)
    private User loser;
    @JsonIgnore
    private ConcurrentHashMap<String, Card> user1Deck;
    @JsonIgnore
    private ConcurrentHashMap<String, Card> user2Deck;
    @JsonProperty("BattleLog")
    @JsonView(BattleViews.ReadBattle.class)
    private Vector<String> battleLog;
    @JsonProperty("IsDraw")
    @JsonView(BattleViews.ReadBattle.class)
    private boolean isDraw;

    public Battle(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.round = 0;
        this.gameFinished = false;
        this.isDraw = false;
        this.battleLog = new Vector<>();
        this.winner = null;
        this.loser = null;

        prepareBattle();
    }

    /**
     * Extracts the users decks and prepares all missing parameters for the battle
     */
    private void prepareBattle() {
        user1Deck = new ConcurrentHashMap<>(user1.getDeck());
        user2Deck = new ConcurrentHashMap<>(user2.getDeck());
    }

    /**
     * Picks a random card from each user's deck and calculates damage for each card.
     * Then the values are passed to a method that concludes the round and logs everything
     * At the end it is checked whether the game has finished or continues
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
     * @param didU1Crit Boolean with the information if card of user 1 crited this round
     * @param didU2Crit Boolean with the information if card of user 2 crited this round
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

        if (didU1Crit)
            logMessage.append(user1Card.getName() + " hit a critical strike!");
        if (didU2Crit)
            logMessage.append(user2Card.getName() + " hit a critical strike!");

        battleLog.add(logMessage.toString());
    }

    public Vector<String> getBattleLog() {
        return battleLog;
    }

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
                isDraw = true;
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
     * Game is set as finished.
     * <p>
     * Note: winner and loser are irrelevant if the game ended in a draw. In this case user 1 is assigned the winner and user 2 the loser position, but not logically
     *
     * @param winner User that won the game
     * @param loser  User that lost the game
     */
    private void concludeGame(User winner, User loser) {
        this.winner = winner;
        this.loser = loser;

        gameFinished = true;
    }

    public User getWinner() {
        return winner;
    }

    public User getLoser() {
        return loser;
    }

    public boolean getIsDraw() {
        return isDraw;
    }
}
