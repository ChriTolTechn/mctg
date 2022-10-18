package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.exceptions.InvalidDeckException;
import bif3.tolan.swe1.mcg.exceptions.InvalidUserException;

import java.util.Set;

public class Battle {

    private final User user1;
    private final User user2;

    private Set<Card> user1Deck;
    private Set<Card> user2Deck;

    private Battle(User user1, User user2) throws InvalidUserException {
        if (user1 == null || user2 == null || user1 == user2)
            throw new InvalidUserException();
        this.user1 = user1;
        this.user2 = user2;
    }

    public void prepareBattle() throws InvalidDeckException {

        user1Deck = user1.getDeck();
        user2Deck = user2.getDeck();

        if (user1Deck == null || user2Deck == null)
            throw new InvalidDeckException();
    }

    public void attack(User user, Card attacks, Card defends) {
        if (user.equals(user1)) {
            return; //TODO
        } else if (user.equals(user2)) {
            return;
        } else {
            return;
        }
    }

    public boolean checkVictoryForUser(User user) throws InvalidUserException {
        if (user.equals(user1)) {
            return user2Deck.isEmpty();
        } else if (user.equals(user2)) {
            return user1Deck.isEmpty();
        } else {
            throw new InvalidUserException();
        }
    }
}
