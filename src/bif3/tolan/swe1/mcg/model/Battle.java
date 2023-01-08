package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DamageMap;
import bif3.tolan.swe1.mcg.model.enums.CardType;
import bif3.tolan.swe1.mcg.model.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.BattleFinishedException;
import bif3.tolan.swe1.mcg.exceptions.InvalidDeckException;
import bif3.tolan.swe1.mcg.utils.MapUtils;

import java.util.Vector;
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
    private Vector<String> battleLog;
    private boolean draw;

    public Battle(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.round = 0;
        this.gameFinished = false;
        this.draw = false;
        this.battleLog = new Vector<>();
        this.winner = null;
        this.loser = null;

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

        battleLog.add("------- Battle: " + user1.getUsername() + " VS " + user2.getUsername() + "-------");
    }

    /**
     * Picks a random card from each users deck and calculates damage for each card
     * Then the values are passed to a method that concludes the round and logs everything
     * At the end it is checked wether the game has finished or continues
     *
     * @throws BattleFinishedException if the game has ended but the method is still called again
     */
    public void nextRound() {
        if (gameFinished == false) {
            // Advance Round Count
            round++;

            // Choose random card from the users deck
            Card user1Card = MapUtils.getRandomValue(user1Deck);
            Card user2Card = MapUtils.getRandomValue(user2Deck);

            // Calculate damage dealt for each card
            float damageU1 = calculateDamage(user1Card, user2Card);
            float damageU2 = calculateDamage(user2Card, user1Card);

            concludeRound(user1Card, user2Card, damageU1, damageU2);

            checkVictoryForUser();
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
    private void concludeRound(Card user1Card, Card user2Card, float damageU1, float damageU2) {
        // Prepare message for log
        String logMessage = "Round " + round + " - " + user1.getUsername() + "(" + user1Deck.size() + ")" + ": " + user1Card.getName() + " (" + user1Card.getDamage() + " BaseDamage" + ")" + " vs " +
                user2.getUsername() + "(" + user2Deck.size() + ")" + ": " + user2Card.getName() + " (" + user2Card.getDamage() + " BaseDamage" + ")" + " => ";

        // Transfer losing card to the winners deck
        if (damageU1 > damageU2) {
            user2Deck.remove(user2Card.getCardId());
            user1Deck.put(user2Card.getCardId(), user2Card);

            logMessage += user1Card.getName() + " (" + damageU1 + "FinalDamage" + ")" + " defeats " +
                    user2Card.getName() + " (" + damageU2 + " FinalDamage" + ")";
        } else if (damageU2 > damageU1) {
            user1Deck.remove(user1Card.getCardId());
            user2Deck.put(user1Card.getCardId(), user1Card);

            logMessage += user2Card.getName() + " (" + damageU2 + " FinalDamage" + ")" + " defeats " +
                    user1Card.getName() + " (" + damageU1 + " FinalDamage" + ")";
        } else {
            // In case of a draw (damageU1 == damageU2) nothing happens
            logMessage += user2Card.getName() + " (" + damageU2 + " FinalDamage" + ")" + " is in draw with " +
                    user1Card.getName() + " (" + damageU1 + " FinalDamage" + ")";
        }
        battleLog.add(logMessage);
    }

    /**
     * @return Returns the battle log as a string
     */
    public String getBattleLog() {
        return String.join(" \n", battleLog);
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
    private void checkVictoryForUser() {
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

        battleLog.add("--------------");
        if (draw) {
            battleLog.add("Game ended in a draw");
        } else {
            battleLog.add("Winner:" + winner.getUsername());
        }
        battleLog.add("--------------");

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

    /**
     * Calculates damage by checking if any of the special cases are met described in the requirement or calculates it with regular settings
     *
     * @param attacker The attacking card
     * @param defender The defending card
     * @return damage that the attacker deals to the defender as float value
     * @throws IllegalStateException if something went wrong during damage calculation
     */
    private float calculateDamage(Card attacker, Card defender) throws IllegalStateException {
        float damage = -1;

        // Check if there are special cases in battle
        damage = calculateSpecialCaseDamage(attacker, defender);

        // If there was no special case, regular damage calculation is applied
        if (damage == -1) {
            damage = calculateRegularDamage(attacker, defender);
        }

        // Should be unreachable
        if (damage == -1) {
            throw new IllegalStateException("Card-State not valid for damage calculation");
        }

        return damage;
    }

    /**
     * Calculates damage the regular way
     * If both cards are monsters, its just about their base damage
     * If any card is a spell card, the damage is determined by their base damage multiplied with the value the damage map returns based on the defender
     *
     * @param attacker The attacking card
     * @param defender The defending card
     * @return damage that the attacker deals to the defender as float value
     */
    private float calculateRegularDamage(Card attacker, Card defender) {
        if (attacker.getMonsterType().isInGroup(CardType.CardGroup.MONSTER)
                && defender.getMonsterType().isInGroup(CardType.CardGroup.MONSTER)) {
            // Fights between Monster don't affect their damage output
            return attacker.getDamage();
        } else if (attacker.getMonsterType().isInGroup(CardType.CardGroup.SPELL)
                || defender.getMonsterType().isInGroup(CardType.CardGroup.SPELL)) {
            // Fights with at least one spell card involved will trigger effectiveness
            float damageMultiplicator = DamageMap.GetDamageMultiplicator(attacker.getElement(), defender.getElement());
            return attacker.getDamage() * damageMultiplicator;
        }
        // Return should not be reachable in normal cases
        return -1;
    }

    /**
     * Calculate damage based on special cases defined in the requirements
     *
     * @param attacker The attacking card
     * @param defender The defending card
     * @return damage that the attacker deals to the defender as float value
     */
    private float calculateSpecialCaseDamage(Card attacker, Card defender) {
        switch (attacker.getMonsterType()) {
            // goblins cant attack dragons
            case GOBLIN:
                if (defender.getMonsterType() == CardType.DRAGON)
                    return 0;
                return -1;
            // orks cant attack wizards
            case ORK:
                if (defender.getMonsterType() == CardType.WIZARD)
                    return 0;
                return -1;
            // dragons cant attack elves with a fire type
            case DRAGON:
                if (defender.getMonsterType() == CardType.ELF && defender.getElement() == ElementType.FIRE)
                    return 0;
                return -1;
            case SPELL:
                // Knights are instant K.O.'d by Water spells
                if (attacker.getElement() == ElementType.WATER) {
                    if (defender.getMonsterType() == CardType.KNIGHT)
                        return Float.MAX_VALUE;
                    // Kraken dont get damaged by spell cards
                } else if (defender.getMonsterType() == CardType.KRAKEN)
                    return 0;
                return -1;
            default:
                return -1;
        }
    }
}
