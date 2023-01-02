package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DamageMap;
import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.CardsNotInStackException;
import bif3.tolan.swe1.mcg.exceptions.InvalidDeckException;
import bif3.tolan.swe1.mcg.exceptions.InvalidUserException;
import bif3.tolan.swe1.mcg.helper.EloCalculator;

import java.util.Random;
import java.util.Vector;

public class Battle {

    private final User user1;
    private final User user2;

    private int round;

    private boolean gameFinished;

    private Vector<Card> user1Deck;
    private Vector<Card> user2Deck;

    private Vector<String> battleLog;

    private Random random;

    public Battle(User user1, User user2) throws InvalidUserException, InvalidDeckException {
        if (user1 == null || user2 == null || user1 == user2)
            throw new InvalidUserException();
        this.user1 = user1;
        this.user2 = user2;
        this.round = 0;
        this.gameFinished = false;
        this.random = new Random();
        this.battleLog = new Vector<>();

        prepareBattle();
    }

    private void prepareBattle() throws InvalidDeckException {
        user1Deck = new Vector<>(user1.getDeck());
        user2Deck = new Vector<>(user2.getDeck());

        if (user1Deck == null || user2Deck == null)
            throw new InvalidDeckException();
    }

    public void battle() throws CardsNotInStackException {
        if (gameFinished == false) {
            // Advance Round Count
            round++;

            // Choose random card from the users deck
            Card user1Card = user1Deck.get(random.nextInt(user1Deck.size()));
            Card user2Card = user2Deck.get(random.nextInt(user2Deck.size()));

            // Calculate damage dealt for each card
            float damageU1 = calculateDamage(user1Card, user2Card);
            float damageU2 = calculateDamage(user2Card, user1Card);

            // Prepare message for log
            String logMessage = user1.getUsername() + ": " + user1Card.getName() + " ( " + user1Card.getDamage() + " BaseDamage" + " )" + " vs " +
                    user2.getUsername() + ": " + user2Card.getName() + " ( " + user2Card.getDamage() + " BaseDamage" + " )" + " => ";

            // Transfer losing card to the winners deck
            if (damageU1 > damageU2) {
                user2Deck.remove(user2Card);
                user1Deck.add(user2Card);

                logMessage += user1Card.getName() + " ( " + damageU1 + " FinalDamage " + " )" + " defeats " +
                        user2Card.getName() + " ( " + damageU2 + " FinalDamage " + " )";
            } else if (damageU2 > damageU1) {
                user1Deck.remove(user1Card);
                user2Deck.add(user1Card);

                logMessage += user2Card.getName() + " ( " + damageU2 + " FinalDamage" + " )" + " defeats " +
                        user1Card.getName() + " ( " + damageU1 + " FinalDamage" + " )";
            } else {
                // In case of a draw (damageU1 == damageU2) nothing happens
                logMessage += user2Card.getName() + " ( " + damageU2 + " FinalDamage" + " )" + " is in draw with " +
                        user1Card.getName() + " ( " + damageU1 + " FinalDamage" + " )";
            }
            battleLog.add(logMessage);

            checkVictoryForUser();
        } else {
            //Maybe some message?
        }
    }

    public String getBattleLog() {
        return String.join("\n", battleLog);
    }

    public boolean getGameFinished() {
        return gameFinished;
    }


    private void checkVictoryForUser() {
        if (round > 99 || user1Deck.isEmpty() || user2Deck.isEmpty()) {
            if (round > 99) {
                //Draw
                EloCalculator.calculateNewElo(user1, user2, true);
            } else if (user1Deck.isEmpty()) {
                // User 2 wins
                EloCalculator.calculateNewElo(user2, user1);
            } else if (user2Deck.isEmpty()) {
                // User 1 wins
                EloCalculator.calculateNewElo(user1, user2);
            }
            user1.setGamesPlayed(user1.getGamesPlayed() + 1);
            user2.setGamesPlayed(user2.getGamesPlayed() + 1);
            gameFinished = true;
        }
    }

    private float calculateDamage(Card card1, Card card2) {
        float damage = -1;

        // Check if there are special cases in battle
        damage = calculateSpecialCaseDamage(card1, card2);

        // If there was no special case, regular damage calculation is applied
        if (damage < 0) {
            damage = calculateRegularDamage(card1, card2);
        }

        // If some ca
        if (damage == -1) {
            throw new IllegalStateException("Card-State now valid for damage calculation");
        }

        return damage;
    }

    private float calculateRegularDamage(Card card1, Card card2) {
        if (card1.getMonsterType().isInGroup(CardType.CardGroup.Monster)
                && card2.getMonsterType().isInGroup(CardType.CardGroup.Monster)) {
            // Fights between Monster don't affect their damage output
            return card1.getDamage();
        } else if (card1.getMonsterType().isInGroup(CardType.CardGroup.Spell)
                || card2.getMonsterType().isInGroup(CardType.CardGroup.Spell)) {
            // Fights with at least one spell card involved will trigger effectiveness
            float damageMultiplicator = DamageMap.GetDamageMultiplicator(card1.getElement(), card2.getElement());
            return card1.getDamage() * damageMultiplicator;
        }
        return -1;
    }

    private float calculateSpecialCaseDamage(Card card1, Card card2) {
        switch (card1.getMonsterType()) {
            // goblins cant attack dragons
            case Goblin:
                if (card2.getMonsterType() == CardType.Dragon)
                    return 0;
                return -1;
            // orks cant attack wizards
            case Ork:
                if (card2.getMonsterType() == CardType.Wizard)
                    return 0;
                return -1;
            // dragons cant attack elves with a fire type
            case Dragon:
                if (card2.getMonsterType() == CardType.Elf && card2.getElement() == ElementType.FIRE)
                    return 0;
                return -1;
            case Spell:
                // Knights are instant K.O.'d by Water spells
                if (card1.getElement() == ElementType.WATER) {
                    if (card2.getMonsterType() == CardType.Knight)
                        return Float.MAX_VALUE;
                    // Kraken dont get damaged by spell cards
                } else if (card2.getMonsterType() == CardType.Kraken)
                    return 0;
                return -1;
            default:
                return -1;
        }
    }
}
