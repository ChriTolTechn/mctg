package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.exceptions.CardsNotInStackException;
import bif3.tolan.swe1.mcg.exceptions.InsufficientFundsException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Base class for user
 *
 * @author Christopher Tolan
 * @version 0.0
 */
public class User {
    private String username;
    private String passwordHash;
    private String token;
    private Set<Card> stack;
    private int elo;
    private Set<Card> deck;
    private int coins;

    public User(String username, String password) {
        this.username = username;
        this.passwordHash = hashString(password);
        this.coins = DefaultValues.NEW_USER_BALANCE;
        this.elo = DefaultValues.DEFAULT_ELO;
        this.stack = new HashSet<Card>();
        this.deck = new HashSet<Card>();
    }

    public Set<Card> getStack() {
        return stack;
    }

    public Set<Card> getDeck() {
        return deck;
    }

    // Sets cards from the stack to the active deck of the user.
    public void setDeck(Set<Card> cards) throws CardsNotInStackException {
        if (cards != null && cards.size() == 4) {
            if (stack.containsAll(cards)) {
                returnCardsFromDeckToStackAndClearDeck();
                deck = cards;
                return;
            }
        }
        throw new CardsNotInStackException();
    }

    public int getCoins() {
        return coins;
    }

    //Adds cards to the users stack. Throws exception if provided cards are emtpy or null
    public void addCardsToStack(Set<Card> newCards) throws IllegalArgumentException {
        if (newCards != null && !newCards.isEmpty()) {
            stack.addAll(newCards);
            return;
        }
        throw new IllegalArgumentException();
    }

    // Checks if user has a specific card in his stack
    public boolean hasUserCardInStack(Card card) throws IllegalArgumentException {
        if (card == null)
            throw new IllegalArgumentException();

        return stack.contains(card);
    }

    public boolean passwordMatches(String password) {
        return passwordHash.equals(hashString(password));
    }

    // Adds all cards in the deck back to the stack and clears the deck
    private void returnCardsFromDeckToStackAndClearDeck() {
        stack.addAll(deck);
        deck.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    private String hashString(String stringToHash) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update(stringToHash.getBytes());
        return new String(messageDigest.digest());
    }

    public void payForPackages(int cost) throws InsufficientFundsException {
        if (coins - cost >= 0) {
            coins += cost;
            return;
        }
        throw new InsufficientFundsException();
    }
}
