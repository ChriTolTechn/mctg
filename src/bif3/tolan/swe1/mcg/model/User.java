package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.exceptions.CardStackNullException;
import bif3.tolan.swe1.mcg.exceptions.CardsNotInStackException;
import bif3.tolan.swe1.mcg.exceptions.InsufficientFundsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidDeckSizeException;
import bif3.tolan.swe1.mcg.utils.MapUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import static bif3.tolan.swe1.mcg.utils.PasswordHashUtils.hashPassword;

/**
 * Base class for user
 *
 * @author Christopher Tolan
 */
public class User {

    private String username;

    @JsonIgnore
    private int id;
    @JsonIgnore
    private String passwordHash;
    @JsonIgnore
    private ConcurrentHashMap<String, Card> stack;
    @JsonIgnore
    private int elo;
    @JsonIgnore
    private ConcurrentHashMap<String, Card> deck;
    @JsonIgnore
    private int coins;
    @JsonIgnore
    private int gamesPlayed;

    // Constructors


    public User(String username, String passwordHash, int elo, int coins, int gamesPlayed, int id) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.elo = elo;
        this.coins = coins;
        this.gamesPlayed = gamesPlayed;
        this.id = id;
    }

    public User() {
        this.coins = DefaultValues.DEFAULT_USER_BALANCE;
        this.elo = DefaultValues.DEFAULT_ELO;
        this.stack = new ConcurrentHashMap<>();
        this.deck = new ConcurrentHashMap<>();
        this.gamesPlayed = 0;
    }

    // Getter and setter
    public ConcurrentHashMap<String, Card> getStack() {
        return stack;
    }

    public ConcurrentHashMap<String, Card> getDeck() {
        return deck;
    }

    // Sets cards from the stack to the active deck of the user.
    public void setDeck(Vector<String> deck) throws CardsNotInStackException, CardStackNullException, InvalidDeckSizeException {
        assignDeckIfValid(deck);
    }

    public String getUsername() {
        return username;
    }

    @JsonSetter("Username")
    private void setUsername(String username) {
        this.username = username;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getId() {
        return id;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public String getToken() {
        return "Basic " + this.username + "-mtcgToken";
    }

    public int getCoins() {
        return coins;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @JsonSetter("Password")
    private void setPasswordHash(String password) {
        this.passwordHash = hashPassword(password);
    }

    /**
     * Adds cards to the users stack. Throws exception if provided cards are emtpy or null
     *
     * @param newCards cards that should be added to the users stack
     * @throws NullPointerException   if the given cards are null
     * @throws NoSuchElementException if there are no cards to be added
     */
    public void addCardsToStack(List<Card> newCards) throws NullPointerException, NoSuchElementException {
        if (newCards == null) {
            throw new NullPointerException("newCards cannot be null");
        } else if (newCards.isEmpty()) {
            throw new NoSuchElementException("There are no cards found to be added");
        } else {
            for (Card card : newCards) {
                stack.put(card.getCardId(), card);
            }
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
            stack.put(card.getCardId(), card);
        }
    }

    /**
     * Checks if the user has enough money to buy something
     *
     * @param cost given cost
     * @return True if there are sufficient coins
     */
    public boolean canPurchase(int cost) {
        return coins >= cost;
    }

    /**
     * Removes given card from the users deck
     *
     * @param cardId Id of that should be removed
     * @throws NullPointerException     if the given card is null
     * @throws CardsNotInStackException if the card is not found in the stack
     */
    public void removeCardFromStack(String cardId) throws NullPointerException, CardsNotInStackException {
        if (stack.containsKey(cardId)) {
            stack.remove(cardId);
        } else if (cardId == null) {
            throw new NullPointerException("Card cannot be null");
        } else {
            throw new CardsNotInStackException();
        }
    }

    /**
     * Checks if user has a specific card in his stack
     *
     * @param cardId Id for card that is looked after in the users stack
     * @return True if the card is in the stack, False if it is not
     * @throws NullPointerException if the given card is null
     */
    public boolean hasUserCardInStack(String cardId) throws NullPointerException {
        if (cardId == null)
            throw new NullPointerException("Card cannot be null");

        return stack.containsKey(cardId);
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
        if (canPurchase(amount)) {
            this.coins -= amount;
        } else {
            throw new InsufficientFundsException();
        }
    }

    /**
     * Adds all cards in the deck back to the stack and clears the deck
     */
    private void returnCardsFromDeckToStackAndClearDeck() {
        stack.putAll(deck);
        deck.clear();
    }

    /**
     * Checks the validity of a given deck and assigns it if it meets all criterias
     *
     * @param newDeckKeys The ids of the card to be assigned to the deck
     * @throws InvalidDeckSizeException if the deck size is not 4
     * @throws CardStackNullException   if the card stack of the user is null
     * @throws CardsNotInStackException if the cards from the deck are not in the users stack
     * @throws NullPointerException     if the deck is null
     */
    private void assignDeckIfValid(List<String> newDeckKeys) throws InvalidDeckSizeException, NullPointerException, CardStackNullException, CardsNotInStackException {
        if (stack == null) {
            throw new CardStackNullException();
        } else if (newDeckKeys == null) {
            throw new NullPointerException();
        } else if (newDeckKeys.size() != 4) {
            throw new InvalidDeckSizeException();
        } else if (MapUtils.stackContainsAllKeys(stack, newDeckKeys) == false) {
            throw new CardsNotInStackException();
        } else {
            returnCardsFromDeckToStackAndClearDeck();
            for (String key : newDeckKeys) {
                Card card = stack.get(key);
                deck.put(card.getCardId(), card);
                stack.remove(key);
            }
        }
    }
}
