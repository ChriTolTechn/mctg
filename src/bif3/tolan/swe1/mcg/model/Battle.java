package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DamageMap;
import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.InvalidDeckException;
import bif3.tolan.swe1.mcg.exceptions.InvalidUserException;

import java.util.Vector;

public class Battle {

    private final User user1;
    private final User user2;

    private int round;

    private boolean gameFinished;

    private Vector<Card> user1Deck;
    private Vector<Card> user2Deck;

    private Battle(User user1, User user2) throws InvalidUserException {
        if (user1 == null || user2 == null || user1 == user2)
            throw new InvalidUserException();
        this.user1 = user1;
        this.user2 = user2;
        this.round = 0;
        this.gameFinished = false;
    }

    public void prepareBattle() throws InvalidDeckException {
        user1Deck = new Vector<>(user1.getDeck());
        user2Deck = new Vector<>(user2.getDeck());

        if (user1Deck == null || user2Deck == null)
            throw new InvalidDeckException();
    }

    public void battle(Card user1Card, Card user2Card) {
        if (gameFinished == false) {
            round++;

            checkVictoryForUser();
        } else {
            //Maybe some message?
        }
    }

    public void checkVictoryForUser() {
        if (round > 99) {
            // implement draw
        }

        if (user1Deck.isEmpty()) {
            // User 2 wins
        } else if (user2Deck.isEmpty()) {
            // User 1 wins
        }
    }

    private void finishGame(User winner) {
        gameFinished = true;
    }

    public float calculateDamage(Card card1, Card card2) {
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

    public float calculateRegularDamage(Card card1, Card card2) {
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

    public float calculateSpecialCaseDamage(Card card1, Card card2) {
        switch (card1.getMonsterType()) {
            // goblins cant attack dragons
            case Goblin:
                if (card2.getMonsterType() == CardType.Dragon)
                    return 0;
                return -1;
            // orks cant attak wizards
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
