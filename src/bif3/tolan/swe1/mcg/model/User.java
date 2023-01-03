package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.exceptions.CardStackNullException;
import bif3.tolan.swe1.mcg.exceptions.CardsNotInStackException;
import bif3.tolan.swe1.mcg.exceptions.InsufficientFundsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidDeckSizeException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.*;

import static bif3.tolan.swe1.mcg.helper.PasswordHashHelper.hashPassword;

/**
 * Base class for user
 *
 * @author Christopher Tolan
 */
public class User {

    private String username;
    @JsonIgnore
    private String passwordHash;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private Vector<Card> stack;
    @JsonIgnore
    private int elo;
    @JsonIgnore
    private Vector<Card> deck;
    @JsonIgnore
    private int coins;
    @JsonIgnore
    private int gamesPlayed;

    // Constructors
    public User(String username, String password) {
        this.username = username;
        this.passwordHash = hashPassword(password);
        this.coins = DefaultValues.DEFAULT_USER_BALANCE;
        this.elo = DefaultValues.DEFAULT_ELO;
        this.stack = new Vector<>();
        this.deck = new Vector<>();
        this.gamesPlayed = 0;
    }

    public User() {
        this.coins = DefaultValues.DEFAULT_USER_BALANCE;
        this.elo = DefaultValues.DEFAULT_ELO;
        this.stack = new Vector<>();
        this.deck = new Vector<>();
        this.gamesPlayed = 0;
    }

    // Getter and setter
    public Vector<Card> getStack() {
        return stack;
    }

    public Vector<Card> getDeck() {
        return deck;
    }

    // Sets cards from the stack to the active deck of the user.
    public void setDeck(Vector<Card> deck) throws CardsNotInStackException, CardStackNullException, InvalidDeckSizeException {
        assignDeckIfValid(deck);
    }

    public String getUsername() {
        return username;
    }

    @JsonSetter("Username")
    private void setUsername(String username) {
        this.username = username;
        setToken("Basic " + this.username + "-mtcgToken");
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }

    public int getCoins() {
        return coins;
    }

    /**
     * Adds cards to the users stack. Throws exception if provided cards are emtpy or null
     *
     * @param newCards cards that should be added to the users stack
     * @throws NullPointerException   if the given cards are null
     * @throws NoSuchElementException if there are no cards to be added
     */
    public void addCardsToStack(Set<Card> newCards) throws NullPointerException, NoSuchElementException {
        if (newCards == null) {
            throw new NullPointerException("newCards cannot be null");
        } else if (newCards.isEmpty()) {
            throw new NoSuchElementException("There are no cards found to be added");
        } else {
            stack.addAll(newCards);
        }
    }

    /**
     * Adds a card to the users card stack
     *
     * @param card The card that is added
     * @throws NullPointerException if the given card is null
     */
    public void addCardToStack(Card card) throws NullPointerException {
        if (card == null) {
            throw new NullPointerException("Card cannot be null");
        } else {
            stack.add(card);
        }
    }

    /**
     * Removes given card from the users deck
     *
     * @param card Card that should be removed
     * @throws NullPointerException     if the given card is null
     * @throws CardsNotInStackException if the card is not found in the stack
     */
    public void removeCardFromStack(Card card) throws NullPointerException, CardsNotInStackException {
        if (stack.contains(card)) {
            stack.remove(card);
        } else if (card == null) {
            throw new NullPointerException("Card cannot be null");
        } else {
            throw new CardsNotInStackException();
        }
    }

    /**
     * Checks if user has a specific card in his stack
     *
     * @param card Card that is looked after in the users stack
     * @return True if the card is in the stack, False if it is not
     * @throws NullPointerException if the given card is null
     */
    public boolean hasUserCardInStack(Card card) throws NullPointerException {
        if (card == null)
            throw new NullPointerException("Card cannot be null");

        return stack.contains(card);
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

    /**
     * Subtracts from the users coins
     *
     * @param amount, the amount that is subtracted
     * @throws InsufficientFundsException if the user does not have enough coins
     */
    public void payCoins(int amount) throws InsufficientFundsException {
        if (this.coins - amount >= 0) {
            this.coins -= amount;
            return;
        } else {
            throw new InsufficientFundsException();
        }
    }

    /**
     * Adds all cards in the deck back to the stack and clears the deck
     */
    private void returnCardsFromDeckToStackAndClearDeck() {
        stack.addAll(deck);
        deck.clear();
    }

    @JsonSetter("Password")
    private void setPasswordHash(String password) {
        this.passwordHash = hashPassword(password);
    }

    /**
     * Checks the validity of a given deck and assigns it if it meets all criterias
     *
     * @param deck The deck to be assigned
     * @throws InvalidDeckSizeException if the deck size is not 4
     * @throws CardStackNullException   if the card stack of the user is null
     * @throws CardsNotInStackException if the cards from the deck are not in the users stack
     * @throws NullPointerException     if the deck is null
     */
    private void assignDeckIfValid(List<Card> deck) throws InvalidDeckSizeException, NullPointerException, CardStackNullException, CardsNotInStackException {
        if (stack == null) {
            throw new CardStackNullException();
        } else if (deck == null) {
            throw new NullPointerException();
        } else if (deck.size() != 4) {
            throw new InvalidDeckSizeException();
        } else if (stack.containsAll(deck) == false) {
            throw new CardsNotInStackException();
        } else {
            returnCardsFromDeckToStackAndClearDeck();
            this.deck = new Vector<>(deck);
        }
    }
}
